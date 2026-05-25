import { useEffect, useRef } from 'react';
import { WS_BASE_URL, HEARTBEAT_INTERVAL_MS } from '../utils/constants';

export function useHeartbeat(submissionId: number | null, token: string | null) {
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    if (!submissionId || !token) return;

    const ws = new WebSocket(`${WS_BASE_URL}/ws/heartbeat?token=${token}`);
    wsRef.current = ws;

    let intervalId: ReturnType<typeof setInterval>;

    ws.onopen = () => {
      intervalId = setInterval(() => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(
            JSON.stringify({
              type: 'HEARTBEAT',
              submissionId,
              screenSharing: true,
              tabVisible: document.visibilityState === 'visible',
              timestamp: Date.now(),
            })
          );
        }
      }, HEARTBEAT_INTERVAL_MS);
    };

    ws.onclose = () => {
      if (intervalId) clearInterval(intervalId);
    };

    return () => {
      if (intervalId) clearInterval(intervalId);
      ws.close();
    };
  }, [submissionId, token]);
}
