import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import DownloadIcon from '@mui/icons-material/Download';
import { format } from 'date-fns';
import { getAttendance, AttendanceRecord } from '../../services/attendanceService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDateTime, calculateDuration } from '../../utils/helpers';
import * as XLSX from 'xlsx';

export default function TeamAttendanceView() {
  const [records, setRecords] = useState<AttendanceRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [date, setDate] = useState(format(new Date(), 'yyyy-MM-dd'));
  const [statusFilter, setStatusFilter] = useState('');

  useEffect(() => {
    fetchAttendance();
  }, [date, statusFilter]);

  const fetchAttendance = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { date, size: 200 };
      if (statusFilter) params.status = statusFilter;
      const data = await getAttendance(params);
      setRecords(data.content || data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleExport = () => {
    const ws = XLSX.utils.json_to_sheet(records.map((r) => ({
      Employee: `${r.user.firstName} ${r.user.lastName || ''}`,
      ID: r.user.employeeId,
      Date: r.date,
      'Check In': r.checkInTime ? formatDateTime(r.checkInTime) : '-',
      'Check Out': r.checkOutTime ? formatDateTime(r.checkOutTime) : '-',
      Status: r.status,
      'Late (min)': r.lateMinutes,
      'Work Hours': r.workHours ? `${r.workHours}h` : '-',
    })));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Attendance');
    XLSX.writeFile(wb, `attendance_${date}.xlsx`);
  };

  const columnDefs = useMemo(() => [
    { field: 'user.employeeId', headerName: 'Emp ID', width: 100 },
    {
      headerName: 'Employee',
      valueGetter: (p: { data: AttendanceRecord }) => `${p.data.user.firstName} ${p.data.user.lastName || ''}`,
      width: 180,
    },
    { field: 'date', headerName: 'Date', width: 120 },
    {
      headerName: 'Check In',
      valueGetter: (p: { data: AttendanceRecord }) => p.data.checkInTime ? formatDateTime(p.data.checkInTime) : '-',
      width: 160,
    },
    {
      headerName: 'Check Out',
      valueGetter: (p: { data: AttendanceRecord }) => p.data.checkOutTime ? formatDateTime(p.data.checkOutTime) : '-',
      width: 160,
    },
    {
      headerName: 'Status',
      field: 'status',
      width: 120,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    { field: 'lateMinutes', headerName: 'Late (min)', width: 100 },
    {
      headerName: 'Work Hours',
      valueGetter: (p: { data: AttendanceRecord }) => p.data.workHours ? `${p.data.workHours}h` : '-',
      width: 100,
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }} alignItems="center">
        <TextField
          type="date"
          label="Date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          InputLabelProps={{ shrink: true }}
          sx={{ width: 200 }}
        />
        <FormControl size="small" sx={{ width: 160 }}>
          <InputLabel>Status</InputLabel>
          <Select value={statusFilter} label="Status" onChange={(e) => setStatusFilter(e.target.value)}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="present">Present</MenuItem>
            <MenuItem value="late">Late</MenuItem>
            <MenuItem value="absent">Absent</MenuItem>
            <MenuItem value="half_day">Half Day</MenuItem>
            <MenuItem value="leave">Leave</MenuItem>
          </Select>
        </FormControl>
        <Box sx={{ flex: 1 }} />
        <Button variant="outlined" startIcon={<DownloadIcon />} onClick={handleExport}>
          Export
        </Button>
        <Button variant="contained" onClick={fetchAttendance}>Refresh</Button>
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <DataTable
          rowData={records}
          columnDefs={columnDefs}
          height={500}
        />
      )}
    </Card>
  );
}
