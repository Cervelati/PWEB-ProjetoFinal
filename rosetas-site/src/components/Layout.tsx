import { Link, NavLink, Outlet } from 'react-router-dom';
import { useCart } from '../cart';
import { BRAND, CONTATO, EMPRESA } from '../format';

export default function Layout() {
  const { itens } = useCart();

  return (
    <>
      <header className="topo">
        <Link to="/" className="marca">
          ✿ {BRAND}
        </Link>
        <nav>
          <NavLink to="/catalogo">Catálogo</NavLink>
          <NavLink to="/acompanhar">Acompanhar pedido</NavLink>
        </nav>
        <div className="usuario">
          <Link to="/carrinho" className="btn pequeno">
            Carrinho{itens.length > 0 && <span className="contador">{itens.length}</span>}
          </Link>
        </div>
      </header>
      <main className="conteudo">
        <Outlet />
      </main>
      <footer className="rodape">
        <div>As cores exibidas são uma aproximação e podem variar do material real. Pagamento integral via Pix após o pedido. Frete (Sedex) não incluído.</div>
        <div>
          {EMPRESA} · <a href={`mailto:${CONTATO.email}`}>{CONTATO.email}</a> · {CONTATO.telefone}
        </div>
      </footer>
    </>
  );
}
