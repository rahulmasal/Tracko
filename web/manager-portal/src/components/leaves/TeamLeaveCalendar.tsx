import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import {
  startOfMonth, endOfMonth, startOfWeek, endOfWeek, eachDayOfInterval,
  format, isSameMonth, isSameDay, addMonths, subMonths,
} from 'date-fns';
import { getTeamLeaves } from '../../services/leaveService';
import { getLeaveTypeColor, getLeaveTypeLabel } from '../../utils/helpers';

interface LeaveEvent {
  userId: number;
  userName: string;
  leaveType: string;
  startDate: string;
  endDate: string;
}

export default function TeamLeaveCalendar() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [events, setEvents] = useState<LeaveEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const monthStart = startOfMonth(currentDate);
  const monthEnd = endOfMonth(currentDate);
  const calStart = startOfWeek(monthStart);
  const calEnd = endOfWeek(monthEnd);
  const days = eachDayOfInterval({ start: calStart, end: calEnd });

  useEffect(() => {
    fetchLeaves();
  }, [currentDate]);

  const fetchLeaves = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getTeamLeaves({ month: currentDate.getMonth() + 1, year: currentDate.getFullYear() });
      setEvents(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const getEventsForDay = (day: Date) => {
    return events.filter((e) => {
      const start = new Date(e.startDate);
      const end = new Date(e.endDate);
      return day >= start && day <= end;
    });
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <IconButton onClick={() => setCurrentDate(subMonths(currentDate, 1))}><ChevronLeftIcon /></IconButton>
          <Typography variant="h6" fontWeight={600}>{format(currentDate, 'MMMM yyyy')}</Typography>
          <IconButton onClick={() => setCurrentDate(addMonths(currentDate, 1))}><ChevronRightIcon /></IconButton>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
        ) : (
          <>
            <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 0.5, mb: 0.5 }}>
              {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((d) => (
                <Typography key={d} variant="caption" align="center" color="text.secondary" fontWeight={600}>
                  {d}
                </Typography>
              ))}
            </Box>
            <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 0.5 }}>
              {days.map((day) => {
                const dayEvents = getEventsForDay(day);
                const isCurrentMonth = isSameMonth(day, currentDate);
                const isToday = isSameDay(day, new Date());
                return (
                  <Box
                    key={day.toISOString()}
                    sx={{
                      minHeight: 80,
                      p: 0.5,
                      bgcolor: isToday ? '#eff6ff' : 'transparent',
                      border: '1px solid',
                      borderColor: isToday ? '#3b82f6' : 'divider',
                      borderRadius: 1,
                      opacity: isCurrentMonth ? 1 : 0.4,
                    }}
                  >
                    <Typography variant="caption" sx={{ fontWeight: isToday ? 700 : 400 }}>
                      {format(day, 'd')}
                    </Typography>
                    {dayEvents.slice(0, 2).map((e, i) => (
                      <Typography
                        key={i}
                        variant="caption"
                        sx={{
                          display: 'block',
                          fontSize: 9,
                          bgcolor: `${getLeaveTypeColor(e.leaveType)}20`,
                          color: getLeaveTypeColor(e.leaveType),
                          borderRadius: 0.5,
                          px: 0.5,
                          mb: 0.25,
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                        }}
                      >
                        {e.userName.split(' ')[0]}
                      </Typography>
                    ))}
                    {dayEvents.length > 2 && (
                      <Typography variant="caption" sx={{ fontSize: 9, color: 'text.secondary' }}>
                        +{dayEvents.length - 2} more
                      </Typography>
                    )}
                  </Box>
                );
              })}
            </Box>
            <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
              {['casual', 'sick', 'earned', 'unpaid'].map((type) => (
                <Box key={type} sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: getLeaveTypeColor(type) }} />
                  <Typography variant="caption">{getLeaveTypeLabel(type)}</Typography>
                </Box>
              ))}
            </Box>
          </>
        )}
      </CardContent>
    </Card>
  );
}
