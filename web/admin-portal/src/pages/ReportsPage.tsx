import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import ReportViewer from '../components/reports/ReportViewer';
export default function ReportsPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Reports</Typography><ReportViewer /></Box>;
}
