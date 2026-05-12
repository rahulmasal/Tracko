import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import RoleManagement from '../components/users/RoleManagement';
export default function RolesPage() {
  return <Box><Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Role Management</Typography><RoleManagement /></Box>;
}
