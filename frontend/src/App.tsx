import { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { useAuthStore } from './store/authStore';
import { AppLayout } from './components/AppLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './features/auth/LoginPage';
import { DashboardPage } from './features/dashboard/DashboardPage';
import { ExamForm } from './features/exam-management/ExamForm';
import { QuestionEditor } from './features/exam-management/QuestionEditor';
import { ExamSessionPage } from './features/exam-session/ExamSessionPage';
import { ResultsPage } from './features/results/ResultsPage';

function App() {
  const loadFromStorage = useAuthStore((s) => s.loadFromStorage);

  useEffect(() => {
    loadFromStorage();
  }, [loadFromStorage]);

  return (
    <ConfigProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/exams/new" element={<ExamForm />} />
            <Route path="/exams/:id/edit" element={<ExamForm />} />
            <Route path="/exams/:id/questions" element={<QuestionEditor />} />
            <Route path="/exams/:id/submissions" element={<ResultsPage />} />
            <Route path="/available-exams" element={<DashboardPage />} />
          </Route>
          <Route
            path="/exam/:examId/session"
            element={
              <ProtectedRoute>
                <ExamSessionPage />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;

