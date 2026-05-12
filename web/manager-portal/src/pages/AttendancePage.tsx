import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TeamAttendanceView from '../components/attendance/TeamAttendanceView';
import AttendanceExceptions from '../components/attendance/AttendanceExceptions';

export default function AttendancePage() {
  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Attendance</Typography>
      <TeamAttendanceView />
      <Box sx={{ mt: 3 }}>
        <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>Exceptions & Corrections</Typography>
        <AttendanceExceptions />
      </Box>
    </Box>
  );
}
