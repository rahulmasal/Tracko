import { useSearchParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TeamScorecard from '../components/score/TeamScorecard';
import ScoreDetail from '../components/score/ScoreDetail';

export default function ScorecardPage() {
  const [params] = useSearchParams();
  const scoreId = params.get('id');

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>{scoreId ? 'Score Detail' : 'Team Scorecard'}</Typography>
      {scoreId ? <ScoreDetail /> : <TeamScorecard />}
    </Box>
  );
}
