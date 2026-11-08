import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, msgErro } from '../api';
import RosettePreview, { separarCores } from '../components/RosettePreview';
import { useCart } from '../cart';
import { FRANZIDO_LABEL, brl } from '../format';
import { PedidoPublico } from '../types';

const KEY_CONTATO = 'rosetas.contato';

function contatoSalvo(): { nome: string; telefone: string; email: string } {
  try {
    return { nome: '', telefone: '', email: '', ...JSON.parse(localStorage.getItem(KEY_CONTATO) ?? '{}') };
  } catch {
    return { nome: '', telefone: '', email: '' };
  }
}

export default function Carrinho() {
  const { itens, remove, clear, total } = useCart();
  const nav = useNavigate();
  const [contato, setContato] = useState(contatoSalvo);
  const [obs, setObs] = useState('');
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  if (itens.length === 0) {
    return (
      <>
        <h2>Carrinho</h2>
        <p>Seu carrinho está vazio. <Link to="/catalogo">Ver catálogo</Link></p>
      </>
    );
  }

  async function finalizar(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      const p = await api<PedidoPublico>('/api/publico/pedidos', {
        method: 'POST',
        body: {
          cliente: { nome: contato.nome, telefone: contato.telefone, email: contato.email || null },
          observacoes: obs || null,
          itens: itens.map((i) => ({
            modeloId: i.modelo.id,
            quantidade: i.quantidade,
            textoPersonalizado: i.texto || null,
            franzido: i.franzido,
            gravacoesExtras: i.gravacoesExtras,
            partes: i.modelo.partes.map((pt) => ({ parteId: pt.id, fitaId: i.escolhas[pt.id].id })),
          })),
        },
      });
      try {
        localStorage.setItem(KEY_CONTATO, JSON.stringify(contato));
      } catch {
        /* ignora */
      }
      clear();
      nav(`/pedido/${p.codigo}`);
    } catch (err) {
      setErro(msgErro(err));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <>
      <h2>Carrinho</h2>
      <div className="itens">
        {itens.map((i) => {
          const prev = separarCores(i.modelo.partes, (p) => i.escolhas[p.id]?.hex ?? undefined);
          return (
          <div key={i.key} className="item">
            <RosettePreview corpo={prev.corpo} rabiola={prev.rabiola} estrela={i.modelo.formato === 'estrela'} size={90} />
            <div className="cresce">
              <strong>
                {i.quantidade}× {i.modelo.nome}
              </strong>
              <div className="muted">
                {i.franzido && <>Franzido {FRANZIDO_LABEL[i.franzido].toLowerCase()}</>}
                {i.gravacoesExtras > 0 && <> · {i.gravacoesExtras} gravação(ões) extra</>}
              </div>
              {i.texto && <div className="muted">Texto: “{i.texto}”</div>}
              <ul className="partes">
                {i.modelo.partes.map((p) => (
                  <li key={p.id}>
                    <span className="swatch" style={{ background: i.escolhas[p.id]?.hex }} /> {p.nome}: {i.escolhas[p.id]?.nome}
                  </li>
                ))}
              </ul>
            </div>
            <div className="direita">
              <div className="preco">{brl(i.precoUnitario * i.quantidade)}</div>
              <button className="link" onClick={() => remove(i.key)}>
                Remover
              </button>
            </div>
          </div>
          );
        })}
      </div>

      <div className="total">
        <span>Total</span>
        <strong>{brl(total)}</strong>
      </div>

      <form className="painel" onSubmit={finalizar}>
        <h3>Seus dados</h3>
        <p className="muted">Não é preciso criar conta. Usamos estes dados só para falar com você sobre o pedido.</p>
        <div className="campo">
          <label htmlFor="nome">Nome *</label>
          <input id="nome" required maxLength={150} value={contato.nome} onChange={(e) => setContato({ ...contato, nome: e.target.value })} />
        </div>
        <div className="campo">
          <label htmlFor="tel">WhatsApp / telefone com DDD *</label>
          <input id="tel" required inputMode="tel" placeholder="(18) 99999-9999" maxLength={30} value={contato.telefone} onChange={(e) => setContato({ ...contato, telefone: e.target.value })} />
        </div>
        <div className="campo">
          <label htmlFor="email">E-mail (opcional)</label>
          <input id="email" type="email" maxLength={255} value={contato.email} onChange={(e) => setContato({ ...contato, email: e.target.value })} />
        </div>
        <div className="campo">
          <label htmlFor="obs">Observações (opcional)</label>
          <textarea id="obs" maxLength={1000} value={obs} onChange={(e) => setObs(e.target.value)} />
        </div>

        {erro && <p className="erro">{erro}</p>}

        <button className="btn grande" disabled={enviando}>
          {enviando ? 'Enviando…' : `Finalizar pedido e pagar via Pix — ${brl(total)}`}
        </button>
      </form>
    </>
  );
}
