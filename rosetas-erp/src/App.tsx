import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import RequireRole from './components/RequireRole';
import { isGestao, useAuth } from './auth';
import Login from './pages/Login';
import Producao from './pages/Producao';
import AdminConfiguracoes from './pages/admin/AdminConfiguracoes';
import AdminClientes from './pages/admin/AdminClientes';
import AdminEstoque from './pages/admin/AdminEstoque';
import AdminModelos from './pages/admin/AdminModelos';
import AdminPedidos from './pages/admin/AdminPedidos';
import AdminUsuarios from './pages/admin/AdminUsuarios';

function Inicio() {
  const { usuario, loading } = useAuth();
  if (loading) return <p className="muted">Carregando…</p>;
  if (!usuario) return <Navigate to="/entrar" replace />;
  return <Navigate to={isGestao(usuario) ? '/admin/pedidos' : '/producao'} replace />;
}

export default function App() {
  const gestao = ['ADMIN', 'ROOT'] as const;
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Inicio />} />
        <Route path="entrar" element={<Login />} />
        <Route path="producao" element={<RequireRole roles={['FUNCIONARIA', 'ADMIN', 'ROOT']}><Producao /></RequireRole>} />
        <Route path="admin/pedidos" element={<RequireRole roles={[...gestao]}><AdminPedidos /></RequireRole>} />
        <Route path="admin/clientes" element={<RequireRole roles={[...gestao]}><AdminClientes /></RequireRole>} />
        <Route path="admin/estoque" element={<RequireRole roles={[...gestao]}><AdminEstoque /></RequireRole>} />
        <Route path="admin/modelos" element={<RequireRole roles={[...gestao]}><AdminModelos /></RequireRole>} />
        <Route path="admin/configuracoes" element={<RequireRole roles={[...gestao]}><AdminConfiguracoes /></RequireRole>} />
        <Route path="admin/usuarios" element={<RequireRole roles={['ROOT']}><AdminUsuarios /></RequireRole>} />
        <Route path="*" element={<p>Página não encontrada.</p>} />
      </Route>
    </Routes>
  );
}
