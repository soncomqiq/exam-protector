import { useEffect, useState } from 'react';
import { Card, List, Typography, Button, Tag, Row, Col, Statistic } from 'antd';
import { useNavigate } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import { submissionApi } from '../../api/submissionApi';
import type { Exam, Submission } from '../../utils/types';

export function StudentDashboard() {
  const [exams, setExams] = useState<Exam[]>([]);
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      examApi.getAvailableExams(),
      submissionApi.getMySubmissions(),
    ]).then(([examsRes, subsRes]) => {
      setExams(examsRes.data.data);
      setSubmissions(subsRes.data.data);
      setLoading(false);
    });
  }, []);

  const completedCount = submissions.filter((s) => s.status !== 'IN_PROGRESS').length;

  return (
    <div>
      <Typography.Title level={4}>Student Dashboard</Typography.Title>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card><Statistic title="Available Exams" value={exams.length} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="Completed" value={completedCount} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="In Progress" value={submissions.filter((s) => s.status === 'IN_PROGRESS').length} /></Card>
        </Col>
      </Row>

      <Typography.Title level={5}>Available Exams</Typography.Title>
      <List
        loading={loading}
        grid={{ gutter: 16, column: 2 }}
        dataSource={exams}
        renderItem={(exam) => {
          const sub = submissions.find((s) => s.examId === exam.id);
          const now = new Date();
          const started = new Date(exam.startTime) <= now;
          const ended = new Date(exam.endTime) < now;

          return (
            <List.Item>
              <Card
                title={exam.title}
                extra={
                  sub ? (
                    <Tag color={sub.status === 'SUBMITTED' ? 'green' : 'blue'}>{sub.status}</Tag>
                  ) : ended ? (
                    <Tag color="red">Ended</Tag>
                  ) : !started ? (
                    <Tag color="orange">Not Started</Tag>
                  ) : null
                }
              >
                <p>{exam.description || 'No description'}</p>
                <p>Duration: {exam.durationMinutes} minutes</p>
                <p>Start: {new Date(exam.startTime).toLocaleString()}</p>
                {started && !ended && !sub && (
                  <Button type="primary" onClick={() => navigate(`/exam/${exam.id}/session`)}>
                    Start Exam
                  </Button>
                )}
                {sub && sub.status !== 'IN_PROGRESS' && sub.score !== null && (
                  <p><strong>Score: {sub.score}%</strong></p>
                )}
                {sub && sub.status === 'IN_PROGRESS' && (
                  <Button type="primary" onClick={() => navigate(`/exam/${exam.id}/session`)}>
                    Resume Exam
                  </Button>
                )}
              </Card>
            </List.Item>
          );
        }}
      />
    </div>
  );
}
