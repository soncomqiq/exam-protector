import apiClient from './apiClient';
import type { ApiResponse } from '../utils/types';

interface AuthResponse {
  accessToken: string;
  fullName: string;
  email: string;
  role: string;
}

export const authApi = {
  login: (email: string, password: string) =>
    apiClient.post<ApiResponse<AuthResponse>>('/api/auth/login', { email, password }),

  register: (email: string, password: string, fullName: string) =>
    apiClient.post<ApiResponse<AuthResponse>>('/api/auth/register', { email, password, fullName }),

  refresh: () =>
    apiClient.post<ApiResponse<AuthResponse>>('/api/auth/refresh'),
};
