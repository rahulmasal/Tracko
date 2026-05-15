import { create } from 'zustand';
import { login as apiLogin, logout as apiLogout, getProfile } from '../services/authService';
import { getToken, clearToken } from '../../shared/utils/auth';

interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  employeeId: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  loadUser: () => Promise<void>;
  clearError: () => void;
}

export const useAuth = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: !!getToken(),
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      const response = await apiLogin({ email, password });
      set({ user: response.user, isAuthenticated: true, isLoading: false });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Login failed';
      set({ error: message, isLoading: false });
      throw err;
    }
  },

  logout: async () => {
    set({ isLoading: true });
    try { await apiLogout(); } catch { /* ignore */ }
    finally { clearToken(); set({ user: null, isAuthenticated: false, isLoading: false }); }
  },

  loadUser: async () => {
    const token = getToken();
    if (!token) { set({ user: null, isAuthenticated: false }); return; }
    set({ isLoading: true });
    try {
      const profile = await getProfile();
      set({ user: profile, isAuthenticated: true, isLoading: false });
    } catch {
      clearToken();
      set({ user: null, isAuthenticated: false, isLoading: false });
    }
  },

  clearError: () => set({ error: null }),
}));
