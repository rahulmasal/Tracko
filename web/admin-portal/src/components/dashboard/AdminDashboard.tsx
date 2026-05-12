import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Skeleton from '@mui/material/Skeleton';
import Alert from '@mui/material/Alert';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import api from '../../../manager-portal/src/services/api';

export default function AdminDashboard() {
  const [data, setData] = useState<Record<string, unknown> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/admin/dashboard');
      setData(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
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

  if (error) return <Alert severity="error">{error}</Alert>;
  if (!data) return null;

  const attendancePct = ((data.totalUsers as number || 0) > 0
    ? Math.round(((data.presentToday as number || 0) / (data.totalUsers as number || 1)) * 100) : 0);

  return (
    <Box>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 3 }}>Admin Dashboard</Typography>
      <Grid container spacing={3}>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>Attendance Today</Typography>
              <Typography variant="h3" color="success.main" fontWeight={700}>{attendancePct}%</Typography>
              <Typography variant="caption" color="text.secondary">{data.presentToday as number || 0} / {data.totalUsers as number || 0} present</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>Active Users</Typography>
              <Box sx={{ display: 'flex', gap: 3 }}>
                <Box><Typography variant="h4" color="success.main" fontWeight={700}>{data.activeUsers as number || 0}</Typography>
                  <Typography variant="caption">Active</Typography></Box>
                <Box><Typography variant="h4" color="text.secondary" fontWeight={700}>{data.inactiveUsers as number || 0}</Typography>
                  <Typography variant="caption">Inactive</Typography></Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>Security Events (24h)</Typography>
              <Typography variant="h3" color="error" fontWeight={700}>{data.securityEvents24h as number || 0}</Typography>
              <Typography variant="caption" color="text.secondary">Events requiring attention</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>Leave Utilization</Typography>
              <Typography variant="h3" color="info.main" fontWeight={700}>{data.leavesToday as number || 0}</Typography>
              <Typography variant="caption" color="text.secondary">On leave today</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>Attendance Trend (30 days)</Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={(data.attendanceTrend as Array<Record<string, unknown>>) || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" fontSize={11} />
                  <YAxis />
                  <Tooltip />
                  <Line type="monotone" dataKey="present" stroke="#22c55e" strokeWidth={2} />
                  <Line type="monotone" dataKey="absent" stroke="#ef4444" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>Branch-wise Attendance</Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={(data.branchAttendance as Array<Record<string, unknown>>) || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="branch" fontSize={11} />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="present" fill="#22c55e" radius={[4,4,0,0]} />
                  <Bar dataKey="absent" fill="#ef4444" radius={[4,4,0,0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>Enquiry Conversion</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <PieChart>
                  <Pie data={(data.enquiryConversion as Array<Record<string, unknown>>) || []}
                    cx="50%" cy="50%" outerRadius={80} dataKey="count" nameKey="status"
                    label={({ status, count }: { status: string; count: number }) => `${status}: ${count}`}>
                    {((data.enquiryConversion as Array<Record<string, unknown>>) || []).map((entry, idx) => (
                      <Cell key={idx} fill={['#3b82f6', '#f59e0b', '#22c55e', '#ef4444'][idx % 4]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>Leave Utilization</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <PieChart>
                  <Pie data={(data.leaveUtilization as Array<Record<string, unknown>>) || []}
                    cx="50%" cy="50%" outerRadius={80} dataKey="count" nameKey="type"
                    label={({ type, count }: { type: string; count: number }) => `${type}: ${count}`}>
                    {((data.leaveUtilization as Array<Record<string, unknown>>) || []).map((_, idx) => (
                      <Cell key={idx} fill={['#22c55e', '#ef4444', '#3b82f6', '#f59e0b'][idx % 4]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>Quotation Turnaround</Typography>
              <Typography variant="h3" color="primary" fontWeight={700}>{data.avgQuotationTurnaroundHours as number || 0}h</Typography>
              <Typography variant="caption" color="text.secondary">Average time to create quotation</Typography>
              <Box sx={{ mt: 2 }}>
                <Typography variant="body2" fontWeight={500}>Recent Audit Events</Typography>
                {(data.recentAuditEvents as Array<Record<string, unknown>> || []).slice(0, 4).map((ev: Record<string, unknown>, idx: number) => (
                  <Typography key={idx} variant="caption" display="block" color="text.secondary" sx={{ mt: 0.5 }}>
                    {ev.action as string} - {ev.entityType as string}
                  </Typography>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
