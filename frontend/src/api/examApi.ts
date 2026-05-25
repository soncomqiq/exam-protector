import apiClient from './apiClient';
import type { ApiResponse, Exam, Question } from '../utils/types';

export interface ExamRequest {
  title: string;
  description?: string;
  durationMinutes: number;
  startTime: string;
  endTime: string;
  maxTabViolations?: number;
  screenShareRequired?: boolean;
  gracePeriodSeconds?: number;
  isPublished?: boolean;
}

export interface QuestionRequest {
  questionText: string;
  questionType: 'MCQ' | 'SHORT_ANSWER' | 'ESSAY';
  points?: number;
  sortOrder?: number;
  options?: { optionText: string; isCorrect: boolean; sortOrder?: number }[];
}

export const examApi = {
  getMyExams: () =>
    apiClient.get<ApiResponse<Exam[]>>('/api/exams'),

  getAvailableExams: () =>
    apiClient.get<ApiResponse<Exam[]>>('/api/exams/available'),

  getExam: (id: number) =>
    apiClient.get<ApiResponse<Exam>>(`/api/exams/${id}`),

  createExam: (data: ExamRequest) =>
    apiClient.post<ApiResponse<Exam>>('/api/exams', data),

  updateExam: (id: number, data: ExamRequest) =>
    apiClient.put<ApiResponse<Exam>>(`/api/exams/${id}`, data),

  deleteExam: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/exams/${id}`),

  addQuestion: (examId: number, data: QuestionRequest) =>
    apiClient.post<ApiResponse<Question>>(`/api/exams/${examId}/questions`, data),

  getQuestions: (examId: number) =>
    apiClient.get<ApiResponse<Question[]>>(`/api/exams/${examId}/questions`),
};
