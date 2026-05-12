import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import UserManagement from '../components/users/UserManagement';
export default function UsersPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>User Management</Typography><UserManagement /></Box>;
}
