import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import Chip from '@mui/material/Chip';
import Typography from '@mui/material/Typography';
import { getSecurityEvents, resolveSecurityEvent } from '../../services/auditService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDateTime } from '../../../manager-portal/src/utils/helpers';
import { useSnackbar } from 'notistack';

const severityColors: Record<string, string> = {
  low: '#6b7280', medium: '#f59e0b', high: '#ef4444', critical: '#7c3aed',
};

export default function SecurityEventsView() {
  const [events, setEvents] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [severityFilter, setSeverityFilter] = useState('');
  const [resolveDialog, setResolveDialog] = useState<{ open: boolean; event: Record<string, unknown> | null }>({ open: false, event: null });
  const [resolveNotes, setResolveNotes] = useState('');
  const [processing, setProcessing] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchEvents(); }, [severityFilter]);

  const fetchEvents = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { size: 200 };
      if (severityFilter) params.severity = severityFilter;
      const data = await getSecurityEvents(params);
      setEvents(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleResolve = async () => {
    if (!resolveDialog.event) return;
    setProcessing(true);
    try {
      await resolveSecurityEvent(resolveDialog.event.id as number, resolveNotes);
      enqueueSnackbar('Event resolved', { variant: 'success' });
      setResolveDialog({ open: false, event: null });
      setResolveNotes('');
      fetchEvents();
    } catch {
      enqueueSnackbar('Failed to resolve', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const columnDefs = useMemo(() => [
    { headerName: 'Date', field: 'createdAt', width: 170, valueFormatter: (p: { value: string }) => formatDateTime(p.value) },
    { field: 'eventType', headerName: 'Event Type', width: 150 },
    { headerName: 'User', valueGetter: (p: { data: Record<string, unknown> }) => p.data.userEmail || p.data.userId || '-', width: 180 },
    {
      headerName: 'Risk Score', field: 'riskScore', width: 110,
      cellRenderer: (p: { value: number }) => {
        const color = p.value > 80 ? '#ef4444' : p.value > 50 ? '#f59e0b' : '#6b7280';
        return <Chip label={p.value} size="small" sx={{ bgcolor: `${color}20`, color, fontWeight: 600 }} />;
      },
    },
    { headerName: 'Rooted', field: 'isRooted', width: 80, cellRenderer: (p: { value: boolean }) => p.value ? 'Yes' : 'No' },
    { headerName: 'Mock Loc', field: 'isMockLocation', width: 80, cellRenderer: (p: { value: boolean }) => p.value ? 'Yes' : 'No' },
    { headerName: 'Dev Ops', field: 'isDevOptionsEnabled', width: 80, cellRenderer: (p: { value: boolean }) => p.value ? 'Yes' : 'No' },
    { headerName: 'Status', field: 'resolved', width: 100, cellRenderer: (p: { value: boolean }) => <StatusChip status={p.value ? 'Resolved' : 'Unresolved'} /> },
    {
      headerName: 'Actions', width: 120,
      cellRenderer: (p: { data: Record<string, unknown> }) =>
        !p.data.resolved ? (
          <Button size="small" onClick={() => setResolveDialog({ open: true, event: p.data })}>Resolve</Button>
        ) : null,
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }} alignItems="center">
        <FormControl size="small" sx={{ width: 160 }}>
          <InputLabel>Severity</InputLabel>
          <Select value={severityFilter} label="Severity" onChange={(e) => setSeverityFilter(e.target.value)}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="low">Low</MenuItem>
            <MenuItem value="medium">Medium</MenuItem>
            <MenuItem value="high">High</MenuItem>
            <MenuItem value="critical">Critical</MenuItem>
          </Select>
        </FormControl>
        <Button variant="contained" onClick={fetchEvents}>Refresh</Button>
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={events} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={resolveDialog.open} onClose={() => setResolveDialog({ open: false, event: null })}>
        <DialogTitle>Resolve Security Event</DialogTitle>
        <DialogContent>
          <TextField autoFocus margin="dense" label="Resolution Notes" fullWidth multiline rows={3}
            value={resolveNotes} onChange={(e) => setResolveNotes(e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setResolveDialog({ open: false, event: null })}>Cancel</Button>
          <Button variant="contained" onClick={handleResolve} disabled={processing || !resolveNotes}>
            {processing ? 'Resolving...' : 'Resolve'}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
