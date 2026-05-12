import Chip from '@mui/material/Chip';
import { getStatusColor } from '../../utils/helpers';

interface StatusChipProps {
  status: string;
  size?: 'small' | 'medium';
  label?: string;
}

export default function StatusChip({ status, size = 'small', label }: StatusChipProps) {
  const color = getStatusColor(status);
  return (
    <Chip
      label={label || status.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase())}
      size={size}
      sx={{
        bgcolor: `${color}15`,
        color: color,
        fontWeight: 600,
        fontSize: size === 'small' ? 11 : 13,
        borderRadius: 1,
        textTransform: 'capitalize',
      }}
    />
  );
}

export { StatusChip };
