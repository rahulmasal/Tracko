import api from './api';

export interface QuotationItem {
  id?: number;
  quotationId?: number;
  itemDescription: string;
  quantity: number;
  unitPrice: number;
  discountPercent: number;
  discountAmount: number;
  taxPercent: number;
  taxAmount: number;
  netAmount: number;
}

export interface Quotation {
  id: number;
  quoteNumber: string;
  customerId: number;
  enquiryId: number | null;
  createdBy: number;
  subject: string;
  status: string;
  subtotal: number;
  discountPercent: number;
  discountAmount: number;
  taxPercent: number;
  taxAmount: number;
  totalAmount: number;
  commercialTerms: string;
  validityDays: number;
  validUntil: string;
  remarks: string;
  customerResponse: string | null;
  createdAt: string;
  customer: {
    id: number;
    companyName: string;
    contactPerson: string;
  };
  items: QuotationItem[];
}

export async function getQuotations(params: {
  status?: string;
  customerId?: number;
  createdBy?: number;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/quotations', { params });
  return response.data;
}

export async function getQuotationById(id: number): Promise<Quotation> {
  const response = await api.get(`/quotations/${id}`);
  return response.data;
}

export async function createQuotation(data: Partial<Quotation>) {
  const response = await api.post('/quotations', data);
  return response.data;
}

export async function updateQuotation(id: number, data: Partial<Quotation>) {
  const response = await api.put(`/quotations/${id}`, data);
  return response.data;
}

export async function submitForApproval(id: number) {
  const response = await api.put(`/quotations/${id}/submit-approval`);
  return response.data;
}

export async function approveQuotation(id: number, remarks: string) {
  const response = await api.put(`/quotations/${id}/approve`, { remarks });
  return response.data;
}

export async function sendQuotation(id: number) {
  const response = await api.put(`/quotations/${id}/send`);
  return response.data;
}

export async function recordCustomerResponse(id: number, response_: string, remarks?: string) {
  const response = await api.put(`/quotations/${id}/customer-response`, { response: response_, remarks });
  return response.data;
}

export async function getPendingApprovals() {
  const response = await api.get('/quotations/pending-approval');
  return response.data;
}

export async function getQuotationTimeline(id: number) {
  const response = await api.get(`/quotations/${id}/timeline`);
  return response.data;
}
