import apiClient from './apiClient';
import type { ApiResponse, Submission } from '../utils/types';

export const submissionApi = {
  startExam: (examId: number) =>
    apiClient.get<ApiResponse<Submission>>(`/api/exams/${examId}/start`),

  submitExam: (examId: number) =>
    apiClient.post<ApiResponse<Submission>>(`/api/exams/${examId}/submit`),

  saveAnswer: (submissionId: number, questionId: number, selectedOptionId?: number, answerText?: string) =>
    apiClient.put<ApiResponse<void>>(`/api/submissions/${submissionId}/answers`, {
      questionId,
      selectedOptionId,
      answerText,
    }),

  getMySubmissions: () =>
    apiClient.get<ApiResponse<Submission[]>>('/api/my-submissions'),

  getExamSubmissions: (examId: number) =>
    apiClient.get<ApiResponse<Submission[]>>(`/api/exams/${examId}/submissions`),
};
