import { useState, useEffect } from 'react';
import IconButton from '@mui/material/IconButton';
import Badge from '@mui/material/Badge';
import Popover from '@mui/material/Popover';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import Button from '@mui/material/Button';
import NotificationsIcon from '@mui/icons-material/Notifications';
import CircleIcon from '@mui/icons-material/Circle';
import { formatDistanceToNow, parseISO } from 'date-fns';
import api from '../../services/api';

interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export default function NotificationBell() {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications', { params: { size: 10 } });
      const data = response.data;
      setNotifications(data.content || data);
      setUnreadCount(data.unreadCount || data.filter((n: Notification) => !n.isRead).length);
    } catch {
      // silently fail
    }
  };

  const markAsRead = async () => {
    try {
      await api.put('/notifications/read-all');
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch {
      // silently fail
    }
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'attendance': return '#3b82f6';
      case 'report': return '#22c55e';
      case 'leave': return '#f59e0b';
      case 'approval': return '#8b5cf6';
      case 'enquiry': return '#ec4899';
      default: return '#64748b';
    }
  };

  return (
    <>
      <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
        <Badge badgeContent={unreadCount} color="error" max={99}>
          <NotificationsIcon />
        </Badge>
      </IconButton>
      <Popover
        open={Boolean(anchorEl)}
        anchorEl={anchorEl}
        onClose={() => setAnchorEl(null)}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        sx={{ mt: 1 }}
        PaperProps={{ sx: { width: 360, borderRadius: 2 } }}
      >
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="subtitle1" fontWeight={600}>Notifications</Typography>
          {unreadCount > 0 && (
            <Button size="small" onClick={markAsRead}>Mark all read</Button>
          )}
        </Box>
        <Divider />
        <List sx={{ maxHeight: 400, overflow: 'auto', py: 0 }}>
          {notifications.length === 0 ? (
            <Box sx={{ p: 3, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">No notifications</Typography>
            </Box>
          ) : (
            notifications.map((notif) => (
              <ListItem key={notif.id} sx={{ px: 2, py: 1.5, bgcolor: notif.isRead ? 'transparent' : '#f0f7ff' }}>
                <CircleIcon sx={{ fontSize: 8, color: getIcon(notif.type), mr: 1.5, mt: 0.5 }} />
                <ListItemText
                  primary={
                    <Typography variant="body2" fontWeight={notif.isRead ? 400 : 600}>
                      {notif.title}
                    </Typography>
                  }
                  secondary={
                    <>
                      <Typography variant="caption" color="text.secondary" display="block">
                        {notif.message}
                      </Typography>
                      <Typography variant="caption" color="text.disabled">
                        {formatDistanceToNow(parseISO(notif.createdAt), { addSuffix: true })}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
            ))
          )}
        </List>
      </Popover>
    </>
  );
}
