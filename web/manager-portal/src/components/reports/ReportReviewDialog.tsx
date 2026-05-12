import { useState } from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import CircularProgress from '@mui/material/CircularProgress';
import { CallReport, approveReport, requestRework } from '../../services/reportService';
import StatusChip from '../shared/StatusChip';
import { formatDateTime } from '../../utils/helpers';
import { useSnackbar } from 'notistack';

interface ReportReviewDialogProps {
  open: boolean;
  onClose: () => void;
  report: CallReport;
  onReviewComplete: () => void;
}

export default function ReportReviewDialog({
  open, onClose, report, onReviewComplete,
}: ReportReviewDialogProps) {
  const [remarks, setRemarks] = useState('');
  const [processing, setProcessing] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const handleApprove = async () => {
    setProcessing(true);
    try {
      await approveReport(report.id, remarks);
      enqueueSnackbar('Report approved', { variant: 'success' });
      onReviewComplete();
      onClose();
    } catch {
      enqueueSnackbar('Failed to approve', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const handleRework = async () => {
    if (!remarks) return;
    setProcessing(true);
    try {
      await requestRework(report.id, remarks);
      enqueueSnackbar('Rework requested', { variant: 'info' });
      onReviewComplete();
      onClose();
    } catch {
      enqueueSnackbar('Failed to request rework', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        Report Review
        <StatusChip status={report.status} />
      </DialogTitle>
      <DialogContent dividers>
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2, mb: 2 }}>
          <Box>
            <Typography variant="caption" color="text.secondary">Customer</Typography>
            <Typography variant="body2">{report.customer.companyName}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">Engineer</Typography>
            <Typography variant="body2">{report.user.firstName} {report.user.lastName} ({report.user.employeeId})</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">Date</Typography>
            <Typography variant="body2">{report.reportDate}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">Visit Type</Typography>
            <Typography variant="body2">{report.visitType}</Typography>
          </Box>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Typography variant="subtitle2" gutterBottom>Work Description</Typography>
        <Typography variant="body2" sx={{ mb: 2, whiteSpace: 'pre-wrap' }}>{report.workDescription || '-'}</Typography>

        {report.findings && (
          <>
            <Typography variant="subtitle2" gutterBottom>Findings</Typography>
            <Typography variant="body2" sx={{ mb: 2, whiteSpace: 'pre-wrap' }}>{report.findings}</Typography>
          </>
        )}

        {report.recommendations && (
          <>
            <Typography variant="subtitle2" gutterBottom>Recommendations</Typography>
            <Typography variant="body2" sx={{ mb: 2, whiteSpace: 'pre-wrap' }}>{report.recommendations}</Typography>
          </>
        )}

        {report.submittedAt && (
          <Typography variant="caption" color="text.secondary">
            Submitted: {formatDateTime(report.submittedAt)}
          </Typography>
        )}

        {report.status === 'submitted' || report.status === 'rework_submitted' ? (
          <>
            <Divider sx={{ my: 2 }} />
            <Typography variant="subtitle2" gutterBottom>Manager Review</Typography>
            <TextField
              fullWidth
              multiline
              rows={3}
              placeholder="Enter review remarks..."
              value={remarks}
              onChange={(e) => setRemarks(e.target.value)}
              sx={{ mb: 2 }}
            />
          </>
        ) : (
          report.managerRemarks && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" gutterBottom>Manager Remarks</Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{report.managerRemarks}</Typography>
            </>
          )
        )}
      </DialogContent>
      <DialogActions sx={{ p: 2 }}>
        <Button onClick={onClose}>Close</Button>
        {(report.status === 'submitted' || report.status === 'rework_submitted') && (
          <>
            <Button
              variant="outlined"
              color="warning"
              onClick={handleRework}
              disabled={processing || !remarks}
            >
              {processing ? <CircularProgress size={20} /> : 'Request Rework'}
            </Button>
            <Button
              variant="contained"
              color="success"
              onClick={handleApprove}
              disabled={processing}
            >
              {processing ? <CircularProgress size={20} /> : 'Approve'}
            </Button>
          </>
        )}
      </DialogActions>
    </Dialog>
  );
}
