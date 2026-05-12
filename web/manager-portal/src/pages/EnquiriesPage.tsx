import { useSearchParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import EnquiryList from '../components/enquiries/EnquiryList';
import EnquiryDetail from '../components/enquiries/EnquiryDetail';

export default function EnquiriesPage() {
  const [params] = useSearchParams();
  const enquiryId = params.get('id');

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>{enquiryId ? 'Enquiry Detail' : 'Enquiries'}</Typography>
      {enquiryId ? <EnquiryDetail /> : <EnquiryList />}
    </Box>
  );
}
