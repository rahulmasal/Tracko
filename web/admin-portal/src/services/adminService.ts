import api from './api';

// User Management
export async function getUsers(params: {
  page?: number;
  size?: number;
  isActive?: boolean;
  branchId?: number;
  departmentId?: number;
}) {
  const response = await api.get('/admin/users', { params });
  return response.data;
}

export async function getUserById(id: number) {
  const response = await api.get(`/admin/users/${id}`);
  return response.data;
}

export async function createUser(data: Record<string, unknown>) {
  const response = await api.post('/admin/users', data);
  return response.data;
}

export async function updateUser(id: number, data: Record<string, unknown>) {
  const response = await api.put(`/admin/users/${id}`, data);
  return response.data;
}

export async function toggleUserActive(id: number) {
  const response = await api.put(`/admin/users/${id}/toggle-active`);
  return response.data;
}

export async function resetPassword(id: number, newPassword: string) {
  const response = await api.put(`/admin/users/${id}/reset-password`, { newPassword });
  return response.data;
}

// Role Management
export async function getRoles() {
  const response = await api.get('/admin/roles');
  return response.data;
}

export async function getPermissions() {
  const response = await api.get('/admin/permissions');
  return response.data;
}

export async function updateRolePermissions(roleId: number, permissionIds: number[]) {
  const response = await api.put(`/admin/roles/${roleId}/permissions`, { permissionIds });
  return response.data;
}

// Branch Management
export async function getBranches(params?: Record<string, unknown>) {
  const response = await api.get('/admin/branches', { params });
  return response.data;
}

export async function createBranch(data: Record<string, unknown>) {
  const response = await api.post('/admin/branches', data);
  return response.data;
}

export async function updateBranch(id: number, data: Record<string, unknown>) {
  const response = await api.put(`/admin/branches/${id}`, data);
  return response.data;
}

// Shift Management
export async function getShifts() {
  const response = await api.get('/admin/shifts');
  return response.data;
}

export async function createShift(data: Record<string, unknown>) {
  const response = await api.post('/admin/shifts', data);
  return response.data;
}

export async function updateShift(id: number, data: Record<string, unknown>) {
  const response = await api.put(`/admin/shifts/${id}`, data);
  return response.data;
}

// Config Management
export async function getConfig(module?: string) {
  const response = await api.get('/admin/config', { params: { module } });
  return response.data;
}

export async function updateConfig(key: string, value: string) {
  const response = await api.put(`/admin/config/${key}`, { configValue: value });
  return response.data;
}

export async function getDepartments() {
  const response = await api.get('/admin/departments');
  return response.data;
}
