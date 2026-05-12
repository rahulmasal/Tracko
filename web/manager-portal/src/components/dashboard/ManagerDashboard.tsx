import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Skeleton from '@mui/material/Skeleton';
import Alert from '@mui/material/Alert';
import { getDashboardData, DashboardData } from '../../services/dashboardService';
import AttendanceWidget from './AttendanceWidget';
import LiveMapWidget from './LiveMapWidget';
import PendingReportsWidget from './PendingReportsWidget';
import ScoreRankingWidget from './ScoreRankingWidget';

export default function ManagerDashboard() {
  const navigate = useNavigate();
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getDashboardData();
      setData(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Grid container spacing={3}>
        {[...Array(8)].map((_, i) => (
          <Grid item xs={12} md={6} lg={3} key={i}>
            <Card><CardContent><Skeleton variant="rounded" height={180} /></CardContent></Card>
          </Grid>
        ))}
      </Grid>
    );
  }

  if (error) {
    return <Alert severity="error" sx={{ m: 2 }}>{error}</Alert>;
  }

  if (!data) return null;

  return (
    <Box sx={{ flex: 1 }}>
      <Typography variant="h4" sx={{ mb: 3, fontWeight: 700 }}>Dashboard</Typography>
      <Grid container spacing={3}>
        <Grid item xs={12} md={6} lg={3}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/attendance')}
          >
            <CardContent>
              <AttendanceWidget summary={data.attendanceSummary} />
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={3}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Missed Check-ins Today
              </Typography>
              <Typography variant="h3" color="error" sx={{ fontWeight: 700 }}>
                {data.missedCheckins.length}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Engineers without check-in
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={3}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/reports')}
          >
            <CardContent>
              <PendingReportsWidget
                pendingCount={data.pendingReports}
                urgentCount={data.urgentReports}
              />
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={3}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/leaves')}
          >
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Pending Leave Approvals
              </Typography>
              <Typography variant="h3" color="warning.main" sx={{ fontWeight: 700 }}>
                {data.pendingLeaveApprovals}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Requests awaiting review
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={4}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/enquiries')}
          >
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Enquiries Pipeline
              </Typography>
              <Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
                <Box>
                  <Typography variant="h4" color="info.main" sx={{ fontWeight: 700 }}>{data.enquiriesPipeline.new}</Typography>
                  <Typography variant="caption">New</Typography>
                </Box>
                <Box>
                  <Typography variant="h4" color="warning.main" sx={{ fontWeight: 700 }}>{data.enquiriesPipeline.quoted}</Typography>
                  <Typography variant="caption">Quoted</Typography>
                </Box>
                <Box>
                  <Typography variant="h4" color="success.main" sx={{ fontWeight: 700 }}>{data.enquiriesPipeline.won}</Typography>
                  <Typography variant="caption">Won</Typography>
                </Box>
                <Box>
                  <Typography variant="h4" color="error.main" sx={{ fontWeight: 700 }}>{data.enquiriesPipeline.lost}</Typography>
                  <Typography variant="caption">Lost</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={4}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/quotations')}
          >
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Quotations Pending Approval
              </Typography>
              <Typography variant="h3" color="secondary.main" sx={{ fontWeight: 700 }}>
                {data.quotationsPendingApproval}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Awaiting your approval
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} lg={4}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/scorecard')}
          >
            <CardContent>
              <ScoreRankingWidget ranking={data.teamRanking} />
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} lg={8}>
          <Card
            sx={{ cursor: 'pointer', '&:hover': { boxShadow: 4 } }}
            onClick={() => navigate('/live-map')}
          >
            <CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}>
              <Box sx={{ p: 2, pb: 0 }}>
                <Typography variant="subtitle1" fontWeight={600}>Live Team Location</Typography>
              </Box>
              <Box sx={{ height: 300 }}>
                <LiveMapWidget locations={data.liveLocations} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>
                Follow-ups Due Today
              </Typography>
              {data.dueFollowUps.length === 0 ? (
                <Typography variant="body2" color="text.secondary" sx={{ py: 4, textAlign: 'center' }}>
                  No follow-ups due today
                </Typography>
              ) : (
                data.dueFollowUps.map((fu) => (
                  <Box key={fu.id} sx={{ py: 1, borderBottom: '1px solid', borderColor: 'divider' }}>
                    <Typography variant="body2" fontWeight={500}>{fu.customerName}</Typography>
                    <Typography variant="caption" color="text.secondary">{fu.notes}</Typography>
                  </Box>
                ))
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
