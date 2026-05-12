import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import BranchManagement from '../components/branches/BranchManagement';
export default function BranchesPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Branch Management</Typography><BranchManagement /></Box>;
}
