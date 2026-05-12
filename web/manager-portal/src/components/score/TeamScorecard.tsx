import { useState, useEffect, useMemo } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Chip from '@mui/material/Chip';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import Collapse from '@mui/material/Collapse';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import RemoveIcon from '@mui/icons-material/Remove';
import { getTeamRanking, ScoreCard } from '../../services/scoreService';
import { useNavigate } from 'react-router-dom';

function Row({ score, rank }: { score: ScoreCard; rank: number }) {
  const [open, setOpen] = useState(false);

  const gradeColor = (grade: string) => {
    switch(grade) {
      case 'A+': return '#22c55e';
      case 'A': return '#3b82f6';
      case 'B+': return '#8b5cf6';
      case 'B': return '#f59e0b';
      case 'C': return '#ef4444';
      default: return '#6b7280';
    }
  };

  return (
    <>
      <TableRow sx={{ '&:hover': { bgcolor: '#f8fafc' }, cursor: 'pointer' }} onClick={() => setOpen(!open)}>
        <TableCell><Typography variant="body2" fontWeight={700}>#{rank}</Typography></TableCell>
        <TableCell>
          <Typography variant="body2" fontWeight={500}>
            {score.user.firstName} {score.user.lastName || ''}
          </Typography>
          <Typography variant="caption" color="text.secondary">{score.user.designation}</Typography>
        </TableCell>
        <TableCell align="center">
          <Chip
            label={score.grade || '-'}
            size="small"
            sx={{ bgcolor: `${gradeColor(score.grade)}20`, color: gradeColor(score.grade), fontWeight: 700 }}
          />
        </TableCell>
        <TableCell align="center">
          <Typography variant="body2" fontWeight={600}>{score.totalScore}</Typography>
        </TableCell>
        <TableCell align="center">
          {score.totalScore > 75 ? <ArrowUpwardIcon sx={{ fontSize: 18, color: 'success.main' }} /> :
           score.totalScore > 50 ? <RemoveIcon sx={{ fontSize: 18, color: 'text.disabled' }} /> :
           <ArrowDownwardIcon sx={{ fontSize: 18, color: 'error.main' }} />}
        </TableCell>
        <TableCell align="center">
          <IconButton size="small" onClick={() => setOpen(!open)}>
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell colSpan={6} sx={{ py: 0 }}>
          <Collapse in={open}>
            <Box sx={{ p: 2, display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 2 }}>
              <Box><Typography variant="caption" color="text.secondary">Attendance</Typography>
                <Typography variant="body2">{score.attendanceScore}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Punctuality</Typography>
                <Typography variant="body2">{score.punctualityScore}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Visits</Typography>
                <Typography variant="body2">{score.visitCompletionScore}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Report Quality</Typography>
                <Typography variant="body2">{score.reportQualityScore}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Enquiries</Typography>
                <Typography variant="body2">{score.enquiryResolutionScore}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Feedback</Typography>
                <Typography variant="body2">{score.customerFeedbackScore}</Typography></Box>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

export default function TeamScorecard() {
  const navigate = useNavigate();
  const [scores, setScores] = useState<ScoreCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const now = new Date();

  useEffect(() => {
    fetchScores();
  }, []);

  const fetchScores = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getTeamRanking({ month: now.getMonth() + 1, year: now.getFullYear() });
      setScores(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Card>
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell width={50}>Rank</TableCell>
              <TableCell>Employee</TableCell>
              <TableCell align="center" width={60}>Grade</TableCell>
              <TableCell align="center" width={80}>Score</TableCell>
              <TableCell align="center" width={60}>Trend</TableCell>
              <TableCell align="center" width={50}></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {scores.map((score, idx) => (
              <Row key={score.id} score={score} rank={idx + 1} />
            ))}
            {scores.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Typography variant="body2" color="text.secondary" sx={{ py: 4 }}>
                    No scorecards for this month
                  </Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Card>
  );
}
