import { useAuthStore } from '../../store/authStore';
import { TeacherDashboard } from './TeacherDashboard';
import { StudentDashboard } from './StudentDashboard';

export function DashboardPage() {
  const user = useAuthStore((s) => s.user);

  if (user?.role === 'TEACHER' || user?.role === 'ADMIN') {
    return <TeacherDashboard />;
  }
  return <StudentDashboard />;
}
