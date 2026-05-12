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
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import { getQuotations, Quotation } from '../../services/quotationService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDate, formatCurrency } from '../../utils/helpers';
import { format } from 'date-fns';

export default function QuotationList() {
  const navigate = useNavigate();
  const [quotations, setQuotations] = useState<Quotation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [dateRange, setDateRange] = useState({ start: '', end: '' });

  useEffect(() => { fetchQuotations(); }, [statusFilter, dateRange]);

  const fetchQuotations = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { size: 200 };
      if (statusFilter) params.status = statusFilter;
      if (dateRange.start) params.startDate = dateRange.start;
      if (dateRange.end) params.endDate = dateRange.end;
      const data = await getQuotations(params);
      setQuotations(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const columnDefs = useMemo(() => [
    { field: 'quoteNumber', headerName: 'Quote #', width: 130, cellStyle: { fontWeight: 600 } },
    {
      headerName: 'Customer',
      valueGetter: (p: { data: Quotation }) => p.data.customer.companyName,
      width: 200,
    },
    { field: 'subject', headerName: 'Subject', flex: 1, minWidth: 200 },
    {
      headerName: 'Status',
      field: 'status',
      width: 140,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    {
      headerName: 'Total',
      field: 'totalAmount',
      width: 130,
      valueFormatter: (p: { value: number }) => formatCurrency(p.value),
      cellStyle: { fontWeight: 600 },
    },
    {
      headerName: 'Valid Until',
      field: 'validUntil',
      width: 120,
      valueFormatter: (p: { value: string }) => p.value ? formatDate(p.value) : '-',
    },
    {
      headerName: 'Created',
      field: 'createdAt',
      width: 120,
      valueFormatter: (p: { value: string }) => formatDate(p.value),
    },
    {
      headerName: 'Actions',
      width: 120,
      cellRenderer: (p: { data: Quotation }) => (
        <Stack direction="row" spacing={0.5}>
          <Button size="small" onClick={() => navigate(`/quotations?id=${p.data.id}`)}>View</Button>
        </Stack>
      ),
    },
  ], [navigate]);

  return (
    <Card sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Stack direction="row" spacing={2}>
          <FormControl size="small" sx={{ width: 160 }}>
            <InputLabel>Status</InputLabel>
            <Select value={statusFilter} label="Status" onChange={(e) => setStatusFilter(e.target.value)}>
              <MenuItem value="">All</MenuItem>
              <MenuItem value="draft">Draft</MenuItem>
              <MenuItem value="pending_approval">Pending Approval</MenuItem>
              <MenuItem value="approved">Approved</MenuItem>
              <MenuItem value="sent">Sent</MenuItem>
              <MenuItem value="accepted">Accepted</MenuItem>
              <MenuItem value="rejected">Rejected</MenuItem>
              <MenuItem value="expired">Expired</MenuItem>
            </Select>
          </FormControl>
          <TextField type="date" size="small" label="From" value={dateRange.start}
            onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })} InputLabelProps={{ shrink: true }} />
          <TextField type="date" size="small" label="To" value={dateRange.end}
            onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })} InputLabelProps={{ shrink: true }} />
        </Stack>
        <Button variant="contained" onClick={() => navigate('/quotations/new')}>
          New Quotation
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={quotations} columnDefs={columnDefs} height={500} />
      )}
    </Card>
  );
}
