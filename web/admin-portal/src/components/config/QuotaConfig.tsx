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

export default function QuotaConfig() {
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
      const data = await getConfig('QUOTATION');
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
      enqueueSnackbar('Quotation config updated', { variant: 'success' });
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
        <Typography variant="h6" gutterBottom>Quotation Configuration</Typography>
        <Stack spacing={2} sx={{ maxWidth: 500 }}>
          <TextField label="SLA Hours" type="number" size="small" value={config['quotation.sla_hours'] || ''}
            onChange={(e) => setConfig({ ...config, 'quotation.sla_hours': e.target.value })} />
          <TextField label="Approval Threshold Amount" type="number" size="small" value={config['quotation.approval_threshold'] || ''}
            onChange={(e) => setConfig({ ...config, 'quotation.approval_threshold': e.target.value })} />
          <TextField label="Auto Expire Days" type="number" size="small" value={config['quotation.auto_expire_days'] || ''}
            onChange={(e) => setConfig({ ...config, 'quotation.auto_expire_days': e.target.value })} />
          <FormControlLabel control={
            <Switch checked={config['quotation.requires_approval'] === 'true'}
              onChange={(e) => setConfig({ ...config, 'quotation.requires_approval': e.target.checked ? 'true' : 'false' })} />
          } label="Requires Approval" />
          <Button variant="contained" onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
