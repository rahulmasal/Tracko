import api from './api';

export async function getReports(params: {
  type?: string;
  startDate?: string;
  endDate?: string;
  branchId?: number;
  format?: string;
}) {
  const response = await api.get('/admin/reports', { params });
  return response.data;
}

export async function exportReport(params: {
  type: string;
  startDate: string;
  endDate: string;
  format: 'excel' | 'pdf';
}) {
  const response = await api.get('/admin/reports/export', {
    params,
    responseType: 'blob',
  });
  return response.data;
}

export async function getAttendanceTrend(days: number = 30) {
  const response = await api.get('/admin/reports/attendance-trend', { params: { days } });
  return response.data;
}

export async function getBranchWiseAttendance(params: { date?: string; startDate?: string; endDate?: string }) {
  const response = await api.get('/admin/reports/branch-attendance', { params });
  return response.data;
}

export async function getQuotationTurnaround(params: { startDate?: string; endDate?: string }) {
  const response = await api.get('/admin/reports/quotation-turnaround', { params });
  return response.data;
}

export async function getEnquiryConversion(params: { startDate?: string; endDate?: string }) {
  const response = await api.get('/admin/reports/enquiry-conversion', { params });
  return response.data;
}
