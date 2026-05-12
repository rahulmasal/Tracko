import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from 'recharts';
import { getScorecardById, saveManagerReview, getPreviousMonthComparison, ScoreCard } from '../../services/scoreService';
import { useSnackbar } from 'notistack';

const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export default function ScoreDetail() {
  const { id } = useParams<{ id: string }>();
  const [score, setScore] = useState<ScoreCard | null>(null);
  const [comparison, setComparison] = useState<ScoreCard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reviewScore, setReviewScore] = useState('');
  const [reviewNotes, setReviewNotes] = useState('');
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    if (id) fetchScore();
  }, [id]);

  const fetchScore = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const scoreData = await getScorecardById(parseInt(id));
      setScore(scoreData);
      setReviewScore(scoreData.managerReviewScore?.toString() || '');
      setReviewNotes(scoreData.managerReviewNotes || '');

      // Fetch previous month comparison
      let prevMonth = scoreData.month - 1;
      let prevYear = scoreData.year;
      if (prevMonth === 0) { prevMonth = 12; prevYear -= 1; }
      try {
        const compData = await getPreviousMonthComparison(scoreData.userId, prevMonth, prevYear);
        setComparison(compData);
      } catch { setComparison(null); }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveReview = async () => {
    if (!score || !reviewScore) return;
    setSaving(true);
    try {
      await saveManagerReview(score.id, {
        managerReviewScore: parseFloat(reviewScore),
        managerReviewNotes: reviewNotes,
      });
      enqueueSnackbar('Review saved', { variant: 'success' });
      fetchScore();
    } catch {
      enqueueSnackbar('Failed to save', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const chartData = score ? [
    { name: 'Attendance', current: score.attendanceScore, previous: comparison?.attendanceScore || 0 },
    { name: 'Punctuality', current: score.punctualityScore, previous: comparison?.punctualityScore || 0 },
    { name: 'Visits', current: score.visitCompletionScore, previous: comparison?.visitCompletionScore || 0 },
    { name: 'Reports', current: score.reportQualityScore, previous: comparison?.reportQualityScore || 0 },
    { name: 'Enquiries', current: score.enquiryResolutionScore, previous: comparison?.enquiryResolutionScore || 0 },
    { name: 'Feedback', current: score.customerFeedbackScore, previous: comparison?.customerFeedbackScore || 0 },
  ] : [];

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!score) return <Alert severity="warning">Scorecard not found</Alert>;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Box>
          <Typography variant="h5" fontWeight={600}>
            {score.user.firstName} {score.user.lastName || ''} - {monthNames[score.month - 1]} {score.year}
          </Typography>
          <Typography variant="body2" color="text.secondary">{score.user.designation} | {score.user.employeeId}</Typography>
        </Box>
        <Chip label={`Grade: ${score.grade || '-'}`}
          sx={{ bgcolor: score.grade === 'A+' ? '#22c55e20' : score.grade === 'A' ? '#3b82f620' : '#f59e0b20',
            color: score.grade === 'A+' ? '#22c55e' : score.grade === 'A' ? '#3b82f6' : '#f59e0b',
            fontWeight: 700, fontSize: 16 }} />
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Score Breakdown</Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 4, mb: 3 }}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h2" color="primary" fontWeight={700}>{score.totalScore}</Typography>
              <Typography variant="caption" color="text.secondary">Total Score</Typography>
            </Box>
            {comparison && (
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h4" color="text.secondary">{comparison.totalScore}</Typography>
                <Typography variant="caption" color="text.secondary">Previous Month</Typography>
              </Box>
            )}
          </Box>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" fontSize={12} />
              <YAxis domain={[0, 100]} />
              <Tooltip />
              <Bar dataKey="current" fill="#3b82f6" name="Current" radius={[4, 4, 0, 0]} />
              {comparison && <Bar dataKey="previous" fill="#94a3b8" name="Previous" radius={[4, 4, 0, 0]} />}
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>Manager Review</Typography>
          <Stack spacing={2}>
            <TextField
              label="Review Score (0-100)"
              type="number"
              value={reviewScore}
              onChange={(e) => setReviewScore(e.target.value)}
              inputProps={{ min: 0, max: 100 }}
              size="small"
              sx={{ width: 200 }}
            />
            <TextField
              label="Review Notes"
              multiline
              rows={3}
              fullWidth
              value={reviewNotes}
              onChange={(e) => setReviewNotes(e.target.value)}
            />
            <Button variant="contained" onClick={handleSaveReview} disabled={saving || !reviewScore}>
              {saving ? 'Saving...' : 'Save Review'}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
