import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import Slider from '@mui/material/Slider';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import { getConfig, updateConfig } from '../../services/adminService';
import { useSnackbar } from 'notistack';

export default function SecurityPolicyConfig() {
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
      const data = await getConfig('SECURITY');
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
      enqueueSnackbar('Security config updated', { variant: 'success' });
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
        <Typography variant="h6" gutterBottom>Security Policy Configuration</Typography>
        <Stack spacing={3} sx={{ maxWidth: 500 }}>
          <Box>
            <Typography variant="body2" gutterBottom>Risk Threshold: Low ({config['security.risk_threshold_low'] || '30'})</Typography>
            <Slider value={parseInt(config['security.risk_threshold_low'] || '30')} min={0} max={100}
              onChange={(_, val) => setConfig({ ...config, 'security.risk_threshold_low': String(val) })} />
          </Box>
          <Box>
            <Typography variant="body2" gutterBottom>Risk Threshold: Medium ({config['security.risk_threshold_medium'] || '60'})</Typography>
            <Slider value={parseInt(config['security.risk_threshold_medium'] || '60')} min={0} max={100}
              onChange={(_, val) => setConfig({ ...config, 'security.risk_threshold_medium': String(val) })} />
          </Box>
          <Box>
            <Typography variant="body2" gutterBottom>Risk Threshold: High ({config['security.risk_threshold_high'] || '80'})</Typography>
            <Slider value={parseInt(config['security.risk_threshold_high'] || '80')} min={0} max={100}
              onChange={(_, val) => setConfig({ ...config, 'security.risk_threshold_high': String(val) })} />
          </Box>
          <FormControl size="small" fullWidth>
            <InputLabel>Rooted Device Action</InputLabel>
            <Select value={config['security.rooted_device_action'] || 'warn'} label="Rooted Device Action"
              onChange={(e) => setConfig({ ...config, 'security.rooted_device_action': e.target.value })}>
              <MenuItem value="warn">Warn</MenuItem>
              <MenuItem value="block">Block</MenuItem>
              <MenuItem value="log">Log Only</MenuItem>
            </Select>
          </FormControl>
          <FormControl size="small" fullWidth>
            <InputLabel>Mock Location Action</InputLabel>
            <Select value={config['security.mock_location_action'] || 'block'} label="Mock Location Action"
              onChange={(e) => setConfig({ ...config, 'security.mock_location_action': e.target.value })}>
              <MenuItem value="warn">Warn</MenuItem>
              <MenuItem value="block">Block</MenuItem>
              <MenuItem value="log">Log Only</MenuItem>
            </Select>
          </FormControl>
          <FormControl size="small" fullWidth>
            <InputLabel>Developer Options Action</InputLabel>
            <Select value={config['security.dev_options_action'] || 'warn'} label="Developer Options Action"
              onChange={(e) => setConfig({ ...config, 'security.dev_options_action': e.target.value })}>
              <MenuItem value="warn">Warn</MenuItem>
              <MenuItem value="block">Block</MenuItem>
              <MenuItem value="log">Log Only</MenuItem>
            </Select>
          </FormControl>
          <Button variant="contained" onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
