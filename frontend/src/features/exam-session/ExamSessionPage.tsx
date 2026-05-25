import { useEffect, useState, useCallback } from 'react';
import { Card, Button, Radio, Input, Typography, Space, Modal, message, Progress } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { submissionApi } from '../../api/submissionApi';
import { useAuthStore } from '../../store/authStore';
import { useExamSessionStore } from '../../store/examStore';
import { useTabMonitor } from '../../hooks/useTabMonitor';
import { useScreenShare } from '../../hooks/useScreenShare';
import { useHeartbeat } from '../../hooks/useHeartbeat';
import { useAutoSave } from '../../hooks/useAutoSave';
import { useDevToolsDetect } from '../../hooks/useDevToolsDetect';
import { CountdownTimer } from '../../components/CountdownTimer';
import { ScreenShareGate } from './ScreenShareGate';

export function ExamSessionPage() {
  const { examId } = useParams();
  const navigate = useNavigate();
  const token = useAuthStore((s) => s.token);
  const { submission, setSubmission, currentQuestionIndex, setCurrentQuestion, answers, setAnswer, reset } =
    useExamSessionStore();
  const [loading, setLoading] = useState(true);
  const [screenShareReady, setScreenShareReady] = useState(false);
  const [locked, setLocked] = useState(false);

  const handleLockout = useCallback(() => {
    setLocked(true);
    Modal.error({
      title: 'Exam Locked',
      content: 'Too many violations detected. Your exam has been locked.',
      onOk: () => navigate('/dashboard'),
    });
  }, [navigate]);

  const handleTimeUp = useCallback(async () => {
    if (submission) {
      await submissionApi.submitExam(Number(examId));
      message.info('Time is up! Exam auto-submitted.');
      reset();
      navigate('/dashboard');
    }
  }, [submission, examId, navigate, reset]);

  // Anti-cheat hooks
  useTabMonitor(submission?.id ?? null, 3, handleLockout);
  useHeartbeat(submission?.id ?? null, token);
  useDevToolsDetect(submission?.id ?? null);

  const { status: screenStatus, graceRemaining, startSharing, resumeSharing } = useScreenShare(
    submission?.id ?? null,
    30,
    handleTimeUp
  );

  const { saveNow } = useAutoSave(submission?.id ?? null, answers);

  useEffect(() => {
    const start = async () => {
      try {
        const res = await submissionApi.startExam(Number(examId));
        setSubmission(res.data.data);
      } catch (err: any) {
        message.error(err?.response?.data?.message || 'Failed to start exam');
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    };
    start();
    return () => reset();
  }, [examId]);

  const handleSubmit = async () => {
    Modal.confirm({
      title: 'Submit Exam',
      content: 'Are you sure you want to submit? This cannot be undone.',
      onOk: async () => {
        await saveNow();
        await submissionApi.submitExam(Number(examId));
        message.success('Exam submitted successfully!');
        reset();
        navigate('/dashboard');
      },
    });
  };

  if (loading) return <Typography.Text>Loading exam...</Typography.Text>;
  if (!submission) return <Typography.Text>Exam not available</Typography.Text>;
  if (locked) return <Typography.Text>Exam has been locked due to violations.</Typography.Text>;

  // Screen share gate
  if (!screenShareReady) {
    return (
      <ScreenShareGate
        onReady={() => setScreenShareReady(true)}
        startSharing={startSharing}
      />
    );
  }

  // Grace period overlay
  if (screenStatus === 'GRACE') {
    return (
      <div style={{ textAlign: 'center', padding: 48 }}>
        <Typography.Title level={3} type="warning">
          Screen Share Stopped!
        </Typography.Title>
        <Typography.Text>Please resume screen sharing within {graceRemaining} seconds.</Typography.Text>
        <br /><br />
        <Button type="primary" size="large" onClick={resumeSharing}>
          Resume Screen Sharing
        </Button>
      </div>
    );
  }

  const questions = submission.questions;
  const currentQuestion = questions[currentQuestionIndex];
  const currentAnswer = currentQuestion ? answers[currentQuestion.id] : undefined;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Typography.Title level={4} style={{ margin: 0 }}>{submission.examTitle}</Typography.Title>
        <CountdownTimer
          startedAt={submission.startedAt}
          durationMinutes={Math.ceil((new Date(submission.startedAt).getTime() + 60 * 60000 - Date.now()) / 60000)}
          onTimeUp={handleTimeUp}
        />
      </div>

      <Progress percent={Math.round(((currentQuestionIndex + 1) / questions.length) * 100)} size="small" />

      {currentQuestion && (
        <Card style={{ marginTop: 16 }}>
          <Typography.Title level={5}>
            Question {currentQuestionIndex + 1} of {questions.length}
          </Typography.Title>
          <Typography.Paragraph>{currentQuestion.questionText}</Typography.Paragraph>
          <Typography.Text type="secondary">({currentQuestion.points} points)</Typography.Text>

          {currentQuestion.questionType === 'MCQ' && (
            <Radio.Group
              style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 16 }}
              value={currentAnswer?.selectedOptionId}
              onChange={(e) => setAnswer(currentQuestion.id, { selectedOptionId: e.target.value })}
            >
              {currentQuestion.options.map((opt) => (
                <Radio key={opt.id} value={opt.id}>{opt.optionText}</Radio>
              ))}
            </Radio.Group>
          )}

          {currentQuestion.questionType === 'SHORT_ANSWER' && (
            <Input
              style={{ marginTop: 16 }}
              placeholder="Your answer"
              value={currentAnswer?.answerText || ''}
              onChange={(e) => setAnswer(currentQuestion.id, { answerText: e.target.value })}
            />
          )}

          {currentQuestion.questionType === 'ESSAY' && (
            <Input.TextArea
              style={{ marginTop: 16 }}
              rows={6}
              placeholder="Write your essay here..."
              value={currentAnswer?.answerText || ''}
              onChange={(e) => setAnswer(currentQuestion.id, { answerText: e.target.value })}
            />
          )}
        </Card>
      )}

      <Space style={{ marginTop: 16 }}>
        <Button disabled={currentQuestionIndex === 0} onClick={() => setCurrentQuestion(currentQuestionIndex - 1)}>
          Previous
        </Button>
        {currentQuestionIndex < questions.length - 1 ? (
          <Button type="primary" onClick={() => setCurrentQuestion(currentQuestionIndex + 1)}>
            Next
          </Button>
        ) : (
          <Button type="primary" danger onClick={handleSubmit}>
            Submit Exam
          </Button>
        )}
      </Space>
    </div>
  );
}
