import { useState } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import { getAttendanceTrend, getBranchWiseAttendance, getQuotationTurnaround, getEnquiryConversion, exportReport } from '../../services/reportService';
import { useSnackbar } from 'notistack';
import DownloadIcon from '@mui/icons-material/Download';
import { format } from 'date-fns';

const reportTypes = [
  { value: 'attendance_trend', label: 'Attendance Trend' },
  { value: 'branch_attendance', label: 'Branch-wise Attendance' },
  { value: 'quotation_turnaround', label: 'Quotation Turnaround' },
  { value: 'enquiry_conversion', label: 'Enquiry Conversion' },
];

export default function ReportViewer() {
  const [reportType, setReportType] = useState('attendance_trend');
  const [dateRange, setDateRange] = useState({ start: format(new Date().setDate(1), 'yyyy-MM-dd'), end: format(new Date(), 'yyyy-MM-dd') });
  const [chartData, setChartData] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const fetchReport = async () => {
    setLoading(true);
    try {
      let data: unknown[];
      switch (reportType) {
        case 'attendance_trend':
          data = await getAttendanceTrend(30);
          break;
        case 'branch_attendance':
          data = await getBranchWiseAttendance({ startDate: dateRange.start, endDate: dateRange.end });
          break;
        case 'quotation_turnaround':
          data = await getQuotationTurnaround({ startDate: dateRange.start, endDate: dateRange.end });
          break;
        case 'enquiry_conversion':
          data = await getEnquiryConversion({ startDate: dateRange.start, endDate: dateRange.end });
          break;
        default:
          data = [];
      }
      setChartData(Array.isArray(data) ? data : []);
    } catch {
      enqueueSnackbar('Failed to load report', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (format: 'excel' | 'pdf') => {
    try {
      await exportReport({ type: reportType, startDate: dateRange.start, endDate: dateRange.end, format });
      enqueueSnackbar(`Report exported as ${format}`, { variant: 'success' });
    } catch {
      enqueueSnackbar('Export failed', { variant: 'error' });
    }
  };

  const renderChart = () => {
    if (chartData.length === 0) {
      return <Typography variant="body2" color="text.secondary" sx={{ py: 4, textAlign: 'center' }}>No data available. Select report type and fetch.</Typography>;
    }
    const keys = Object.keys(chartData[0] as Record<string, unknown>).filter((k) => k !== 'date' && k !== 'branch' && k !== 'label');
    return (
      <ResponsiveContainer width="100%" height={400}>
        {reportType === 'attendance_trend' ? (
          <LineChart data={chartData as Array<Record<string, unknown>>}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" fontSize={11} />
            <YAxis />
            <Tooltip />
            {keys.map((key, idx) => (
              <Line key={key} type="monotone" dataKey={key} stroke={['#3b82f6', '#22c55e', '#ef4444', '#f59e0b'][idx % 4]} strokeWidth={2} />
            ))}
          </LineChart>
        ) : (
          <BarChart data={chartData as Array<Record<string, unknown>>}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey={(keys.length > 0 ? chartData[0] && Object.keys(chartData[0] as Record<string, unknown>).filter(k => k !== 'date' && k !== 'branch' && k !== 'label')[0] : undefined) || 'label'} fontSize={11} />
            <YAxis />
            <Tooltip />
            {keys.map((key, idx) => (
              <Bar key={key} dataKey={key} fill={['#3b82f6', '#22c55e', '#ef4444', '#f59e0b'][idx % 4]} radius={[4, 4, 0, 0]} />
            ))}
          </BarChart>
        )}
      </ResponsiveContainer>
    );
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>Report Viewer</Typography>
        <Stack direction="row" spacing={2} sx={{ mb: 3 }} alignItems="center">
          <FormControl size="small" sx={{ width: 220 }}>
            <InputLabel>Report Type</InputLabel>
            <Select value={reportType} label="Report Type" onChange={(e) => setReportType(e.target.value)}>
              {reportTypes.map((rt) => <MenuItem key={rt.value} value={rt.value}>{rt.label}</MenuItem>)}
            </Select>
          </FormControl>
          <TextField type="date" size="small" label="From" value={dateRange.start}
            onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })} InputLabelProps={{ shrink: true }} />
          <TextField type="date" size="small" label="To" value={dateRange.end}
            onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })} InputLabelProps={{ shrink: true }} />
          <Button variant="contained" onClick={fetchReport} disabled={loading}>
            {loading ? 'Loading...' : 'Fetch'}
          </Button>
          <Button variant="outlined" startIcon={<DownloadIcon />} onClick={() => handleExport('excel')}>Excel</Button>
          <Button variant="outlined" startIcon={<DownloadIcon />} onClick={() => handleExport('pdf')}>PDF</Button>
        </Stack>
        {renderChart()}
      </CardContent>
    </Card>
  );
}
