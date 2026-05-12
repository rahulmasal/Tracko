import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { getPendingReports } from '../../services/reportService';
import { CallReport } from '../../services/reportService';
import DataTable from '../shared/DataTable';
import StatusChip from '../shared/StatusChip';
import { formatDate } from '../../utils/helpers';
import ReportReviewDialog from './ReportReviewDialog';

export default function PendingReportsList() {
  const [reports, setReports] = useState<CallReport[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedReport, setSelectedReport] = useState<CallReport | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getPendingReports();
      setReports(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load reports');
    } finally {
      setLoading(false);
    }
  };

  const handleView = (report: CallReport) => {
    setSelectedReport(report);
    setDialogOpen(true);
  };

  const columnDefs = useMemo(() => [
    {
      headerName: 'Customer',
      valueGetter: (p: { data: CallReport }) => p.data.customer.companyName,
      width: 200,
    },
    {
      headerName: 'Engineer',
      valueGetter: (p: { data: CallReport }) => `${p.data.user.firstName} ${p.data.user.lastName || ''}`,
      width: 160,
    },
    { field: 'reportDate', headerName: 'Date', width: 120, valueFormatter: (p: { value: string }) => formatDate(p.value) },
    { field: 'visitType', headerName: 'Visit Type', width: 130 },
    {
      headerName: 'Status',
      field: 'status',
      width: 120,
      cellRenderer: (p: { value: string }) => <StatusChip status={p.value} />,
    },
    {
      headerName: 'Actions',
      width: 100,
      cellRenderer: (p: { data: CallReport }) => (
        <Button size="small" startIcon={<VisibilityIcon />} onClick={() => handleView(p.data)}>
          Review
        </Button>
      ),
    },
  ], []);

  return (
    <Card sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Button variant="contained" onClick={fetchReports}>Refresh</Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
      ) : (
        <DataTable rowData={reports} columnDefs={columnDefs} height={500} />
      )}

      {selectedReport && (
        <ReportReviewDialog
          open={dialogOpen}
          onClose={() => setDialogOpen(false)}
          report={selectedReport}
          onReviewComplete={fetchReports}
        />
      )}
    </Card>
  );
}
