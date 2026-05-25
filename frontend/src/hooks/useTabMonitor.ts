import { useEffect, useRef, useCallback } from 'react';
import { violationApi } from '../api/violationApi';

export function useTabMonitor(
  submissionId: number | null,
  maxViolations: number,
  onLockout: () => void
) {
  const countRef = useRef(0);

  const reportViolation = useCallback(
    async (type: string) => {
      if (!submissionId) return;
      countRef.current += 1;
      const severity = countRef.current >= maxViolations ? 'CRITICAL' : 'MEDIUM';

      await violationApi.report({
        submissionId,
        violationType: type,
        severity,
        clientTimestamp: new Date().toISOString(),
        details: JSON.stringify({ count: countRef.current, max: maxViolations }),
      });

      if (countRef.current >= maxViolations) {
        onLockout();
      }
    },
    [submissionId, maxViolations, onLockout]
  );

  useEffect(() => {
    if (!submissionId) return;

    const onVisChange = () => {
      if (document.visibilityState === 'hidden') {
        reportViolation('TAB_HIDDEN');
      }
    };

    const onBlur = () => reportViolation('TAB_SWITCH');

    const block = (e: Event) => {
      e.preventDefault();
      return false;
    };

    document.addEventListener('visibilitychange', onVisChange);
    window.addEventListener('blur', onBlur);
    document.addEventListener('copy', block);
    document.addEventListener('paste', block);
    document.addEventListener('contextmenu', block);

    return () => {
      document.removeEventListener('visibilitychange', onVisChange);
      window.removeEventListener('blur', onBlur);
      document.removeEventListener('copy', block);
      document.removeEventListener('paste', block);
      document.removeEventListener('contextmenu', block);
    };
  }, [submissionId, reportViolation]);

  return { violationCount: countRef.current };
}
