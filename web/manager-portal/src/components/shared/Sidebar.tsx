import { useNavigate, useLocation } from 'react-router-dom';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MapIcon from '@mui/icons-material/Map';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import DescriptionIcon from '@mui/icons-material/Description';
import QuestionAnswerIcon from '@mui/icons-material/QuestionAnswer';
import RequestQuoteIcon from '@mui/icons-material/RequestQuote';
import BeachAccessIcon from '@mui/icons-material/BeachAccess';
import StarIcon from '@mui/icons-material/Star';
import { SIDEBAR_WIDTH, SIDEBAR_COLLAPSED_WIDTH } from '../../utils/constants';

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

const navItems = [
  { path: '/dashboard', label: 'Dashboard', icon: <DashboardIcon /> },
  { path: '/live-map', label: 'Live Map', icon: <MapIcon /> },
  { path: '/attendance', label: 'Attendance', icon: <CalendarMonthIcon /> },
  { path: '/reports', label: 'Reports', icon: <DescriptionIcon /> },
  { path: '/enquiries', label: 'Enquiries', icon: <QuestionAnswerIcon /> },
  { path: '/quotations', label: 'Quotations', icon: <RequestQuoteIcon /> },
  { path: '/leaves', label: 'Leaves', icon: <BeachAccessIcon /> },
  { path: '/scorecard', label: 'Scorecard', icon: <StarIcon /> },
];

export default function Sidebar({ collapsed }: SidebarProps) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Box
      sx={{
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        width: collapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_WIDTH,
        bgcolor: '#0f172a',
        color: '#ffffff',
        display: 'flex',
        flexDirection: 'column',
        transition: 'width 0.2s ease',
        zIndex: 1200,
        overflow: 'hidden',
      }}
    >
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          height: 64,
          px: 2,
          borderBottom: '1px solid rgba(255,255,255,0.1)',
        }}
      >
        <Typography
          variant="h6"
          sx={{
            fontWeight: 700,
            color: '#ffffff',
            whiteSpace: 'nowrap',
            fontSize: collapsed ? 16 : 20,
          }}
        >
          {collapsed ? 'T' : 'Tracko'}
        </Typography>
      </Box>
      <List sx={{ flex: 1, py: 1 }}>
        {navItems.map((item) => {
          const isActive = location.pathname.startsWith(item.path);
          return (
            <ListItem key={item.path} disablePadding sx={{ display: 'block' }}>
              <ListItemButton
                onClick={() => navigate(item.path)}
                sx={{
                  minHeight: 48,
                  px: 2.5,
                  mx: 1,
                  borderRadius: 1,
                  mb: 0.5,
                  justifyContent: collapsed ? 'center' : 'initial',
                  bgcolor: isActive ? 'rgba(59,130,246,0.15)' : 'transparent',
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.08)' },
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 0,
                    mr: collapsed ? 0 : 2,
                    justifyContent: 'center',
                    color: isActive ? '#3b82f6' : 'rgba(255,255,255,0.6)',
                  }}
                >
                  {item.icon}
                </ListItemIcon>
                {!collapsed && (
                  <ListItemText
                    primary={item.label}
                    sx={{
                      '& .MuiListItemText-primary': {
                        fontSize: 14,
                        fontWeight: isActive ? 600 : 400,
                        color: isActive ? '#ffffff' : 'rgba(255,255,255,0.7)',
                      },
                    }}
                  />
                )}
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)' }} />
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.4)', fontSize: 10 }}>
          {collapsed ? 'v1' : 'Tracko v1.0.0'}
        </Typography>
      </Box>
    </Box>
  );
}
