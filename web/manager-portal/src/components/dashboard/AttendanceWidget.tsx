import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';

interface AttendanceWidgetProps {
  summary: {
    present: number;
    late: number;
    absent: number;
    halfDay: number;
    leave: number;
    total: number;
  };
}

const COLORS = {
  present: '#22c55e',
  late: '#f59e0b',
  absent: '#ef4444',
  halfDay: '#f97316',
  leave: '#3b82f6',
};

export default function AttendanceWidget({ summary }: AttendanceWidgetProps) {
  const data = [
    { name: 'Present', value: summary.present, color: COLORS.present },
    { name: 'Late', value: summary.late, color: COLORS.late },
    { name: 'Absent', value: summary.absent, color: COLORS.absent },
    { name: 'Half Day', value: summary.halfDay, color: COLORS.halfDay },
    { name: 'Leave', value: summary.leave, color: COLORS.leave },
  ].filter((d) => d.value > 0);

  const presentPercent = summary.total > 0 ? Math.round((summary.present / summary.total) * 100) : 0;

  return (
    <Box>
      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
        Team Attendance Today
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Box sx={{ width: 100, height: 100 }}>
          <ResponsiveContainer>
            <PieChart>
              <Pie data={data} cx="50%" cy="50%" innerRadius={30} outerRadius={45} dataKey="value">
                {data.map((entry) => (
                  <Cell key={entry.name} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Box>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 700, lineHeight: 1 }}>
            {presentPercent}%
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {summary.present} / {summary.total} present
          </Typography>
        </Box>
      </Box>
    </Box>
  );
}
