import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import AddIcon from '@mui/icons-material/Add';
import { getBranches, createBranch, updateBranch } from '../../services/adminService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { useSnackbar } from 'notistack';

export default function BranchManagement() {
  const [branches, setBranches] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Record<string, unknown> | null>(null);
  const [form, setForm] = useState({ name: '', code: '', address: '', city: '', state: '', pincode: '', latitude: 0, longitude: 0, radiusMeters: 100 });
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchBranches(); }, []);

  const fetchBranches = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getBranches({ size: 100 });
      setBranches(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditing(null);
    setForm({ name: '', code: '', address: '', city: '', state: '', pincode: '', latitude: 0, longitude: 0, radiusMeters: 100 });
    setDialogOpen(true);
  };

  const openEdit = (branch: Record<string, unknown>) => {
    setEditing(branch);
    setForm({
      name: branch.name as string, code: branch.code as string,
      address: (branch.address as string) || '', city: (branch.city as string) || '',
      state: (branch.state as string) || '', pincode: (branch.pincode as string) || '',
      latitude: (branch.latitude as number) || 0, longitude: (branch.longitude as number) || 0,
      radiusMeters: (branch.radiusMeters as number) || 100,
    });
    setDialogOpen(true);
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      if (editing) {
        await updateBranch(editing.id as number, form);
        enqueueSnackbar('Branch updated', { variant: 'success' });
      } else {
        await createBranch(form);
        enqueueSnackbar('Branch created', { variant: 'success' });
      }
      setDialogOpen(false);
      fetchBranches();
    } catch {
      enqueueSnackbar('Operation failed', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const columnDefs = useMemo(() => [
    { field: 'code', headerName: 'Code', width: 80 },
    { field: 'name', headerName: 'Name', width: 180 },
    { field: 'city', headerName: 'City', width: 120 },
    { field: 'state', headerName: 'State', width: 120 },
    { field: 'address', headerName: 'Address', flex: 1, minWidth: 200 },
    { headerName: 'Status', field: 'isActive', width: 100, cellRenderer: (p: { value: boolean }) => <StatusChip status={p.value ? 'Active' : 'Inactive'} /> },
    { headerName: 'Actions', width: 100, cellRenderer: (p: { data: Record<string, unknown> }) => <Button size="small" onClick={() => openEdit(p.data)}>Edit</Button> },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Button variant="contained" startIcon={<AddIcon />} onClick={openCreate}>Add Branch</Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={branches} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editing ? 'Edit Branch' : 'Add Branch'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <Stack direction="row" spacing={2}>
              <TextField label="Name" size="small" fullWidth value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
              <TextField label="Code" size="small" fullWidth value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} />
            </Stack>
            <TextField label="Address" size="small" fullWidth value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
            <Stack direction="row" spacing={2}>
              <TextField label="City" size="small" fullWidth value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
              <TextField label="State" size="small" fullWidth value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} />
              <TextField label="Pincode" size="small" fullWidth value={form.pincode} onChange={(e) => setForm({ ...form, pincode: e.target.value })} />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField label="Latitude" type="number" size="small" fullWidth value={form.latitude}
                onChange={(e) => setForm({ ...form, latitude: parseFloat(e.target.value) || 0 })} />
              <TextField label="Longitude" type="number" size="small" fullWidth value={form.longitude}
                onChange={(e) => setForm({ ...form, longitude: parseFloat(e.target.value) || 0 })} />
              <TextField label="Radius (m)" type="number" size="small" fullWidth value={form.radiusMeters}
                onChange={(e) => setForm({ ...form, radiusMeters: parseInt(e.target.value) || 100 })} />
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
