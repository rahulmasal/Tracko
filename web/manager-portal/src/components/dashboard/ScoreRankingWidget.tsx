import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import RemoveIcon from '@mui/icons-material/Remove';

interface TeamMember {
  userId: number;
  userName: string;
  totalScore: number;
  grade: string;
  trend: 'up' | 'down' | 'stable';
}

interface ScoreRankingWidgetProps {
  ranking: TeamMember[];
}

export default function ScoreRankingWidget({ ranking }: ScoreRankingWidgetProps) {
  const top3 = ranking.slice(0, 3);
  const bottom3 = ranking.slice(-3).reverse();

  return (
    <Box>
      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
        Team Score Ranking
      </Typography>
      <Box sx={{ display: 'flex', gap: 2 }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="caption" color="success.main" fontWeight={600}>Top 3</Typography>
          {top3.map((m, i) => (
            <Box key={m.userId} sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ minWidth: 16 }}>#{i + 1}</Typography>
              <Typography variant="body2" sx={{ flex: 1 }}>{m.userName}</Typography>
              <Typography variant="caption" fontWeight={600}>{m.totalScore}</Typography>
              {m.trend === 'up' ? <ArrowUpwardIcon sx={{ fontSize: 14, color: 'success.main' }} /> :
               m.trend === 'down' ? <ArrowDownwardIcon sx={{ fontSize: 14, color: 'error.main' }} /> :
               <RemoveIcon sx={{ fontSize: 14, color: 'text.disabled' }} />}
            </Box>
          ))}
        </Box>
        <Box sx={{ flex: 1 }}>
          <Typography variant="caption" color="error.main" fontWeight={600}>Bottom 3</Typography>
          {bottom3.map((m, i) => (
            <Box key={m.userId} sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ minWidth: 16 }}>#{ranking.length - 2 + i}</Typography>
              <Typography variant="body2" sx={{ flex: 1 }}>{m.userName}</Typography>
              <Typography variant="caption" fontWeight={600}>{m.totalScore}</Typography>
              {m.trend === 'up' ? <ArrowUpwardIcon sx={{ fontSize: 14, color: 'success.main' }} /> :
               m.trend === 'down' ? <ArrowDownwardIcon sx={{ fontSize: 14, color: 'error.main' }} /> :
               <RemoveIcon sx={{ fontSize: 14, color: 'text.disabled' }} />}
            </Box>
          ))}
        </Box>
      </Box>
    </Box>
  );
}
