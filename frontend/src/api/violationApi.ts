import apiClient from './apiClient';
import type { ApiResponse, ViolationResult } from '../utils/types';

export interface ViolationReport {
  submissionId: number;
  violationType: string;
  severity: string;
  clientTimestamp: string;
  details?: string;
}

export const violationApi = {
  report: (data: ViolationReport) =>
    apiClient.post<ApiResponse<ViolationResult>>('/api/violations', data),

  getBySubmission: (submissionId: number) =>
    apiClient.get<ApiResponse<unknown[]>>(`/api/violations/${submissionId}`),
};
