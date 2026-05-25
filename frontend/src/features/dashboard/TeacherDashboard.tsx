import { useEffect, useState } from 'react';
import { Card, Table, Tag, Typography, Button, Space, Statistic, Row, Col } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import type { Exam } from '../../utils/types';

export function TeacherDashboard() {
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    examApi.getMyExams().then((res: { data: { data: Exam[] } }) => {
      setExams(res.data.data);
      setLoading(false);
    });
  }, []);

  const columns = [
    { title: 'Title', dataIndex: 'title', key: 'title' },
    {
      title: 'Status',
      key: 'status',
      render: (_: unknown, record: Exam) => (
        <Tag color={record.isPublished ? 'green' : 'orange'}>
          {record.isPublished ? 'Published' : 'Draft'}
        </Tag>
      ),
    },
    {
      title: 'Duration',
      dataIndex: 'durationMinutes',
      key: 'duration',
      render: (v: number) => `${v} min`,
    },
    {
      title: 'Start Time',
      dataIndex: 'startTime',
      key: 'startTime',
      render: (v: string) => new Date(v).toLocaleString(),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, record: Exam) => (
        <Space>
          <Button size="small" onClick={() => navigate(`/exams/${record.id}/edit`)}>
            Edit
          </Button>
          <Button size="small" onClick={() => navigate(`/exams/${record.id}/submissions`)}>
            Results
          </Button>
        </Space>
      ),
    },
  ];

  const published = exams.filter((e) => e.isPublished).length;

  return (
    <div>
      <Typography.Title level={4}>Teacher Dashboard</Typography.Title>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card><Statistic title="Total Exams" value={exams.length} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="Published" value={published} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="Drafts" value={exams.length - published} /></Card>
        </Col>
      </Row>
      <Button
        type="primary"
        icon={<PlusOutlined />}
        onClick={() => navigate('/exams/new')}
        style={{ marginBottom: 16 }}
      >
        Create Exam
      </Button>
      <Table dataSource={exams} columns={columns} rowKey="id" loading={loading} />
    </div>
  );
}
