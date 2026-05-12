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
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import AddIcon from '@mui/icons-material/Add';
import { getShifts, createShift, updateShift } from '../../services/adminService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { useSnackbar } from 'notistack';

export default function ShiftManagement() {
  const [shifts, setShifts] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Record<string, unknown> | null>(null);
  const [form, setForm] = useState({ name: '', startTime: '09:00', endTime: '18:00', graceMinutes: 15, halfDayMinutes: 240, isNightShift: false, description: '' });
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchShifts(); }, []);

  const fetchShifts = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getShifts();
      setShifts(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditing(null);
    setForm({ name: '', startTime: '09:00', endTime: '18:00', graceMinutes: 15, halfDayMinutes: 240, isNightShift: false, description: '' });
    setDialogOpen(true);
  };

  const openEdit = (shift: Record<string, unknown>) => {
    setEditing(shift);
    setForm({
      name: shift.name as string, startTime: shift.startTime as string, endTime: shift.endTime as string,
      graceMinutes: (shift.graceMinutes as number) || 15, halfDayMinutes: (shift.halfDayMinutes as number) || 240,
      isNightShift: (shift.isNightShift as boolean) || false, description: (shift.description as string) || '',
    });
    setDialogOpen(true);
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      if (editing) {
        await updateShift(editing.id as number, form);
        enqueueSnackbar('Shift updated', { variant: 'success' });
      } else {
        await createShift(form);
        enqueueSnackbar('Shift created', { variant: 'success' });
      }
      setDialogOpen(false);
      fetchShifts();
    } catch {
      enqueueSnackbar('Operation failed', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const columnDefs = useMemo(() => [
    { field: 'name', headerName: 'Name', width: 150 },
    { field: 'startTime', headerName: 'Start', width: 100 },
    { field: 'endTime', headerName: 'End', width: 100 },
    { field: 'graceMinutes', headerName: 'Grace (min)', width: 110 },
    { field: 'halfDayMinutes', headerName: 'Half Day (min)', width: 130 },
    { headerName: 'Night Shift', field: 'isNightShift', width: 110, cellRenderer: (p: { value: boolean }) => p.value ? 'Yes' : 'No' },
    { headerName: 'Status', field: 'isActive', width: 100, cellRenderer: (p: { value: boolean }) => <StatusChip status={p.value ? 'Active' : 'Inactive'} /> },
    { headerName: 'Actions', width: 100, cellRenderer: (p: { data: Record<string, unknown> }) => <Button size="small" onClick={() => openEdit(p.data)}>Edit</Button> },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Button variant="contained" startIcon={<AddIcon />} onClick={openCreate}>Add Shift</Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={shifts} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editing ? 'Edit Shift' : 'Add Shift'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField label="Name" size="small" fullWidth value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            <Stack direction="row" spacing={2}>
              <TextField label="Start Time" type="time" size="small" fullWidth value={form.startTime}
                onChange={(e) => setForm({ ...form, startTime: e.target.value })} InputLabelProps={{ shrink: true }} />
              <TextField label="End Time" type="time" size="small" fullWidth value={form.endTime}
                onChange={(e) => setForm({ ...form, endTime: e.target.value })} InputLabelProps={{ shrink: true }} />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField label="Grace (min)" type="number" size="small" fullWidth value={form.graceMinutes}
                onChange={(e) => setForm({ ...form, graceMinutes: parseInt(e.target.value) || 0 })} />
              <TextField label="Half Day (min)" type="number" size="small" fullWidth value={form.halfDayMinutes}
                onChange={(e) => setForm({ ...form, halfDayMinutes: parseInt(e.target.value) || 0 })} />
            </Stack>
            <TextField label="Description" size="small" fullWidth value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })} />
            <FormControlLabel control={<Switch checked={form.isNightShift} onChange={(e) => setForm({ ...form, isNightShift: e.target.checked })} />} label="Night Shift" />
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
