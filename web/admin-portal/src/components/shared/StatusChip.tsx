import Chip from '@mui/material/Chip';

const COLORS: Record<string, string> = {
  present: '#22c55e', late: '#f59e0b', absent: '#ef4444', pending: '#f59e0b',
  approved: '#22c55e', rejected: '#ef4444', active: '#22c55e', inactive: '#6b7280',
  resolved: '#22c55e', unresolved: '#ef4444', success: '#22c55e', error: '#ef4444',
  warning: '#f59e0b', info: '#3b82f6',
};

interface StatusChipProps {
  status: string;
  size?: 'small' | 'medium';
}

export default function StatusChip({ status, size = 'small' }: StatusChipProps) {
  const color = COLORS[status.toLowerCase()] || '#6b7280';
  return (
    <Chip
      label={status.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase())}
      size={size}
      sx={{ bgcolor: `${color}15`, color, fontWeight: 600, fontSize: size === 'small' ? 11 : 13, borderRadius: 1 }}
    />
  );
}
