import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import { getConfig, updateConfig } from '../../services/adminService';
import { useSnackbar } from 'notistack';

const FIELDS = [
  { key: 'leave.allocation.casual', label: 'Casual Leave (annual)', type: 'number' },
  { key: 'leave.allocation.sick', label: 'Sick Leave (annual)', type: 'number' },
  { key: 'leave.allocation.earned', label: 'Earned Leave (annual)', type: 'number' },
  { key: 'leave.carry_forward.casual', label: 'Casual Leave Carry Forward', type: 'number' },
  { key: 'leave.carry_forward.sick', label: 'Sick Leave Carry Forward', type: 'number' },
  { key: 'leave.carry_forward.earned', label: 'Earned Leave Carry Forward', type: 'number' },
  { key: 'leave.max_consecutive_days', label: 'Max Consecutive Days', type: 'number' },
  { key: 'leave.min_days_notice', label: 'Min Days Notice', type: 'number' },
];

export default function LeavePolicyConfig() {
  const [config, setConfig] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchConfig(); }, []);

  const fetchConfig = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getConfig('LEAVE');
      const cfgMap: Record<string, string> = {};
      (Array.isArray(data) ? data : []).forEach((item: { configKey: string; configValue: string }) => {
        cfgMap[item.configKey] = item.configValue;
      });
      setConfig(cfgMap);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      for (const [key, value] of Object.entries(config)) {
        await updateConfig(key, value);
      }
      enqueueSnackbar('Leave policy updated', { variant: 'success' });
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
        <Typography variant="h6" gutterBottom>Leave Policy Configuration</Typography>
        <Stack spacing={2} sx={{ maxWidth: 500 }}>
          {FIELDS.map((field) => (
            <TextField key={field.key} label={field.label} type={field.type} size="small"
              value={config[field.key] || ''}
              onChange={(e) => setConfig({ ...config, [field.key]: e.target.value })} />
          ))}
          <FormControlLabel control={
            <Switch checked={config['leave.policy_requires_attachment'] === 'true'}
              onChange={(e) => setConfig({ ...config, 'leave.policy_requires_attachment': e.target.checked ? 'true' : 'false' })} />
          } label="Require Attachment for Leave" />
          <Button variant="contained" onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
