import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';

export interface User {
  id: number;
  employeeCode: string;
  fullName: string;
  email: string;
  mobile: string;
  isActive: boolean;
  isLocked: boolean;
  designation: string;
  departmentId: number;
  branchId: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export function useUsers(params: { page?: number; size?: number; isActive?: boolean } = {}) {
  return useQuery({
    queryKey: ['users', params],
    queryFn: async () => {
      const response = await api.get('/admin/users', { params });
      return response.data as PagedResponse<User>;
    },
    staleTime: 30000,
  });
}

export function useUser(id: number) {
  return useQuery({
    queryKey: ['users', id],
    queryFn: async () => {
      const response = await api.get(`/admin/users/${id}`);
      return response.data as User;
    },
    enabled: !!id,
  });
}

export function useCreateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: Partial<User>) => {
      const response = await api.post('/admin/users', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: Partial<User> }) => {
      const response = await api.put(`/admin/users/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });
}
