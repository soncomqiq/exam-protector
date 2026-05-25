import { useEffect, useRef } from 'react';
import { violationApi } from '../api/violationApi';

export function useDevToolsDetect(submissionId: number | null) {
  const reportedRef = useRef(false);

  useEffect(() => {
    if (!submissionId) return;

    const threshold = 160;
    const check = () => {
      const widthDiff = window.outerWidth - window.innerWidth > threshold;
      const heightDiff = window.outerHeight - window.innerHeight > threshold;
      if ((widthDiff || heightDiff) && !reportedRef.current) {
        reportedRef.current = true;
        violationApi.report({
          submissionId,
          violationType: 'DEVTOOLS_OPEN',
          severity: 'HIGH',
          clientTimestamp: new Date().toISOString(),
        });
        // Reset after a delay to allow re-detection
        setTimeout(() => {
          reportedRef.current = false;
        }, 5000);
      }
    };

    const id = setInterval(check, 1000);
    return () => clearInterval(id);
  }, [submissionId]);
}
