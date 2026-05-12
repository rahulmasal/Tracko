import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import TextField from '@mui/material/TextField';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Collapse from '@mui/material/Collapse';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { getAuditLogs } from '../../services/auditService';
import DataTable from '../shared/DataTable';
import { formatDateTime } from '../../../manager-portal/src/utils/helpers';
import { format } from 'date-fns';

export default function AuditLogViewer() {
  const [logs, setLogs] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState({ action: '', entityType: '', startDate: '', endDate: '' });

  useEffect(() => { fetchLogs(); }, []);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { size: 200, ...filters };
      Object.entries(params).forEach(([k, v]) => { if (!v) delete params[k]; });
      const data = await getAuditLogs(params);
      setLogs(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const columnDefs = useMemo(() => [
    { headerName: 'Date', field: 'createdAt', width: 170, valueFormatter: (p: { value: string }) => formatDateTime(p.value) },
    { field: 'action', headerName: 'Action', width: 120 },
    { field: 'entityType', headerName: 'Entity', width: 120 },
    { field: 'entityId', headerName: 'Entity ID', width: 90 },
    { headerName: 'User', valueGetter: (p: { data: Record<string, unknown> }) => p.data.userEmail || p.data.userId || '-', width: 200 },
    { field: 'ipAddress', headerName: 'IP', width: 140 },
    {
      headerName: 'Changes', width: 50,
      cellRenderer: (p: { data: Record<string, unknown> }) => {
        const [open, setOpen] = useState(false);
        const hasChanges = !!p.data.changes;
        return (
          <Box>
            <IconButton size="small" disabled={!hasChanges} onClick={() => setOpen(!open)}>
              {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
            </IconButton>
            <Collapse in={open}>
              <Box sx={{ p: 1, bgcolor: '#f8fafc', borderRadius: 1, maxHeight: 300, overflow: 'auto' }}>
                <Typography variant="caption" component="pre" sx={{ whiteSpace: 'pre-wrap', fontSize: 10 }}>
                  {JSON.stringify(p.data.changes, null, 2)}
                </Typography>
              </Box>
            </Collapse>
          </Box>
        );
      },
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }} alignItems="center">
        <TextField label="Action" size="small" value={filters.action}
          onChange={(e) => setFilters({ ...filters, action: e.target.value })} sx={{ width: 140 }} />
        <TextField label="Entity" size="small" value={filters.entityType}
          onChange={(e) => setFilters({ ...filters, entityType: e.target.value })} sx={{ width: 140 }} />
        <TextField type="date" size="small" label="From" value={filters.startDate}
          onChange={(e) => setFilters({ ...filters, startDate: e.target.value })} InputLabelProps={{ shrink: true }} />
        <TextField type="date" size="small" label="To" value={filters.endDate}
          onChange={(e) => setFilters({ ...filters, endDate: e.target.value })} InputLabelProps={{ shrink: true }} />
        <Button variant="contained" onClick={fetchLogs}>Search</Button>
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={logs} columnDefs={columnDefs} height={500} />
      )}
    </Card>
  );
}
