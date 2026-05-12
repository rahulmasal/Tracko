import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import LeaveApprovals from '../components/leaves/LeaveApprovals';
import TeamLeaveCalendar from '../components/leaves/TeamLeaveCalendar';

export default function LeavesPage() {
  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Leave Management</Typography>
      <Box sx={{ mb: 3 }}>
        <LeaveApprovals />
      </Box>
      <TeamLeaveCalendar />
    </Box>
  );
}
