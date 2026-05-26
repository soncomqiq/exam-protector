export interface User {
  email: string;
  fullName: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}

export interface Exam {
  id: number;
  title: string;
  description: string;
  createdByName: string;
  durationMinutes: number;
  startTime: string;
  endTime: string;
  maxTabViolations: number;
  screenShareRequired: boolean;
  gracePeriodSeconds: number;
  isPublished: boolean;
  createdAt: string;
}

export interface Question {
  id: number;
  questionText: string;
  questionType: 'MCQ' | 'SHORT_ANSWER' | 'ESSAY';
  points: number;
  sortOrder: number;
  options: QuestionOption[];
}

export interface QuestionOption {
  id: number;
  optionText: string;
  sortOrder: number;
}

export interface Submission {
  id: number;
  examId: number;
  examTitle: string;
  studentName: string;
  studentEmail: string;
  status: 'IN_PROGRESS' | 'SUBMITTED' | 'AUTO_SUBMITTED' | 'LOCKED';
  startedAt: string;
  submittedAt: string | null;
  score: number | null;
  questions: Question[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string | null;
  data: T;
}

export interface ViolationResult {
  action: 'WARNING' | 'LOCKED';
  count: number;
}
