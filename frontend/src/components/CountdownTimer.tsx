import { useEffect, useState } from 'react';
import { Typography } from 'antd';

interface Props {
  startedAt: string;
  durationMinutes: number;
  onTimeUp: () => void;
}

export function CountdownTimer({ startedAt, durationMinutes, onTimeUp }: Props) {
  const [remaining, setRemaining] = useState('');

  useEffect(() => {
    const endTime = new Date(startedAt).getTime() + durationMinutes * 60 * 1000;

    const id = setInterval(() => {
      const now = Date.now();
      const diff = endTime - now;

      if (diff <= 0) {
        setRemaining('00:00');
        clearInterval(id);
        onTimeUp();
        return;
      }

      const minutes = Math.floor(diff / 60000);
      const seconds = Math.floor((diff % 60000) / 1000);
      setRemaining(`${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`);
    }, 1000);

    return () => clearInterval(id);
  }, [startedAt, durationMinutes, onTimeUp]);

  const isWarning = remaining && parseInt(remaining) < 5;

  return (
    <Typography.Text
      strong
      style={{ fontSize: 18, color: isWarning ? '#ff4d4f' : undefined }}
    >
      ⏱ {remaining}
    </Typography.Text>
  );
}
