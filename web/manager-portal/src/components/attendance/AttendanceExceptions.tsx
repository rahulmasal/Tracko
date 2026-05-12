import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Stack from '@mui/material/Stack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import {
  getCorrections,
  approveCorrection,
  rejectCorrection,
  getMissedCheckins,
  AttendanceCorrection,
} from '../../services/attendanceService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDate } from '../../utils/helpers';
import { useSnackbar } from 'notistack';

export default function AttendanceExceptions() {
  const [tab, setTab] = useState(0);
  const [corrections, setCorrections] = useState<AttendanceCorrection[]>([]);
  const [missedCheckins, setMissedCheckins] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogAction, setDialogAction] = useState<'approve' | 'reject' | null>(null);
  const [selectedCorrection, setSelectedCorrection] = useState<AttendanceCorrection | null>(null);
  const [remarks, setRemarks] = useState('');
  const [processing, setProcessing] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    if (tab === 0) fetchCorrections();
    else fetchMissedCheckins();
  }, [tab]);

  const fetchCorrections = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getCorrections({ status: 'pending', size: 100 });
      setCorrections(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const fetchMissedCheckins = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMissedCheckins({});
      setMissedCheckins(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async () => {
    if (!selectedCorrection || !dialogAction) return;
    setProcessing(true);
    try {
      if (dialogAction === 'approve') {
        await approveCorrection(selectedCorrection.id, remarks);
        enqueueSnackbar('Correction approved', { variant: 'success' });
      } else {
        await rejectCorrection(selectedCorrection.id, remarks);
        enqueueSnackbar('Correction rejected', { variant: 'info' });
      }
      setDialogOpen(false);
      setRemarks('');
      fetchCorrections();
    } catch (err) {
      enqueueSnackbar('Action failed', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const correctionCols = useMemo(() => [
    {
      headerName: 'Employee',
      valueGetter: (p: { data: AttendanceCorrection }) =>
        `${p.data.user.firstName} ${p.data.user.lastName || ''} (${p.data.user.employeeId})`,
      width: 200,
    },
    { field: 'correctionType', headerName: 'Type', width: 140 },
    { field: 'requestedDate', headerName: 'Date', width: 120, valueFormatter: (p: { value: string }) => formatDate(p.value) },
    { field: 'reason', headerName: 'Reason', flex: 1, minWidth: 200 },
    {
      headerName: 'Status',
      field: 'status',
      width: 110,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    {
      headerName: 'Actions',
      width: 160,
      cellRenderer: (p: { data: AttendanceCorrection }) =>
        p.data.status === 'pending' ? (
          <Stack direction="row" spacing={0.5}>
            <Button
              size="small"
              color="success"
              onClick={() => { setSelectedCorrection(p.data); setDialogAction('approve'); setDialogOpen(true); }}
            >
              <CheckCircleIcon fontSize="small" />
            </Button>
            <Button
              size="small"
              color="error"
              onClick={() => { setSelectedCorrection(p.data); setDialogAction('reject'); setDialogOpen(true); }}
            >
              <CancelIcon fontSize="small" />
            </Button>
          </Stack>
        ) : null,
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label="Correction Requests" />
        <Tab label="Missed Check-ins" />
      </Tabs>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : tab === 0 ? (
        <DataTable rowData={corrections} columnDefs={correctionCols} height={400} />
      ) : (
        <Box>
          <Typography variant="body2" color="text.secondary">
            {missedCheckins.length} missed check-ins today
          </Typography>
          {missedCheckins.map((mc: unknown) => (
            <Box key={(mc as Record<string, unknown>).id as string} sx={{ py: 1, borderBottom: '1px solid', borderColor: 'divider' }}>
              <Typography variant="body2">{(mc as Record<string, unknown>).userName as string}</Typography>
            </Box>
          ))}
        </Box>
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {dialogAction === 'approve' ? 'Approve Correction' : 'Reject Correction'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Remarks"
            fullWidth
            multiline
            rows={3}
            value={remarks}
            onChange={(e) => setRemarks(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            color={dialogAction === 'approve' ? 'success' : 'error'}
            onClick={handleAction}
            disabled={processing || !remarks}
          >
            {processing ? 'Processing...' : dialogAction === 'approve' ? 'Approve' : 'Reject'}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
