import { Link } from 'react-router-dom';
import { api } from '../api';
import CartaoModelo from '../components/CartaoModelo';
import RosettePreview from '../components/RosettePreview';
import { BRAND, CONTATO } from '../format';
import { useLoad } from '../hooks';
import { Modelo } from '../types';

export default function Landing() {
  const { data: modelos, error, loading } = useLoad(() => api<Modelo[]>('/api/catalogo/modelos'), []);
  // Na landing mostramos uma seleção; o catálogo completo tem busca e filtros.
  const destaque = (modelos ?? []).filter((m) => m.disponivel && m.categoria !== 'Diversos').slice(0, 8);

  return (
    <>
      <section className="hero">
        <div>
          <h1>Rosetas personalizadas para competições e exposições</h1>
          <p>
            A {BRAND} confecciona rosetas (escarapelas) sob medida. Escolha o modelo, monte as cores volta por volta e fita
            por fita e veja a prévia antes de fechar o pedido.
          </p>
          <a href="#catalogo" className="btn grande">
            Ver catálogo
          </a>
        </div>
        <RosettePreview corpo={['#C8102E', '#FFFFFF', '#1F3FBF']} rabiola={['#C8102E', '#FFFFFF', '#1F3FBF']} size={260} />
      </section>

      <section className="grade tres">
        <div className="cartao">
          <h3>1. Personalize</h3>
          <p>Escolha o modelo, o franzido e a cor de cada volta e de cada fita da rabiola. Só aparecem as cores em estoque.</p>
        </div>
        <div className="cartao">
          <h3>2. Pague via Pix</h3>
          <p>O pagamento é integral e feito ao finalizar o pedido, por QR code ou Pix copia e cola. Não precisa criar conta.</p>
        </div>
        <div className="cartao">
          <h3>3. Acompanhe pelo link</h3>
          <p>Você recebe um link para ver o andamento. Conferimos o pagamento, produzimos e combinamos a entrega.</p>
        </div>
      </section>

      <section id="catalogo" className="secao">
        <div className="linha entre">
          <h2>Catálogo 2026</h2>
          <Link to="/catalogo">Ver o catálogo completo ({modelos?.length ?? '…'} modelos)</Link>
        </div>
        {loading && <p className="muted">Carregando…</p>}
        {error && <p className="erro">Não foi possível carregar o catálogo: {error}</p>}
        {modelos && modelos.length === 0 && <p>Nenhum modelo disponível no momento.</p>}
        <div className="grade">
          {destaque.map((m) => (
            <CartaoModelo key={m.id} m={m} />
          ))}
        </div>
      </section>

      <section className="secao">
        <h2>Fale com a gente</h2>
        <p>
          Dúvidas ou pedidos especiais (logotipo no bottom, fitas estampadas ou metálicas)? Escreva para{' '}
          <a href={`mailto:${CONTATO.email}`}>{CONTATO.email}</a> ou chame no WhatsApp {CONTATO.telefone}.
        </p>
      </section>
    </>
  );
}
