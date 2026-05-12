import Timeline from '@mui/lab/Timeline';
import TimelineItem from '@mui/lab/TimelineItem';
import TimelineSeparator from '@mui/lab/TimelineSeparator';
import TimelineConnector from '@mui/lab/TimelineConnector';
import TimelineContent from '@mui/lab/TimelineContent';
import TimelineDot from '@mui/lab/TimelineDot';
import Typography from '@mui/material/Typography';
import { formatDateTime } from '../../utils/helpers';

interface TimelineEvent {
  event: string;
  userName: string;
  timestamp: string;
}

interface QuotationTimelineProps {
  events: TimelineEvent[];
}

const eventColors: Record<string, 'success' | 'info' | 'warning' | 'error' | 'primary' | 'secondary'> = {
  created: 'info',
  approved: 'success',
  rejected: 'error',
  sent: 'primary',
  accepted: 'success',
  expired: 'warning',
  negotiated: 'warning',
};

export default function QuotationTimeline({ events }: QuotationTimelineProps) {
  if (!events || events.length === 0) {
    return <Typography variant="body2" color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>No timeline events</Typography>;
  }

  return (
    <Timeline>
      {events.map((event, idx) => (
        <TimelineItem key={idx}>
          <TimelineSeparator>
            <TimelineDot color={eventColors[event.event] || 'grey'} />
            {idx < events.length - 1 && <TimelineConnector />}
          </TimelineSeparator>
          <TimelineContent>
            <Typography variant="body2" fontWeight={600} sx={{ textTransform: 'capitalize' }}>
              {event.event.replace(/_/g, ' ')}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {event.userName} - {formatDateTime(event.timestamp)}
            </Typography>
          </TimelineContent>
        </TimelineItem>
      ))}
    </Timeline>
  );
}
