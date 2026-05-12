import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Stack from '@mui/material/Stack';
import Alert from '@mui/material/Alert';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import SendIcon from '@mui/icons-material/Send';
import { createQuotation, submitForApproval, QuotationItem } from '../../services/quotationService';
import Api from '../../services/api';
import { formatCurrency } from '../../utils/helpers';
import { useSnackbar } from 'notistack';
import { addDays, format } from 'date-fns';

interface FormItem extends QuotationItem {
  _key: number;
}

export default function QuotationForm() {
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const [customers, setCustomers] = useState<Array<{ id: number; companyName: string }>>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState({
    customerId: 0,
    subject: '',
    commercialTerms: '',
    validityDays: 30,
    remarks: '',
  });
  const [items, setItems] = useState<FormItem[]>([
    { _key: 1, itemDescription: '', quantity: 1, unitPrice: 0, discountPercent: 0, discountAmount: 0, taxPercent: 0, taxAmount: 0, netAmount: 0 },
  ]);

  useEffect(() => {
    Api.get('/customers', { params: { size: 500 } })
      .then((res) => setCustomers(res.data.content || res.data))
      .catch(() => {});
  }, []);

  const nextKey = () => Math.max(...items.map((i) => i._key), 0) + 1;

  const addItem = () => {
    setItems([...items, { _key: nextKey(), itemDescription: '', quantity: 1, unitPrice: 0, discountPercent: 0, discountAmount: 0, taxPercent: 0, taxAmount: 0, netAmount: 0 }]);
  };

  const removeItem = (key: number) => {
    if (items.length === 1) return;
    setItems(items.filter((i) => i._key !== key));
  };

  const updateItem = (key: number, field: string, value: number | string) => {
    setItems(items.map((item) => {
      if (item._key !== key) return item;
      const updated = { ...item, [field]: value };

      // Auto-calculate
      const qty = updated.quantity || 0;
      const price = updated.unitPrice || 0;
      const discPct = updated.discountPercent || 0;
      const taxPct = updated.taxPercent || 0;
      const lineTotal = qty * price;
      updated.discountAmount = lineTotal * (discPct / 100);
      const afterDiscount = lineTotal - updated.discountAmount;
      updated.taxAmount = afterDiscount * (taxPct / 100);
      updated.netAmount = afterDiscount + updated.taxAmount;

      return updated;
    }));
  };

  const calculateTotals = () => {
    const subtotal = items.reduce((s, i) => s + (i.quantity || 0) * (i.unitPrice || 0), 0);
    const discount = items.reduce((s, i) => s + (i.discountAmount || 0), 0);
    const tax = items.reduce((s, i) => s + (i.taxAmount || 0), 0);
    const total = items.reduce((s, i) => s + (i.netAmount || 0), 0);
    return { subtotal, discount, tax, total };
  };

  const handleSave = async (submitForApprovalFlag: boolean) => {
    if (!form.customerId || !form.subject) {
      setError('Customer and subject are required');
      return;
    }
    if (items.some((i) => !i.itemDescription)) {
      setError('All items need a description');
      return;
    }

    setSubmitting(true);
    setError(null);
    try {
      const totals = calculateTotals();
      const payload = {
        ...form,
        subtotal: totals.subtotal,
        discountPercent: totals.subtotal > 0 ? (totals.discount / totals.subtotal) * 100 : 0,
        discountAmount: totals.discount,
        taxPercent: totals.subtotal > 0 ? (totals.tax / totals.subtotal) * 100 : 0,
        taxAmount: totals.tax,
        totalAmount: totals.total,
        validUntil: format(addDays(new Date(), form.validityDays), 'yyyy-MM-dd'),
        items: items.map(({ _key, ...item }) => item),
      };

      const result = await createQuotation(payload);

      if (submitForApprovalFlag) {
        await submitForApproval(result.id);
        enqueueSnackbar('Quotation created and submitted for approval', { variant: 'success' });
      } else {
        enqueueSnackbar('Quotation saved as draft', { variant: 'success' });
      }

      navigate(`/quotations?id=${result.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save quotation');
    } finally {
      setSubmitting(false);
    }
  };

  const totals = calculateTotals();

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Create Quotation</Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Stack spacing={2}>
            <FormControl size="small" fullWidth>
              <InputLabel>Customer *</InputLabel>
              <Select value={form.customerId} label="Customer *" onChange={(e) => setForm({ ...form, customerId: e.target.value as number })}>
                {customers.map((c) => (
                  <MenuItem key={c.id} value={c.id}>{c.companyName}</MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField label="Subject *" fullWidth value={form.subject}
              onChange={(e) => setForm({ ...form, subject: e.target.value })} />
            <TextField label="Commercial Terms" fullWidth multiline rows={3} value={form.commercialTerms}
              onChange={(e) => setForm({ ...form, commercialTerms: e.target.value })} />
            <Stack direction="row" spacing={2}>
              <TextField label="Validity (days)" type="number" size="small" value={form.validityDays}
                onChange={(e) => setForm({ ...form, validityDays: parseInt(e.target.value) || 30 })} sx={{ width: 150 }} />
              <TextField label="Remarks" fullWidth size="small" value={form.remarks}
                onChange={(e) => setForm({ ...form, remarks: e.target.value })} />
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="h6">Line Items</Typography>
            <Button startIcon={<AddIcon />} onClick={addItem} size="small">Add Item</Button>
          </Box>

          {items.map((item, idx) => (
            <Box key={item._key} sx={{ p: 1.5, mb: 1, border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
              <Stack spacing={1}>
                <Stack direction="row" spacing={1} alignItems="center">
                  <Typography variant="caption" color="text.secondary" sx={{ minWidth: 30 }}>#{idx + 1}</Typography>
                  <TextField label="Description" size="small" fullWidth value={item.itemDescription}
                    onChange={(e) => updateItem(item._key, 'itemDescription', e.target.value)} />
                  <IconButton size="small" color="error" onClick={() => removeItem(item._key)} disabled={items.length === 1}>
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </Stack>
                <Stack direction="row" spacing={1}>
                  <TextField label="Qty" type="number" size="small" value={item.quantity} sx={{ width: 80 }}
                    onChange={(e) => updateItem(item._key, 'quantity', parseFloat(e.target.value) || 0)} />
                  <TextField label="Unit Price" type="number" size="small" value={item.unitPrice} sx={{ width: 120 }}
                    onChange={(e) => updateItem(item._key, 'unitPrice', parseFloat(e.target.value) || 0)} />
                  <TextField label="Disc %" type="number" size="small" value={item.discountPercent} sx={{ width: 80 }}
                    onChange={(e) => updateItem(item._key, 'discountPercent', parseFloat(e.target.value) || 0)} />
                  <TextField label="Tax %" type="number" size="small" value={item.taxPercent} sx={{ width: 80 }}
                    onChange={(e) => updateItem(item._key, 'taxPercent', parseFloat(e.target.value) || 0)} />
                  <TextField label="Net" size="small" value={formatCurrency(item.netAmount)} sx={{ width: 120 }}
                    InputProps={{ readOnly: true }} />
                </Stack>
              </Stack>
            </Box>
          ))}

          <Box sx={{ mt: 2, p: 2, bgcolor: '#f8fafc', borderRadius: 1 }}>
            <Stack direction="row" spacing={4} justifyContent="flex-end">
              <Box><Typography variant="caption" color="text.secondary">Subtotal</Typography>
                <Typography variant="body2" fontWeight={600}>{formatCurrency(totals.subtotal)}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Discount</Typography>
                <Typography variant="body2" color="error">{formatCurrency(totals.discount)}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Tax</Typography>
                <Typography variant="body2">{formatCurrency(totals.tax)}</Typography></Box>
              <Box><Typography variant="caption" color="text.secondary">Total</Typography>
                <Typography variant="h6" color="primary" fontWeight={700}>{formatCurrency(totals.total)}</Typography></Box>
            </Stack>
          </Box>
        </CardContent>
      </Card>

      <Stack direction="row" spacing={2} sx={{ mt: 3, justifyContent: 'flex-end' }}>
        <Button variant="outlined" onClick={() => navigate('/quotations')}>Cancel</Button>
        <Button variant="outlined" startIcon={<SaveIcon />} onClick={() => handleSave(false)} disabled={submitting}>
          Save Draft
        </Button>
        <Button variant="contained" startIcon={<SendIcon />} onClick={() => handleSave(true)} disabled={submitting}>
          {submitting ? 'Saving...' : 'Submit for Approval'}
        </Button>
      </Stack>
    </Box>
  );
}
