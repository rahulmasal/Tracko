import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import SecurityEventsView from '../components/security/SecurityEventsView';
export default function SecurityPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Security Events</Typography><SecurityEventsView /></Box>;
}
