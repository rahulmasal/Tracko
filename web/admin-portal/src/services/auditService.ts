import api from './api';

export async function getAuditLogs(params: {
  userId?: number;
  action?: string;
  entityType?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/admin/audit-logs', { params });
  return response.data;
}

export async function getAuditLogDetail(id: number) {
  const response = await api.get(`/admin/audit-logs/${id}`);
  return response.data;
}

export async function getSecurityEvents(params: {
  userId?: number;
  eventType?: string;
  severity?: string;
  resolved?: boolean;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/admin/security-events', { params });
  return response.data;
}

export async function resolveSecurityEvent(id: number, notes: string) {
  const response = await api.put(`/admin/security-events/${id}/resolve`, { notes });
  return response.data;
}

export async function getSecurityStats() {
  const response = await api.get('/admin/security-events/stats');
  return response.data;
}
