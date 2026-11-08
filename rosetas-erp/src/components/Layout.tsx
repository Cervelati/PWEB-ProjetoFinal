import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { isGestao, useAuth } from '../auth';
import { BRAND } from '../format';

export default function Layout() {
  const { usuario, logout } = useAuth();
  const nav = useNavigate();
  const gestao = isGestao(usuario);

  return (
    <>
      <header className="topo">
        <Link to="/" className="marca">✿ {BRAND} <small>gestão</small></Link>
        <nav>
          {gestao && (
            <>
              <NavLink to="/admin/pedidos">Pedidos</NavLink>
              <NavLink to="/admin/clientes">Clientes</NavLink>
              <NavLink to="/admin/estoque">Estoque</NavLink>
              <NavLink to="/admin/modelos">Modelos e preços</NavLink>
              <NavLink to="/admin/configuracoes">Configurações</NavLink>
            </>
          )}
          {usuario && <NavLink to="/producao">Produção</NavLink>}
          {usuario?.perfil === 'ROOT' && <NavLink to="/admin/usuarios">Equipe</NavLink>}
        </nav>
        <div className="usuario">
          {usuario && (
            <>
              <span>{usuario.nome} <small className="muted">({usuario.perfil})</small></span>
              <button className="link" onClick={() => { logout(); nav('/entrar'); }}>Sair</button>
            </>
          )}
        </div>
      </header>
      <main className="conteudo">
        <Outlet />
      </main>
    </>
  );
}
