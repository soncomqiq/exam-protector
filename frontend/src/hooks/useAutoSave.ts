import { useEffect, useRef, useCallback } from 'react';
import { submissionApi } from '../api/submissionApi';

export function useAutoSave(
  submissionId: number | null,
  answers: Record<number, { selectedOptionId?: number; answerText?: string }>,
  intervalMs = 10000
) {
  const lastSavedRef = useRef<string>('');

  const save = useCallback(async () => {
    if (!submissionId) return;
    const current = JSON.stringify(answers);
    if (current === lastSavedRef.current) return;

    for (const [questionId, answer] of Object.entries(answers)) {
      await submissionApi.saveAnswer(
        submissionId,
        Number(questionId),
        answer.selectedOptionId,
        answer.answerText
      );
    }
    lastSavedRef.current = current;
  }, [submissionId, answers]);

  useEffect(() => {
    if (!submissionId) return;
    const id = setInterval(save, intervalMs);
    return () => clearInterval(id);
  }, [submissionId, save, intervalMs]);

  return { saveNow: save };
}
