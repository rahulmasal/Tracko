import api from './api';

export interface LeaveRequest {
  id: number;
  userId: number;
  leaveType: string;
  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: string;
  isHalfDay: boolean;
  emergencyContact: string;
  reviewedBy: number | null;
  reviewRemarks: string | null;
  createdAt: string;
  user: {
    id: number;
    firstName: string;
    lastName: string;
    employeeId: string;
  };
}

export interface LeaveBalance {
  leaveType: string;
  totalAllocated: number;
  used: number;
  pending: number;
  remaining: number;
  carriedForward: number;
}

export async function getLeaveRequests(params: {
  status?: string;
  userId?: number;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/leaves', { params });
  return response.data;
}

export async function getPendingLeaves() {
  const response = await api.get('/leaves/pending');
  return response.data;
}

export async function approveLeave(id: number, remarks: string) {
  const response = await api.put(`/leaves/${id}/approve`, { remarks });
  return response.data;
}

export async function rejectLeave(id: number, remarks: string) {
  const response = await api.put(`/leaves/${id}/reject`, { remarks });
  return response.data;
}

export async function getTeamLeaves(params: { month: number; year: number }) {
  const response = await api.get('/leaves/team', { params });
  return response.data;
}

export async function getLeaveBalances(userId?: number): Promise<LeaveBalance[]> {
  const params = userId ? { userId } : {};
  const response = await api.get('/leaves/balances', { params });
  return response.data;
}

export async function getLeaveStats() {
  const response = await api.get('/leaves/stats');
  return response.data;
}
