import { useEffect, useState } from 'react';
import { Table, Typography, Tag } from 'antd';
import { useParams } from 'react-router-dom';
import { submissionApi } from '../../api/submissionApi';
import type { Submission } from '../../utils/types';

export function ResultsPage() {
  const { examId } = useParams();
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    submissionApi.getExamSubmissions(Number(examId)).then((res: { data: { data: Submission[] } }) => {
      setSubmissions(res.data.data);
      setLoading(false);
    });
  }, [examId]);

  const statusColor = (status: string) => {
    switch (status) {
      case 'SUBMITTED': return 'green';
      case 'AUTO_SUBMITTED': return 'orange';
      case 'LOCKED': return 'red';
      default: return 'blue';
    }
  };

  const columns = [
    { title: 'Student', dataIndex: 'examTitle', key: 'student' },
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
  ];

  return (
    <div>
      <Typography.Title level={4}>Exam Results</Typography.Title>
      <Table dataSource={submissions} columns={columns} rowKey="id" loading={loading} />
    </div>
  );
}
