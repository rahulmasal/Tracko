import api from './api';

export interface Visit {
  id: number;
  userId: number;
  customerId: number;
  visitType: string;
  status: string;
  scheduledAt: string;
  checkInTime: string | null;
  checkOutTime: string | null;
  purpose: string;
  remarks: string;
  customer: {
    id: number;
    companyName: string;
    contactPerson: string;
    phone: string;
  };
  user: {
    id: number;
    firstName: string;
    lastName: string;
  };
}

export async function getVisits(params: {
  userId?: number;
  status?: string;
  startDate?: string;
  endDate?: string;
  customerId?: number;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/visits', { params });
  return response.data;
}

export async function getVisitById(id: number): Promise<Visit> {
  const response = await api.get(`/visits/${id}`);
  return response.data;
}

export async function getTodayVisits() {
  const response = await api.get('/visits/today');
  return response.data;
}

export async function getVisitStats(params: {
  startDate?: string;
  endDate?: string;
}) {
  const response = await api.get('/visits/stats', { params });
  return response.data;
}
