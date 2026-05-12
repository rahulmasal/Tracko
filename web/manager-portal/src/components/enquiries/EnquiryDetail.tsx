import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import TextField from '@mui/material/TextField';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import Timeline from '@mui/lab/Timeline';
import TimelineItem from '@mui/lab/TimelineItem';
import TimelineSeparator from '@mui/lab/TimelineSeparator';
import TimelineConnector from '@mui/lab/TimelineConnector';
import TimelineContent from '@mui/lab/TimelineContent';
import TimelineDot from '@mui/lab/TimelineDot';
import { getEnquiryById, getFollowUps, addFollowUp, Enquiry } from '../../services/enquiryService';
import StatusChip from '../shared/StatusChip';
import { formatDateTime } from '../../utils/helpers';
import { useSnackbar } from 'notistack';

export default function EnquiryDetail() {
  const { id } = useParams<{ id: string }>();
  const [enquiry, setEnquiry] = useState<Enquiry | null>(null);
  const [followUps, setFollowUps] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newFollowUp, setNewFollowUp] = useState({ followupType: 'call', notes: '', nextFollowupAt: '' });
  const [submitting, setSubmitting] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    if (id) fetchEnquiry();
  }, [id]);

  const fetchEnquiry = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const [enqData, fuData] = await Promise.all([
        getEnquiryById(parseInt(id)),
        getFollowUps(parseInt(id)),
      ]);
      setEnquiry(enqData);
      setFollowUps(fuData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleAddFollowUp = async () => {
    if (!id || !newFollowUp.notes) return;
    setSubmitting(true);
    try {
      await addFollowUp(parseInt(id), newFollowUp);
      enqueueSnackbar('Follow-up added', { variant: 'success' });
      setNewFollowUp({ followupType: 'call', notes: '', nextFollowupAt: '' });
      fetchEnquiry();
    } catch {
      enqueueSnackbar('Failed to add follow-up', { variant: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!enquiry) return <Alert severity="warning">Enquiry not found</Alert>;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5" fontWeight={600}>{enquiry.subject}</Typography>
        <StatusChip status={enquiry.status} size="medium" />
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 2 }}>
            <Box><Typography variant="caption" color="text.secondary">Customer</Typography>
              <Typography variant="body2">{enquiry.customer.companyName}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Contact</Typography>
              <Typography variant="body2">{enquiry.customer.contactPerson} | {enquiry.customer.phone}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Priority</Typography>
              <Chip label={enquiry.priority} size="small" color={enquiry.priority === 'urgent' ? 'error' : enquiry.priority === 'high' ? 'warning' : 'default'} /></Box>
            <Box><Typography variant="caption" color="text.secondary">Source</Typography>
              <Typography variant="body2">{enquiry.source || '-'}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Created</Typography>
              <Typography variant="body2">{formatDateTime(enquiry.createdAt)}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Assigned To</Typography>
              <Typography variant="body2">{enquiry.assignedToUser ? `${enquiry.assignedToUser.firstName} ${enquiry.assignedToUser.lastName}` : 'Unassigned'}</Typography></Box>
          </Box>
          {enquiry.description && (
            <Box sx={{ mt: 2 }}><Typography variant="caption" color="text.secondary">Description</Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{enquiry.description}</Typography></Box>
          )}
        </CardContent>
      </Card>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Follow-up Timeline</Typography>
          {followUps.length === 0 ? (
            <Typography variant="body2" color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>No follow-ups yet</Typography>
          ) : (
            <Timeline>
              {(followUps as Array<{ id: number; followupType: string; notes: string; nextFollowupAt: string; createdAt: string }>).map((fu) => (
                <TimelineItem key={fu.id}>
                  <TimelineSeparator>
                    <TimelineDot color="primary" />
                    <TimelineConnector />
                  </TimelineSeparator>
                  <TimelineContent>
                    <Typography variant="body2" fontWeight={500}>{fu.followupType}</Typography>
                    <Typography variant="body2">{fu.notes}</Typography>
                    {fu.nextFollowupAt && (
                      <Typography variant="caption" color="text.secondary">Next: {formatDateTime(fu.nextFollowupAt)}</Typography>
                    )}
                    <Typography variant="caption" color="text.disabled" display="block">{formatDateTime(fu.createdAt)}</Typography>
                  </TimelineContent>
                </TimelineItem>
              ))}
            </Timeline>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>Add Follow-up</Typography>
          <Stack spacing={2}>
            <FormControl size="small" fullWidth>
              <InputLabel>Type</InputLabel>
              <Select value={newFollowUp.followupType} label="Type" onChange={(e) => setNewFollowUp({ ...newFollowUp, followupType: e.target.value })}>
                <MenuItem value="call">Call</MenuItem>
                <MenuItem value="email">Email</MenuItem>
                <MenuItem value="visit">Visit</MenuItem>
                <MenuItem value="meeting">Meeting</MenuItem>
                <MenuItem value="whatsapp">WhatsApp</MenuItem>
              </Select>
            </FormControl>
            <TextField label="Notes" multiline rows={3} value={newFollowUp.notes} onChange={(e) => setNewFollowUp({ ...newFollowUp, notes: e.target.value })} />
            <TextField type="datetime-local" label="Next Follow-up" value={newFollowUp.nextFollowupAt} onChange={(e) => setNewFollowUp({ ...newFollowUp, nextFollowupAt: e.target.value })} InputLabelProps={{ shrink: true }} />
            <Button variant="contained" onClick={handleAddFollowUp} disabled={submitting || !newFollowUp.notes}>
              {submitting ? 'Adding...' : 'Add Follow-up'}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
