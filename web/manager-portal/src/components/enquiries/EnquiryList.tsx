import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import TextField from '@mui/material/TextField';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import { getEnquiries, updateEnquiryStatus, assignEnquiry, Enquiry } from '../../services/enquiryService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDate, calculateAge, getPriorityColor } from '../../utils/helpers';
import { ENQUIRY_PRIORITY_COLORS } from '../../utils/constants';
import { useSnackbar } from 'notistack';

export default function EnquiryList() {
  const navigate = useNavigate();
  const [enquiries, setEnquiries] = useState<Enquiry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [statusDialog, setStatusDialog] = useState<{ open: boolean; enquiry: Enquiry | null }>({ open: false, enquiry: null });
  const [newStatus, setNewStatus] = useState('');
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    fetchEnquiries();
  }, [statusFilter, priorityFilter]);

  const fetchEnquiries = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { size: 200 };
      if (statusFilter) params.status = statusFilter;
      if (priorityFilter) params.priority = priorityFilter;
      const data = await getEnquiries(params);
      setEnquiries(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async () => {
    if (!statusDialog.enquiry || !newStatus) return;
    try {
      await updateEnquiryStatus(statusDialog.enquiry.id, newStatus);
      enqueueSnackbar('Status updated', { variant: 'success' });
      setStatusDialog({ open: false, enquiry: null });
      fetchEnquiries();
    } catch {
      enqueueSnackbar('Failed to update', { variant: 'error' });
    }
  };

  const columnDefs = useMemo(() => [
    {
      headerName: 'Subject',
      field: 'subject',
      width: 250,
      cellRenderer: (p: { value: string; data: Enquiry }) => (
        <Box>
          <span style={{ fontWeight: 500 }}>{p.value}</span>
          <Chip
            label={p.data.priority}
            size="small"
            sx={{
              ml: 1,
              bgcolor: `${ENQUIRY_PRIORITY_COLORS[p.data.priority] || '#6b7280'}20`,
              color: ENQUIRY_PRIORITY_COLORS[p.data.priority] || '#6b7280',
              fontSize: 10,
              height: 20,
            }}
          />
        </Box>
      ),
    },
    {
      headerName: 'Customer',
      valueGetter: (p: { data: Enquiry }) => p.data.customer.companyName,
      width: 180,
    },
    {
      headerName: 'Assigned To',
      valueGetter: (p: { data: Enquiry }) =>
        p.data.assignedToUser ? `${p.data.assignedToUser.firstName} ${p.data.assignedToUser.lastName || ''}` : 'Unassigned',
      width: 160,
    },
    {
      headerName: 'Status',
      field: 'status',
      width: 120,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    {
      headerName: 'Created',
      field: 'createdAt',
      width: 120,
      valueFormatter: (p: { value: string }) => formatDate(p.value),
    },
    {
      headerName: 'Age',
      valueGetter: (p: { data: Enquiry }) => {
        const days = Math.floor((Date.now() - new Date(p.data.createdAt).getTime()) / (1000 * 86400));
        return calculateAge(days);
      },
      width: 80,
    },
    {
      headerName: 'Actions',
      width: 180,
      cellRenderer: (p: { data: Enquiry }) => (
        <Stack direction="row" spacing={0.5}>
          <Button size="small" onClick={() => navigate(`/enquiries?id=${p.data.id}`)}>View</Button>
          <Button
            size="small"
            onClick={() => setStatusDialog({ open: true, enquiry: p.data })}
          >
            Change Status
          </Button>
        </Stack>
      ),
    },
  ], [navigate]);

  return (
    <Card sx={{ p: 2 }}>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }} alignItems="center">
        <FormControl size="small" sx={{ width: 160 }}>
          <InputLabel>Status</InputLabel>
          <Select value={statusFilter} label="Status" onChange={(e) => setStatusFilter(e.target.value)}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="new">New</MenuItem>
            <MenuItem value="contacted">Contacted</MenuItem>
            <MenuItem value="quoted">Quoted</MenuItem>
            <MenuItem value="negotiation">Negotiation</MenuItem>
            <MenuItem value="won">Won</MenuItem>
            <MenuItem value="lost">Lost</MenuItem>
          </Select>
        </FormControl>
        <FormControl size="small" sx={{ width: 140 }}>
          <InputLabel>Priority</InputLabel>
          <Select value={priorityFilter} label="Priority" onChange={(e) => setPriorityFilter(e.target.value)}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="low">Low</MenuItem>
            <MenuItem value="medium">Medium</MenuItem>
            <MenuItem value="high">High</MenuItem>
            <MenuItem value="urgent">Urgent</MenuItem>
          </Select>
        </FormControl>
        <Box sx={{ flex: 1 }} />
        <Button variant="contained" onClick={fetchEnquiries}>Refresh</Button>
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={enquiries} columnDefs={columnDefs} height={500} />
      )}

      <Dialog open={statusDialog.open} onClose={() => setStatusDialog({ open: false, enquiry: null })}>
        <DialogTitle>Change Status</DialogTitle>
        <DialogContent>
          <FormControl fullWidth size="small" sx={{ mt: 1 }}>
            <InputLabel>New Status</InputLabel>
            <Select value={newStatus} label="New Status" onChange={(e) => setNewStatus(e.target.value)}>
              <MenuItem value="new">New</MenuItem>
              <MenuItem value="contacted">Contacted</MenuItem>
              <MenuItem value="quoted">Quoted</MenuItem>
              <MenuItem value="negotiation">Negotiation</MenuItem>
              <MenuItem value="won">Won</MenuItem>
              <MenuItem value="lost">Lost</MenuItem>
              <MenuItem value="on_hold">On Hold</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStatusDialog({ open: false, enquiry: null })}>Cancel</Button>
          <Button variant="contained" onClick={handleStatusChange} disabled={!newStatus}>Update</Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
