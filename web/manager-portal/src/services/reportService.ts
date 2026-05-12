import api from './api';

export interface CallReport {
  id: number;
  visitId: number | null;
  userId: number;
  customerId: number;
  reportDate: string;
  visitType: string;
  workDescription: string;
  findings: string;
  recommendations: string;
  status: string;
  submittedAt: string | null;
  approvedAt: string | null;
  approvedBy: number | null;
  managerRemarks: string;
  customer: {
    id: number;
    companyName: string;
  };
  user: {
    id: number;
    firstName: string;
    lastName: string;
    employeeId: string;
  };
}

export async function getReports(params: {
  status?: string;
  userId?: number;
  customerId?: number;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/reports', { params });
  return response.data;
}

export async function getPendingReports() {
  const response = await api.get('/reports/pending');
  return response.data;
}

export async function getReportById(id: number): Promise<CallReport> {
  const response = await api.get(`/reports/${id}`);
  return response.data;
}

export async function approveReport(id: number, remarks: string) {
  const response = await api.put(`/reports/${id}/approve`, { managerRemarks: remarks });
  return response.data;
}

export async function requestRework(id: number, remarks: string) {
  const response = await api.put(`/reports/${id}/rework`, { managerRemarks: remarks });
  return response.data;
}

export async function getPendingCount() {
  const response = await api.get('/reports/pending/count');
  return response.data;
}
