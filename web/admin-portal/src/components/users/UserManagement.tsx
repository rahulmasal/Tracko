import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import AddIcon from '@mui/icons-material/Add';
import { getUsers, createUser, updateUser, toggleUserActive, getRoles, getBranches, getShifts, getDepartments } from '../../services/adminService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { useSnackbar } from 'notistack';

export default function UserManagement() {
  const [users, setUsers] = useState<unknown[]>([]);
  const [roles, setRoles] = useState<Array<{ id: number; name: string }>>([]);
  const [branches, setBranches] = useState<Array<{ id: number; name: string }>>([]);
  const [shifts, setShifts] = useState<Array<{ id: number; name: string }>>([]);
  const [departments, setDepartments] = useState<Array<{ id: number; name: string }>>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<Record<string, unknown> | null>(null);
  const [form, setForm] = useState({
    employeeId: '', email: '', phone: '', firstName: '', lastName: '',
    designation: '', password: '', departmentId: 0, branchId: 0, shiftId: 0,
    roleIds: [] as number[], isActive: true,
  });
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [uData, rData, bData, sData, dData] = await Promise.all([
        getUsers({ size: 200 }),
        getRoles(),
        getBranches(),
        getShifts(),
        getDepartments(),
      ]);
      setUsers(uData.content || uData);
      setRoles(rData);
      setBranches(bData);
      setShifts(sData);
      setDepartments(dData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditingUser(null);
    setForm({ employeeId: '', email: '', phone: '', firstName: '', lastName: '', designation: '', password: '', departmentId: 0, branchId: 0, shiftId: 0, roleIds: [], isActive: true });
    setDialogOpen(true);
  };

  const openEdit = (user: Record<string, unknown>) => {
    setEditingUser(user);
    setForm({
      employeeId: user.employeeId as string,
      email: user.email as string,
      phone: (user.phone as string) || '',
      firstName: user.firstName as string,
      lastName: (user.lastName as string) || '',
      designation: (user.designation as string) || '',
      password: '',
      departmentId: (user.departmentId as number) || 0,
      branchId: (user.branchId as number) || 0,
      shiftId: (user.shiftId as number) || 0,
      roleIds: (user.roleIds as number[]) || [],
      isActive: user.isActive as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      if (editingUser) {
        await updateUser(editingUser.id as number, form);
        enqueueSnackbar('User updated', { variant: 'success' });
      } else {
        await createUser(form);
        enqueueSnackbar('User created', { variant: 'success' });
      }
      setDialogOpen(false);
      fetchAll();
    } catch (err) {
      enqueueSnackbar('Operation failed', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const handleToggleActive = async (user: Record<string, unknown>) => {
    try {
      await toggleUserActive(user.id as number);
      enqueueSnackbar('User status toggled', { variant: 'info' });
      fetchAll();
    } catch {
      enqueueSnackbar('Failed to toggle', { variant: 'error' });
    }
  };

  const columnDefs = useMemo(() => [
    { field: 'employeeId', headerName: 'Emp ID', width: 100 },
    { headerName: 'Name', valueGetter: (p: { data: Record<string, unknown> }) => `${p.data.firstName} ${p.data.lastName || ''}`, width: 180 },
    { field: 'email', headerName: 'Email', width: 220 },
    { field: 'phone', headerName: 'Phone', width: 140 },
    { field: 'designation', headerName: 'Designation', width: 150 },
    { headerName: 'Status', field: 'isActive', width: 100, cellRenderer: (p: { value: boolean }) => <StatusChip status={p.value ? 'Active' : 'Inactive'} /> },
    {
      headerName: 'Actions', width: 180,
      cellRenderer: (p: { data: Record<string, unknown> }) => (
        <Stack direction="row" spacing={0.5}>
          <Button size="small" onClick={() => openEdit(p.data)}>Edit</Button>
          <FormControlLabel control={<Switch checked={p.data.isActive as boolean} onChange={() => handleToggleActive(p.data)} size="small" />} label="" />
        </Stack>
      ),
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Button variant="contained" startIcon={<AddIcon />} onClick={openCreate}>Add User</Button>
        <Button onClick={fetchAll}>Refresh</Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={users} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>{editingUser ? 'Edit User' : 'Create User'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <Stack direction="row" spacing={2}>
              <TextField label="Employee ID" size="small" fullWidth value={form.employeeId}
                onChange={(e) => setForm({ ...form, employeeId: e.target.value })} />
              <TextField label="Email" size="small" fullWidth value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })} />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField label="First Name" size="small" fullWidth value={form.firstName}
                onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
              <TextField label="Last Name" size="small" fullWidth value={form.lastName}
                onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField label="Phone" size="small" fullWidth value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })} />
              <TextField label="Designation" size="small" fullWidth value={form.designation}
                onChange={(e) => setForm({ ...form, designation: e.target.value })} />
              {!editingUser && (
                <TextField label="Password" type="password" size="small" fullWidth value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })} />
              )}
            </Stack>
            <Stack direction="row" spacing={2}>
              <FormControl size="small" fullWidth>
                <InputLabel>Department</InputLabel>
                <Select value={form.departmentId} label="Department" onChange={(e) => setForm({ ...form, departmentId: e.target.value as number })}>
                  <MenuItem value={0}>None</MenuItem>
                  {departments.map((d) => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl size="small" fullWidth>
                <InputLabel>Branch</InputLabel>
                <Select value={form.branchId} label="Branch" onChange={(e) => setForm({ ...form, branchId: e.target.value as number })}>
                  <MenuItem value={0}>None</MenuItem>
                  {branches.map((b) => <MenuItem key={b.id} value={b.id}>{b.name}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl size="small" fullWidth>
                <InputLabel>Shift</InputLabel>
                <Select value={form.shiftId} label="Shift" onChange={(e) => setForm({ ...form, shiftId: e.target.value as number })}>
                  <MenuItem value={0}>None</MenuItem>
                  {shifts.map((s) => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
                </Select>
              </FormControl>
            </Stack>
            <FormControl size="small">
              <InputLabel>Roles</InputLabel>
              <Select multiple value={form.roleIds} label="Roles" onChange={(e) => setForm({ ...form, roleIds: e.target.value as number[] })}>
                {roles.map((r) => <MenuItem key={r.id} value={r.id}>{r.name}</MenuItem>)}
              </Select>
            </FormControl>
            <FormControlLabel control={<Switch checked={form.isActive} onChange={(e) => setForm({ ...form, isActive: e.target.checked })} />} label="Active" />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSave} disabled={saving}>
            {saving ? 'Saving...' : editingUser ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
