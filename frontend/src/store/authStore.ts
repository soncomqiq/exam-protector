import { create } from 'zustand';
import type { User } from '../utils/types';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  loadFromStorage: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,

  login: (token, user) => {
    sessionStorage.setItem('access_token', token);
    sessionStorage.setItem('user', JSON.stringify(user));
    set({ token, user, isAuthenticated: true });
  },

  logout: () => {
    sessionStorage.removeItem('access_token');
    sessionStorage.removeItem('user');
    set({ token: null, user: null, isAuthenticated: false });
  },

  loadFromStorage: () => {
    const token = sessionStorage.getItem('access_token');
    const userStr = sessionStorage.getItem('user');
    if (token && userStr) {
      const user = JSON.parse(userStr) as User;
      set({ token, user, isAuthenticated: true });
    }
  },
}));
