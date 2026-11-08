import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth';
import { Perfil } from '../types';

export default function RequireRole({ roles, children }: { roles?: Perfil[]; children: ReactNode }) {
  const { usuario, loading } = useAuth();
  if (loading) return <p className="muted">Carregando…</p>;
  if (!usuario) return <Navigate to="/entrar" replace />;
  if (roles && !roles.includes(usuario.perfil)) {
    return <p className="erro">Você não tem permissão para acessar esta página.</p>;
  }
  return <>{children}</>;
}
