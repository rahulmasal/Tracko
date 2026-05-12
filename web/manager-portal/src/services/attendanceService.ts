import api from './api';

export interface AttendanceRecord {
  id: number;
  userId: number;
  date: string;
  checkInTime: string | null;
  checkOutTime: string | null;
  status: string;
  lateMinutes: number;
  earlyExitMinutes: number;
  workHours: number;
  user: {
    id: number;
    firstName: string;
    lastName: string;
    employeeId: string;
  };
}

export interface AttendanceCorrection {
  id: number;
  userId: number;
  attendanceId: number | null;
  correctionType: string;
  requestedDate: string;
  reason: string;
  status: string;
  user: {
    firstName: string;
    lastName: string;
    employeeId: string;
  };
}

export interface AttendanceSummary {
  present: number;
  late: number;
  absent: number;
  halfDay: number;
  leave: number;
  total: number;
}

export async function getAttendance(params: {
  date?: string;
  startDate?: string;
  endDate?: string;
  userId?: number;
  status?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/attendance', { params });
  return response.data;
}

export async function getAttendanceSummary(params: {
  date?: string;
  startDate?: string;
  endDate?: string;
}): Promise<AttendanceSummary> {
  const response = await api.get('/attendance/summary', { params });
  return response.data;
}

export async function getCorrections(params: {
  status?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/attendance/corrections', { params });
  return response.data;
}

export async function approveCorrection(id: number, remarks: string) {
  const response = await api.put(`/attendance/corrections/${id}/approve`, { remarks });
  return response.data;
}

export async function rejectCorrection(id: number, remarks: string) {
  const response = await api.put(`/attendance/corrections/${id}/reject`, { remarks });
  return response.data;
}

export async function getMissedCheckins(params: { date?: string }) {
  const response = await api.get('/attendance/missed-checkins', { params });
  return response.data;
}
