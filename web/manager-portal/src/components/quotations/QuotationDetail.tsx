import { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import TextField from '@mui/material/TextField';
import {
  getQuotationById, approveQuotation, sendQuotation,
  recordCustomerResponse, getQuotationTimeline,
  Quotation,
} from '../../services/quotationService';
import StatusChip from '../shared/StatusChip';
import DataTable from '../shared/DataTable';
import { formatDate, formatDateTime, formatCurrency } from '../../utils/helpers';
import QuotationTimeline from './QuotationTimeline';
import { useSnackbar } from 'notistack';

export default function QuotationDetail() {
  const { id } = useParams<{ id: string }>();
  const [quotation, setQuotation] = useState<Quotation | null>(null);
  const [timeline, setTimeline] = useState<unknown[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [approvalDialog, setApprovalDialog] = useState(false);
  const [approvalRemarks, setApprovalRemarks] = useState('');
  const [responseDialog, setResponseDialog] = useState<{ open: boolean; response_: string }>({ open: false, response_: '' });
  const [processing, setProcessing] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => { if (id) fetchQuotation(); }, [id]);

  const fetchQuotation = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const [qData, tData] = await Promise.all([
        getQuotationById(parseInt(id)),
        getQuotationTimeline(parseInt(id)),
      ]);
      setQuotation(qData);
      setTimeline(tData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async () => {
    if (!quotation) return;
    setProcessing(true);
    try {
      await approveQuotation(quotation.id, approvalRemarks);
      enqueueSnackbar('Quotation approved', { variant: 'success' });
      setApprovalDialog(false);
      fetchQuotation();
    } catch {
      enqueueSnackbar('Failed to approve', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const handleSend = async () => {
    if (!quotation) return;
    setProcessing(true);
    try {
      await sendQuotation(quotation.id);
      enqueueSnackbar('Quotation sent', { variant: 'success' });
      fetchQuotation();
    } catch {
      enqueueSnackbar('Failed to send', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const handleResponse = async () => {
    if (!quotation) return;
    setProcessing(true);
    try {
      await recordCustomerResponse(quotation.id, responseDialog.response_);
      enqueueSnackbar('Response recorded', { variant: 'success' });
      setResponseDialog({ open: false, response_: '' });
      fetchQuotation();
    } catch {
      enqueueSnackbar('Failed to record', { variant: 'error' });
    } finally {
      setProcessing(false);
    }
  };

  const itemCols = useMemo(() => [
    { field: 'itemDescription', headerName: 'Description', flex: 1, minWidth: 200 },
    { field: 'quantity', headerName: 'Qty', width: 80 },
    { field: 'unitPrice', headerName: 'Unit Price', width: 110, valueFormatter: (p: { value: number }) => formatCurrency(p.value) },
    { field: 'discountPercent', headerName: 'Disc %', width: 70 },
    { field: 'taxPercent', headerName: 'Tax %', width: 70 },
    { field: 'netAmount', headerName: 'Net Amount', width: 120, valueFormatter: (p: { value: number }) => formatCurrency(p.value), cellStyle: { fontWeight: 600 } },
  ], []);

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!quotation) return <Alert severity="warning">Quotation not found</Alert>;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Box>
          <Typography variant="h5" fontWeight={600}>{quotation.quoteNumber}</Typography>
          <Typography variant="body2" color="text.secondary">{quotation.subject}</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <StatusChip status={quotation.status} size="medium" />
          {quotation.validUntil && new Date(quotation.validUntil) < new Date() && quotation.status === 'sent' && (
            <Chip label="Expired" size="small" color="error" />
          )}
        </Box>
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 2 }}>
            <Box><Typography variant="caption" color="text.secondary">Customer</Typography>
              <Typography variant="body2" fontWeight={500}>{quotation.customer.companyName}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Contact</Typography>
              <Typography variant="body2">{quotation.customer.contactPerson || '-'}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Issue Date</Typography>
              <Typography variant="body2">{formatDateTime(quotation.createdAt)}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Valid Until</Typography>
              <Typography variant="body2">{quotation.validUntil ? formatDate(quotation.validUntil) : '-'}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Total Amount</Typography>
              <Typography variant="h6" color="primary" fontWeight={700}>{formatCurrency(quotation.totalAmount)}</Typography></Box>
            <Box><Typography variant="caption" color="text.secondary">Customer Response</Typography>
              <Typography variant="body2">{quotation.customerResponse ? quotation.customerResponse.replace(/_/g, ' ') : 'No response yet'}</Typography></Box>
          </Box>
          {quotation.commercialTerms && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="caption" color="text.secondary">Commercial Terms</Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{quotation.commercialTerms}</Typography>
            </Box>
          )}
        </CardContent>
      </Card>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Line Items</Typography>
          <DataTable rowData={quotation.items} columnDefs={itemCols} pagination={false} height={300} />
          <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end', gap: 4 }}>
            <Box><Typography variant="caption">Subtotal</Typography>
              <Typography variant="body2" fontWeight={600}>{formatCurrency(quotation.subtotal)}</Typography></Box>
            <Box><Typography variant="caption">Discount</Typography>
              <Typography variant="body2" color="error">{formatCurrency(quotation.discountAmount)}</Typography></Box>
            <Box><Typography variant="caption">Tax</Typography>
              <Typography variant="body2">{formatCurrency(quotation.taxAmount)}</Typography></Box>
            <Box><Typography variant="caption">Total</Typography>
              <Typography variant="h6" color="primary" fontWeight={700}>{formatCurrency(quotation.totalAmount)}</Typography></Box>
          </Box>
        </CardContent>
      </Card>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Timeline</Typography>
          <QuotationTimeline events={timeline as Array<{ event: string; userName: string; timestamp: string }>} />
        </CardContent>
      </Card>

      <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
        {quotation.status === 'pending_approval' && (
          <Button variant="contained" color="success" onClick={() => setApprovalDialog(true)}>
            Approve
          </Button>
        )}
        {quotation.status === 'approved' && (
          <Button variant="contained" onClick={handleSend} disabled={processing}>
            {processing ? 'Sending...' : 'Send to Customer'}
          </Button>
        )}
        {quotation.status === 'sent' && (
          <>
            <Button variant="outlined" color="success" onClick={() => setResponseDialog({ open: true, response_: 'accepted' })}>
              Customer Accepted
            </Button>
            <Button variant="outlined" color="warning" onClick={() => setResponseDialog({ open: true, response_: 'negotiating' })}>
              Negotiating
            </Button>
            <Button variant="outlined" color="error" onClick={() => setResponseDialog({ open: true, response_: 'rejected' })}>
              Customer Rejected
            </Button>
          </>
        )}
      </Box>

      <Dialog open={approvalDialog} onClose={() => setApprovalDialog(false)}>
        <DialogTitle>Approve Quotation</DialogTitle>
        <DialogContent>
          <TextField autoFocus margin="dense" label="Remarks" fullWidth multiline rows={3}
            value={approvalRemarks} onChange={(e) => setApprovalRemarks(e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setApprovalDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleApprove} disabled={processing}>Approve</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={responseDialog.open} onClose={() => setResponseDialog({ open: false, response_: '' })}>
        <DialogTitle>Record Customer Response</DialogTitle>
        <DialogContent>
          <Typography variant="body2" sx={{ mb: 2 }}>
            Mark as: <strong>{responseDialog.response_}</strong>
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setResponseDialog({ open: false, response_: '' })}>Cancel</Button>
          <Button variant="contained" onClick={handleResponse} disabled={processing}>Confirm</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
