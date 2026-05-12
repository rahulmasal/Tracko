import { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { getToken, decodeToken, hasAnyRole } from '../utils/auth';

interface ProtectedRouteProps {
  children: ReactNode;
  allowedRoles?: string[];
}

export default function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const location = useLocation();
  const token = getToken();

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  const payload = decodeToken(token);
  if (!payload) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && allowedRoles.length > 0) {
    if (!hasAnyRole(payload.roles, allowedRoles)) {
      return (
        <div style={{ padding: '2rem', textAlign: 'center' }}>
          <h2>Unauthorized Access</h2>
          <p>You do not have permission to access this page.</p>
          <p>Required roles: {allowedRoles.join(', ')}</p>
          <p>Your roles: {payload.roles.join(', ')}</p>
        </div>
      );
    }
  }

  return <>{children}</>;
}
