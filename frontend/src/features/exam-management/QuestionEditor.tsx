import { useEffect, useState } from 'react';
import { Card, Button, Form, Input, Select, InputNumber, List, Typography, message, Space } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useParams } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import type { QuestionRequest } from '../../api/examApi';
import type { Question } from '../../utils/types';

export function QuestionEditor() {
  const { id } = useParams();
  const examId = Number(id);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [options, setOptions] = useState<{ optionText: string; isCorrect: boolean }[]>([
    { optionText: '', isCorrect: false },
    { optionText: '', isCorrect: false },
  ]);

  useEffect(() => {
    loadQuestions();
  }, [examId]);

  const loadQuestions = async () => {
    const res = await examApi.getQuestions(examId);
    setQuestions(res.data.data);
  };

  const handleAddQuestion = async (values: any) => {
    setLoading(true);
    try {
      const data: QuestionRequest = {
        questionText: values.questionText,
        questionType: values.questionType,
        points: values.points,
        sortOrder: questions.length,
        options: values.questionType === 'MCQ' ? options.filter((o) => o.optionText.trim()) : undefined,
      };

      await examApi.addQuestion(examId, data);
      message.success('Question added');
      form.resetFields();
      setOptions([{ optionText: '', isCorrect: false }, { optionText: '', isCorrect: false }]);
      loadQuestions();
    } catch {
      message.error('Failed to add question');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Typography.Title level={4}>Questions</Typography.Title>

      <Card title="Add Question" style={{ marginBottom: 24 }}>
        <Form form={form} layout="vertical" onFinish={handleAddQuestion} initialValues={{ points: 1, questionType: 'MCQ' }}>
          <Form.Item name="questionText" label="Question" rules={[{ required: true }]}>
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="questionType" label="Type" rules={[{ required: true }]}>
            <Select
              options={[
                { value: 'MCQ', label: 'Multiple Choice' },
                { value: 'SHORT_ANSWER', label: 'Short Answer' },
                { value: 'ESSAY', label: 'Essay' },
              ]}
            />
          </Form.Item>
          <Form.Item name="points" label="Points">
            <InputNumber min={1} />
          </Form.Item>

          <Form.Item noStyle shouldUpdate={(prev, cur) => prev.questionType !== cur.questionType}>
            {({ getFieldValue }) =>
              getFieldValue('questionType') === 'MCQ' && (
                <div style={{ marginBottom: 16 }}>
                  <Typography.Text strong>Options:</Typography.Text>
                  {options.map((opt, idx) => (
                    <Space key={idx} style={{ display: 'flex', marginTop: 8 }}>
                      <Input
                        placeholder={`Option ${idx + 1}`}
                        value={opt.optionText}
                        onChange={(e) => {
                          const copy = [...options];
                          copy[idx].optionText = e.target.value;
                          setOptions(copy);
                        }}
                      />
                      <Button
                        type={opt.isCorrect ? 'primary' : 'default'}
                        size="small"
                        onClick={() => {
                          const copy = [...options];
                          copy[idx].isCorrect = !copy[idx].isCorrect;
                          setOptions(copy);
                        }}
                      >
                        {opt.isCorrect ? 'Correct' : 'Mark Correct'}
                      </Button>
                      {options.length > 2 && (
                        <Button
                          icon={<DeleteOutlined />}
                          size="small"
                          danger
                          onClick={() => setOptions(options.filter((_, i) => i !== idx))}
                        />
                      )}
                    </Space>
                  ))}
                  <Button
                    type="dashed"
                    icon={<PlusOutlined />}
                    style={{ marginTop: 8 }}
                    onClick={() => setOptions([...options, { optionText: '', isCorrect: false }])}
                  >
                    Add Option
                  </Button>
                </div>
              )
            }
          </Form.Item>

          <Button type="primary" htmlType="submit" loading={loading}>
            Add Question
          </Button>
        </Form>
      </Card>

      <List
        dataSource={questions}
        renderItem={(q, idx) => (
          <Card size="small" style={{ marginBottom: 8 }}>
            <Typography.Text strong>Q{idx + 1}.</Typography.Text> {q.questionText}
            <Typography.Text type="secondary"> ({q.questionType}, {q.points} pts)</Typography.Text>
            {q.options && q.options.length > 0 && (
              <ul style={{ marginTop: 8 }}>
                {q.options.map((opt) => (
                  <li key={opt.id}>{opt.optionText}</li>
                ))}
              </ul>
            )}
          </Card>
        )}
      />
    </div>
  );
}
