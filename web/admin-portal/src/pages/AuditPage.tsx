import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import AuditLogViewer from '../components/audit/AuditLogViewer';
export default function AuditPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Audit Logs</Typography><AuditLogViewer /></Box>;
}
