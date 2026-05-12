import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import { getConfig, updateConfig } from '../../services/adminService';
import { useSnackbar } from 'notistack';

const PARAMS = [
  { key: 'score.weight_attendance', label: 'Attendance' },
  { key: 'score.weight_punctuality', label: 'Punctuality' },
  { key: 'score.weight_visits', label: 'Visit Completion' },
  { key: 'score.weight_report_quality', label: 'Report Quality' },
  { key: 'score.weight_enquiry_resolution', label: 'Enquiry Resolution' },
  { key: 'score.weight_customer_feedback', label: 'Customer Feedback' },
];

export default function ScoreFormulaConfig() {
  const [weights, setWeights] = useState<Record<string, number>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchConfig(); }, []);

  const fetchConfig = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getConfig('SCORE');
      const w: Record<string, number> = {};
      (Array.isArray(data) ? data : []).forEach((item: { configKey: string; configValue: string }) => {
        if (item.configKey.startsWith('score.weight_')) {
          w[item.configKey] = parseInt(item.configValue) || 0;
        }
      });
      setWeights(w);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const total = Object.values(weights).reduce((s, v) => s + v, 0);

  const handleSave = async () => {
    setSaving(true);
    try {
      for (const [key, value] of Object.entries(weights)) {
        await updateConfig(key, String(value));
      }
      enqueueSnackbar('Score weights updated', { variant: 'success' });
    } catch {
      enqueueSnackbar('Failed to save', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>Score Formula Weights</Typography>
        <Typography variant="body2" color={total === 100 ? 'success.main' : 'error.main'} sx={{ mb: 2 }}>
          Total: {total}% {total !== 100 && '(must sum to 100%)'}
        </Typography>
        <Stack spacing={3} sx={{ maxWidth: 500 }}>
          {PARAMS.map((param) => (
            <Box key={param.key}>
              <Typography variant="body2" gutterBottom>
                {param.label}: <strong>{weights[param.key] || 0}%</strong>
              </Typography>
              <Slider
                value={weights[param.key] || 0}
                min={0} max={100}
                onChange={(_, val) => setWeights({ ...weights, [param.key]: val as number })}
              />
            </Box>
          ))}
          <Button variant="contained" onClick={handleSave} disabled={saving || total !== 100}>
            {saving ? 'Saving...' : 'Save Weights'}
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
