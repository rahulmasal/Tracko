import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import Chip from '@mui/material/Chip';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import { getLeaveRequests, approveLeave, rejectLeave, LeaveRequest } from '../../services/leaveService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDate, getLeaveTypeLabel, getLeaveTypeColor } from '../../utils/helpers';
import { useSnackbar } from 'notistack';

export default function LeaveApprovals() {
  const [leaves, setLeaves] = useState<LeaveRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogAction, setDialogAction] = useState<'approve' | 'reject' | null>(null);
  const [selectedLeave, setSelectedLeave] = useState<LeaveRequest | null>(null);
  const [remarks, setRemarks] = useState('');
  const [processing, setProcessing] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchPendingLeaves(); }, []);

  const fetchPendingLeaves = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getLeaveRequests({ status: 'pending', size: 100 });
      setLeaves(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async () => {
    if (!selectedLeave || !dialogAction) return;
    setProcessing(true);
    try {
      if (dialogAction === 'approve') {
        await approveLeave(selectedLeave.id, remarks);
        enqueueSnackbar('Leave approved', { variant: 'success' });
      } else {
        await rejectLeave(selectedLeave.id, remarks);
        enqueueSnackbar('Leave rejected', { variant: 'info' });
      }
      setDialogOpen(false);
      setRemarks('');
      fetchPendingLeaves();
    } catch {
      enqueueSnackbar('Action failed', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const columnDefs = useMemo(() => [
    {
      headerName: 'Employee',
      valueGetter: (p: { data: LeaveRequest }) => `${p.data.user.firstName} ${p.data.user.lastName || ''}`,
      width: 180,
    },
    { field: 'user.employeeId', headerName: 'Emp ID', width: 100 },
    {
      headerName: 'Type',
      field: 'leaveType',
      width: 120,
      cellRenderer: (p: { value: string }) => (
        <Chip label={getLeaveTypeLabel(p.value)} size="small"
          sx={{ bgcolor: `${getLeaveTypeColor(p.value)}20`, color: getLeaveTypeColor(p.value), fontWeight: 600 }} />
      ),
    },
    { field: 'startDate', headerName: 'From', width: 110, valueFormatter: (p: { value: string }) => formatDate(p.value) },
    { field: 'endDate', headerName: 'To', width: 110, valueFormatter: (p: { value: string }) => formatDate(p.value) },
    { field: 'totalDays', headerName: 'Days', width: 70 },
    { field: 'reason', headerName: 'Reason', flex: 1, minWidth: 150 },
    {
      headerName: 'Status',
      field: 'status',
      width: 110,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    {
      headerName: 'Actions',
      width: 140,
      cellRenderer: (p: { data: LeaveRequest }) =>
        p.data.status === 'pending' ? (
          <Stack direction="row" spacing={0.5}>
            <Button size="small" color="success" onClick={() => { setSelectedLeave(p.data); setDialogAction('approve'); setDialogOpen(true); }}>
              <CheckCircleIcon fontSize="small" />
            </Button>
            <Button size="small" color="error" onClick={() => { setSelectedLeave(p.data); setDialogAction('reject'); setDialogOpen(true); }}>
              <CancelIcon fontSize="small" />
            </Button>
          </Stack>
        ) : null,
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={leaves} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{dialogAction === 'approve' ? 'Approve Leave' : 'Reject Leave'}</DialogTitle>
        <DialogContent>
          <TextField autoFocus margin="dense" label="Remarks" fullWidth multiline rows={3}
            value={remarks} onChange={(e) => setRemarks(e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" color={dialogAction === 'approve' ? 'success' : 'error'}
            onClick={handleAction} disabled={processing || !remarks}>
            {processing ? 'Processing...' : dialogAction === 'approve' ? 'Approve' : 'Reject'}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
