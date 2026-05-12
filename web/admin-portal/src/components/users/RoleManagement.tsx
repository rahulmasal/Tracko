import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Divider from '@mui/material/Divider';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { getRoles, getPermissions, updateRolePermissions } from '../../services/adminService';
import { useSnackbar } from 'notistack';

export default function RoleManagement() {
  const [roles, setRoles] = useState<Array<{ id: number; name: string; description: string }>>([]);
  const [permissions, setPermissions] = useState<Array<{ id: number; code: string; name: string; module: string }>>([]);
  const [selectedRole, setSelectedRole] = useState<number | null>(null);
  const [rolePermissions, setRolePermissions] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [rData, pData] = await Promise.all([getRoles(), getPermissions()]);
      setRoles(rData);
      setPermissions(pData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const selectRole = async (roleId: number) => {
    setSelectedRole(roleId);
    try {
      const role = roles.find((r) => r.id === roleId);
      if (role) {
        const permIds = (role as Record<string, unknown>).permissionIds as number[] || [];
        setRolePermissions(permIds);
      }
    } catch {
      setRolePermissions([]);
    }
  };

  const togglePermission = (permId: number) => {
    setRolePermissions((prev) =>
      prev.includes(permId) ? prev.filter((id) => id !== permId) : [...prev, permId],
    );
  };

  const handleSave = async () => {
    if (!selectedRole) return;
    setSaving(true);
    try {
      await updateRolePermissions(selectedRole, rolePermissions);
      enqueueSnackbar('Permissions updated', { variant: 'success' });
    } catch {
      enqueueSnackbar('Failed to update', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const modules = [...new Set(permissions.map((p) => p.module))].sort();

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Box sx={{ display: 'flex', gap: 2, height: 'calc(100vh - 180px)' }}>
      <Card sx={{ width: 250, overflow: 'auto' }}>
        <CardContent>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom>Roles</Typography>
          <List dense>
            {roles.map((role) => (
              <ListItem key={role.id} disablePadding>
                <ListItemButton
                  selected={selectedRole === role.id}
                  onClick={() => selectRole(role.id)}
                  sx={{ borderRadius: 1 }}
                >
                  <ListItemText primary={role.name} secondary={role.description} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>

      <Card sx={{ flex: 1, overflow: 'auto' }}>
        <CardContent>
          {selectedRole ? (
            <>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="subtitle1" fontWeight={600}>
                  Permissions - {roles.find((r) => r.id === selectedRole)?.name}
                </Typography>
                <Button variant="contained" onClick={handleSave} disabled={saving}>
                  {saving ? 'Saving...' : 'Save Permissions'}
                </Button>
              </Box>
              {modules.map((module) => (
                <Box key={module} sx={{ mb: 2 }}>
                  <Typography variant="subtitle2" color="primary" sx={{ mb: 0.5 }}>{module}</Typography>
                  <Divider sx={{ mb: 1 }} />
                  <FormGroup>
                    {permissions.filter((p) => p.module === module).map((perm) => (
                      <FormControlLabel
                        key={perm.id}
                        control={
                          <Checkbox
                            checked={rolePermissions.includes(perm.id)}
                            onChange={() => togglePermission(perm.id)}
                            size="small"
                          />
                        }
                        label={<Typography variant="body2">{perm.name} ({perm.code})</Typography>}
                      />
                    ))}
                  </FormGroup>
                </Box>
              ))}
            </>
          ) : (
            <Typography variant="body2" color="text.secondary" sx={{ py: 4, textAlign: 'center' }}>
              Select a role to manage permissions
            </Typography>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
