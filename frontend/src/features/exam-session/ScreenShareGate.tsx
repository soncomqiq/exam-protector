import { useState } from 'react';
import { Card, Typography, Button, Alert } from 'antd';
import { VideoCameraOutlined } from '@ant-design/icons';

interface Props {
  onReady: () => void;
  startSharing: () => Promise<boolean>;
}

export function ScreenShareGate({ onReady, startSharing }: Props) {
  const [error, setError] = useState(false);

  const handleStart = async () => {
    const ok = await startSharing();
    if (ok) {
      onReady();
    } else {
      setError(true);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
      <Card style={{ maxWidth: 500, textAlign: 'center' }}>
        <VideoCameraOutlined style={{ fontSize: 48, color: '#1677ff', marginBottom: 16 }} />
        <Typography.Title level={4}>Screen Sharing Required</Typography.Title>
        <Typography.Paragraph>
          This exam requires screen sharing for integrity monitoring. Please share your
          <strong> entire screen</strong> when prompted.
        </Typography.Paragraph>
        <Typography.Paragraph type="secondary">
          Your screen will be monitored during the exam. If you stop sharing, you will have a grace
          period to resume before the exam is auto-submitted.
        </Typography.Paragraph>
        {error && (
          <Alert
            type="error"
            message="You must share your ENTIRE SCREEN (not a window or tab). Please try again and select the full screen option."
            style={{ marginBottom: 16 }}
          />
        )}
        <Button type="primary" size="large" onClick={handleStart}>
          Start Screen Sharing & Begin Exam
        </Button>
      </Card>
    </div>
  );
}
