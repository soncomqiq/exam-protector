import { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Tabs } from 'antd';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { useAuthStore } from '../../store/authStore';

export function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  const handleLogin = async (values: { email: string; password: string }) => {
    setLoading(true);
    try {
      const res = await authApi.login(values.email, values.password);
      const { accessToken, fullName, email, role } = res.data.data;
      login(accessToken, { email, fullName, role: role as 'STUDENT' | 'TEACHER' | 'ADMIN' });
      message.success('Login successful');
      navigate('/dashboard');
    } catch {
      message.error('Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (values: { email: string; password: string; fullName: string }) => {
    setLoading(true);
    try {
      const res = await authApi.register(values.email, values.password, values.fullName);
      const { accessToken, fullName, email, role } = res.data.data;
      login(accessToken, { email, fullName, role: role as 'STUDENT' | 'TEACHER' | 'ADMIN' });
      message.success('Registration successful');
      navigate('/dashboard');
    } catch {
      message.error('Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#f0f2f5' }}>
      <Card style={{ width: 400 }}>
        <Typography.Title level={3} style={{ textAlign: 'center' }}>
          Exam Protector
        </Typography.Title>
        <Tabs
          items={[
            {
              key: 'login',
              label: 'Login',
              children: (
                <Form onFinish={handleLogin} layout="vertical">
                  <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="password" label="Password" rules={[{ required: true, min: 6 }]}>
                    <Input.Password />
                  </Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>
                    Login
                  </Button>
                </Form>
              ),
            },
            {
              key: 'register',
              label: 'Register',
              children: (
                <Form onFinish={handleRegister} layout="vertical">
                  <Form.Item name="fullName" label="Full Name" rules={[{ required: true }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="password" label="Password" rules={[{ required: true, min: 6 }]}>
                    <Input.Password />
                  </Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>
                    Register
                  </Button>
                </Form>
              ),
            },
          ]}
        />
      </Card>
    </div>
  );
}
