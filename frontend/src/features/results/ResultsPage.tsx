import { useEffect, useState } from 'react';
import { Table, Typography, Tag, Button, Modal, List, Space } from 'antd';
import { PlayCircleOutlined, VideoCameraOutlined } from '@ant-design/icons';
import { useParams } from 'react-router-dom';
import { submissionApi } from '../../api/submissionApi';
import { recordingApi } from '../../api/recordingApi';
import { API_BASE_URL } from '../../utils/constants';
import type { Submission } from '../../utils/types';

interface RecordingChunk {
  id: number;
  chunkIndex: number;
  fileSize: number;
  durationMs: number;
}

export function ResultsPage() {
  const { id: examId } = useParams();
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [loading, setLoading] = useState(true);
  const [recordingModal, setRecordingModal] = useState(false);
  const [recordings, setRecordings] = useState<RecordingChunk[]>([]);
  const [playingUrl, setPlayingUrl] = useState<string | null>(null);

  useEffect(() => {
    submissionApi.getExamSubmissions(Number(examId)).then((res: { data: { data: Submission[] } }) => {
      setSubmissions(res.data.data);
      setLoading(false);
    });
  }, [examId]);

  const openRecordings = async (submissionId: number) => {
    const chunks = await recordingApi.listBySubmission(submissionId);
    setRecordings(chunks);
    setPlayingUrl(null);
    setRecordingModal(true);
  };

  const playChunk = (recordingId: number) => {
    const token = sessionStorage.getItem('access_token') ?? '';
    setPlayingUrl(`${API_BASE_URL}/recordings/${recordingId}/stream?token=${token}`);
  };

  const statusColor = (status: string) => {
    switch (status) {
      case 'SUBMITTED': return 'green';
      case 'AUTO_SUBMITTED': return 'orange';
      case 'LOCKED': return 'red';
      default: return 'blue';
    }
  };

  const columns = [
    { title: 'Student', dataIndex: 'studentName', key: 'student' },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => <Tag color={statusColor(status)}>{status}</Tag>,
    },
    {
      title: 'Score',
      dataIndex: 'score',
      key: 'score',
      render: (score: number | null) => (score !== null ? `${score}%` : '-'),
    },
    {
      title: 'Started',
      dataIndex: 'startedAt',
      key: 'startedAt',
      render: (v: string) => new Date(v).toLocaleString(),
    },
    {
      title: 'Submitted',
      dataIndex: 'submittedAt',
      key: 'submittedAt',
      render: (v: string | null) => (v ? new Date(v).toLocaleString() : '-'),
    },
    {
      title: 'Recording',
      key: 'recording',
      render: (_: unknown, record: Submission) => (
        <Button
          icon={<VideoCameraOutlined />}
          size="small"
          onClick={() => openRecordings(record.id)}
        >
          Watch
        </Button>
      ),
    },
  ];

  const formatDuration = (ms: number) => {
    const secs = Math.floor(ms / 1000);
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m}:${String(s).padStart(2, '0')}`;
  };

  const formatSize = (bytes: number) => {
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  return (
    <div>
      <Typography.Title level={4}>Exam Results</Typography.Title>
      <Table dataSource={submissions} columns={columns} rowKey="id" loading={loading} />

      <Modal
        title="Screen Recordings"
        open={recordingModal}
        onCancel={() => { setRecordingModal(false); setPlayingUrl(null); }}
        footer={null}
        width={800}
      >
        {playingUrl && (
          <div style={{ marginBottom: 16 }}>
            <video
              src={playingUrl}
              controls
              autoPlay
              style={{ width: '100%', maxHeight: 450, background: '#000' }}
            />
          </div>
        )}

        {recordings.length === 0 ? (
          <Typography.Text type="secondary">No recordings available for this submission.</Typography.Text>
        ) : (
          <List
            size="small"
            dataSource={recordings}
            renderItem={(chunk) => (
              <List.Item
                actions={[
                  <Button
                    key="play"
                    type="link"
                    icon={<PlayCircleOutlined />}
                    onClick={() => playChunk(chunk.id)}
                  >
                    Play
                  </Button>,
                ]}
              >
                <Space>
                  <Tag>Segment {chunk.chunkIndex + 1}</Tag>
                  <span>{formatDuration(chunk.durationMs)}</span>
                  <Typography.Text type="secondary">{formatSize(chunk.fileSize)}</Typography.Text>
                </Space>
              </List.Item>
            )}
          />
        )}
      </Modal>
    </div>
  );
}
