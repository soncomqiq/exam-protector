import { useState, useRef, useCallback, useEffect } from 'react';
import { violationApi } from '../api/violationApi';
import { recordingApi } from '../api/recordingApi';

const CHUNK_INTERVAL_MS = 30_000; // Upload a chunk every 30 seconds

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
  const recorderRef = useRef<MediaRecorder | null>(null);
  const chunkTimerRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const chunkStartRef = useRef<number>(0);

  const uploadBlob = useCallback((blob: Blob, durationMs: number) => {
    if (!submissionId || blob.size === 0) return;
    recordingApi.uploadChunk(submissionId, blob, durationMs).catch(console.error);
  }, [submissionId]);

  const startRecording = useCallback((stream: MediaStream) => {
    const mimeType = MediaRecorder.isTypeSupported('video/webm;codecs=vp9')
      ? 'video/webm;codecs=vp9'
      : 'video/webm';

    const recorder = new MediaRecorder(stream, { mimeType, videoBitsPerSecond: 500_000 });
    recorderRef.current = recorder;

    let chunks: Blob[] = [];
    chunkStartRef.current = Date.now();

    recorder.ondataavailable = (e) => {
      if (e.data.size > 0) chunks.push(e.data);
    };

    recorder.onstop = () => {
      if (chunks.length > 0) {
        const blob = new Blob(chunks, { type: mimeType });
        const duration = Date.now() - chunkStartRef.current;
        uploadBlob(blob, duration);
        chunks = [];
      }
    };

    recorder.start();

    // Periodically stop/restart to flush chunks to server
    chunkTimerRef.current = setInterval(() => {
      if (recorder.state === 'recording') {
        recorder.stop();
        // Restart after a small delay
        setTimeout(() => {
          if (streamRef.current && streamRef.current.active) {
            chunks = [];
            chunkStartRef.current = Date.now();
            recorder.start();
          }
        }, 100);
      }
    }, CHUNK_INTERVAL_MS);
  }, [uploadBlob]);

  const stopRecording = useCallback(() => {
    if (chunkTimerRef.current) {
      clearInterval(chunkTimerRef.current);
      chunkTimerRef.current = null;
    }
    if (recorderRef.current && recorderRef.current.state === 'recording') {
      recorderRef.current.stop();
    }
    recorderRef.current = null;
  }, []);

  const startSharing = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: { displaySurface: 'monitor' } as MediaTrackConstraints,
        audio: false,
      });

      // Reject if user picked a window or tab instead of entire screen
      const track = stream.getVideoTracks()[0];
      const settings = track.getSettings() as MediaTrackSettings & { displaySurface?: string };
      if (settings.displaySurface && settings.displaySurface !== 'monitor') {
        stream.getTracks().forEach((t) => t.stop());
        return false;
      }

      streamRef.current = stream;

      // Start recording
      startRecording(stream);

      track.onended = () => {
        stopRecording();
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
      stopRecording();
      streamRef.current?.getTracks().forEach((t) => t.stop());
    };
  }, [stopRecording]);

  return { status, graceRemaining, startSharing, resumeSharing };
}
