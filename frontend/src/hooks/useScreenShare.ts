import { useState, useRef, useCallback, useEffect } from 'react';
import { violationApi } from '../api/violationApi';

type ScreenShareStatus = 'IDLE' | 'ACTIVE' | 'GRACE' | 'EXPIRED';

export function useScreenShare(
  submissionId: number | null,
  gracePeriodSec: number,
  onGraceExpired: () => void
) {
  const [status, setStatus] = useState<ScreenShareStatus>('IDLE');
  const [graceRemaining, setGraceRemaining] = useState(gracePeriodSec);
  const streamRef = useRef<MediaStream | null>(null);
  const countdownRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const startSharing = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: { displaySurface: 'monitor' } as MediaTrackConstraints,
        audio: false,
      });
      streamRef.current = stream;
      const track = stream.getVideoTracks()[0];

      track.onended = () => {
        setStatus('GRACE');
        setGraceRemaining(gracePeriodSec);

        if (submissionId) {
          violationApi.report({
            submissionId,
            violationType: 'SCREEN_SHARE_STOPPED',
            severity: 'HIGH',
            clientTimestamp: new Date().toISOString(),
          });
        }

        let remaining = gracePeriodSec;
        countdownRef.current = setInterval(() => {
          remaining -= 1;
          setGraceRemaining(remaining);
          if (remaining <= 0) {
            if (countdownRef.current) clearInterval(countdownRef.current);
            setStatus('EXPIRED');
            onGraceExpired();
          }
        }, 1000);
      };

      setStatus('ACTIVE');
      return true;
    } catch {
      setStatus('IDLE');
      return false;
    }
  }, [submissionId, gracePeriodSec, onGraceExpired]);

  const resumeSharing = useCallback(async () => {
    if (countdownRef.current) clearInterval(countdownRef.current);
    const ok = await startSharing();
    if (ok) {
      setGraceRemaining(gracePeriodSec);
    }
    return ok;
  }, [startSharing, gracePeriodSec]);

  useEffect(() => {
    return () => {
      if (countdownRef.current) clearInterval(countdownRef.current);
      streamRef.current?.getTracks().forEach((t) => t.stop());
    };
  }, []);

  return { status, graceRemaining, startSharing, resumeSharing };
}
