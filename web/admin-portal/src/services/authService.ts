import api from './api';
import { setToken, setRefreshToken, clearToken } from '../../shared/utils/auth';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    roles: string[];
    employeeId: string;
    profilePictureUrl: string | null;
  };
}

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>('/auth/login', data);
  const { token, refreshToken } = response.data;
  setToken(token);
  setRefreshToken(refreshToken);
  return response.data;
}

export async function logout(): Promise<void> {
  try {
    await api.post('/auth/logout');
  } catch {
    // ignore logout errors
  } finally {
    clearToken();
  }
}

export async function refreshToken(): Promise<string> {
  const response = await api.post<{ token: string }>('/auth/refresh');
  setToken(response.data.token);
  return response.data.token;
}

export async function getProfile() {
  const response = await api.get('/auth/profile');
  return response.data;
}
