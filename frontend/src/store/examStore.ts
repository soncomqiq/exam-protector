import { create } from 'zustand';
import type { Submission } from '../utils/types';

interface ExamSessionState {
  submission: Submission | null;
  currentQuestionIndex: number;
  answers: Record<number, { selectedOptionId?: number; answerText?: string }>;
  setSubmission: (submission: Submission) => void;
  setCurrentQuestion: (index: number) => void;
  setAnswer: (questionId: number, answer: { selectedOptionId?: number; answerText?: string }) => void;
  reset: () => void;
}

export const useExamSessionStore = create<ExamSessionState>((set) => ({
  submission: null,
  currentQuestionIndex: 0,
  answers: {},

  setSubmission: (submission) => set({ submission }),
  setCurrentQuestion: (index) => set({ currentQuestionIndex: index }),
  setAnswer: (questionId, answer) =>
    set((state) => ({
      answers: { ...state.answers, [questionId]: answer },
    })),
  reset: () => set({ submission: null, currentQuestionIndex: 0, answers: {} }),
}));
