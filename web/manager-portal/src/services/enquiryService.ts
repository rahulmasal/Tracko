import api from './api';

export interface Enquiry {
  id: number;
  customerId: number;
  assignedTo: number | null;
  subject: string;
  description: string;
  priority: string;
  source: string;
  status: string;
  expectedCloseDate: string | null;
  closedAt: string | null;
  createdAt: string;
  customer: {
    id: number;
    companyName: string;
    contactPerson: string;
    phone: string;
  };
  assignedToUser?: {
    id: number;
    firstName: string;
    lastName: string;
  };
}

export async function getEnquiries(params: {
  status?: string;
  priority?: string;
  assignedTo?: number;
  customerId?: number;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/enquiries', { params });
  return response.data;
}

export async function getEnquiryById(id: number): Promise<Enquiry> {
  const response = await api.get(`/enquiries/${id}`);
  return response.data;
}

export async function updateEnquiryStatus(id: number, status: string, reason?: string) {
  const response = await api.put(`/enquiries/${id}/status`, { status, reason });
  return response.data;
}

export async function assignEnquiry(id: number, userId: number) {
  const response = await api.put(`/enquiries/${id}/assign`, { assignedTo: userId });
  return response.data;
}

export async function getFollowUps(enquiryId: number) {
  const response = await api.get(`/enquiries/${enquiryId}/followups`);
  return response.data;
}

export async function addFollowUp(enquiryId: number, data: {
  followupType: string;
  notes: string;
  nextFollowupAt?: string;
}) {
  const response = await api.post(`/enquiries/${enquiryId}/followups`, data);
  return response.data;
}

export async function getEnquiryStats() {
  const response = await api.get('/enquiries/stats');
  return response.data;
}

export async function getDueFollowUps() {
  const response = await api.get('/enquiries/followups/due');
  return response.data;
}
