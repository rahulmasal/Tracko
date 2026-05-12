import { useSearchParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import QuotationList from '../components/quotations/QuotationList';
import QuotationDetail from '../components/quotations/QuotationDetail';
import QuotationForm from '../components/quotations/QuotationForm';

export default function QuotationsPage() {
  const [params] = useSearchParams();
  const quoteId = params.get('id');
  const isNew = params.get('new') !== null;

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>
        {quoteId ? 'Quotation Detail' : isNew ? 'Create Quotation' : 'Quotations'}
      </Typography>
      {quoteId ? <QuotationDetail /> : isNew ? <QuotationForm /> : <QuotationList />}
    </Box>
  );
}
