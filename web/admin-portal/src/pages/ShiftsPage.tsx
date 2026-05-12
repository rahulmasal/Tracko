import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import ShiftManagement from '../components/shifts/ShiftManagement';
export default function ShiftsPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Shift Management</Typography><ShiftManagement /></Box>;
}
