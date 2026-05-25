import { useState } from 'react';
import { Form, Input, InputNumber, DatePicker, Switch, Button, Card, Typography, message } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import type { ExamRequest } from '../../api/examApi';

export function ExamForm() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = !!id;

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const data: ExamRequest = {
        title: values.title,
        description: values.description,
        durationMinutes: values.durationMinutes,
        startTime: values.startTime.toISOString(),
        endTime: values.endTime.toISOString(),
        maxTabViolations: values.maxTabViolations,
        screenShareRequired: values.screenShareRequired,
        gracePeriodSeconds: values.gracePeriodSeconds,
        isPublished: values.isPublished,
      };

      if (isEdit) {
        await examApi.updateExam(Number(id), data);
        message.success('Exam updated');
      } else {
        const res = await examApi.createExam(data);
        message.success('Exam created');
        navigate(`/exams/${res.data.data.id}/edit`);
      }
    } catch {
      message.error('Failed to save exam');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Typography.Title level={4}>{isEdit ? 'Edit Exam' : 'Create Exam'}</Typography.Title>
      <Card>
        <Form layout="vertical" onFinish={handleSubmit} initialValues={{
          maxTabViolations: 3,
          screenShareRequired: true,
          gracePeriodSeconds: 30,
          isPublished: false,
        }}>
          <Form.Item name="title" label="Title" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="durationMinutes" label="Duration (minutes)" rules={[{ required: true }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="startTime" label="Start Time" rules={[{ required: true }]}>
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="endTime" label="End Time" rules={[{ required: true }]}>
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="maxTabViolations" label="Max Tab Violations">
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="gracePeriodSeconds" label="Grace Period (seconds)">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="screenShareRequired" label="Screen Share Required" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="isPublished" label="Published" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              {isEdit ? 'Update' : 'Create'}
            </Button>
            {isEdit && (
              <Button style={{ marginLeft: 8 }} onClick={() => navigate(`/exams/${id}/questions`)}>
                Manage Questions
              </Button>
            )}
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
