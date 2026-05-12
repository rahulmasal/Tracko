import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Badge from '@mui/material/Badge';

interface PendingReportsWidgetProps {
  pendingCount: number;
  urgentCount: number;
}

export default function PendingReportsWidget({ pendingCount, urgentCount }: PendingReportsWidgetProps) {
  return (
    <Box>
      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
        Pending Reports
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Badge badgeContent={urgentCount} color="error">
          <Typography variant="h3" sx={{ fontWeight: 700 }}>
            {pendingCount}
          </Typography>
        </Badge>
        <Box>
          {urgentCount > 0 && (
            <Typography variant="caption" color="error" sx={{ fontWeight: 600, display: 'block' }}>
              {urgentCount} urgent
            </Typography>
          )}
          <Typography variant="caption" color="text.secondary">
            Reports to review
          </Typography>
        </Box>
      </Box>
    </Box>
  );
}
