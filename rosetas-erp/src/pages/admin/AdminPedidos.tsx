import { useState } from 'react';
import { api, msgErro } from '../../api';
import ItensPedido from '../../components/ItensPedido';
import StatusBadge from '../../components/StatusBadge';
import { STATUS_LABEL, brl, dataHora, linkPedido, telefoneBr, whatsapp } from '../../format';
import { useLoad } from '../../hooks';
import { Pedido, StatusPedido } from '../../types';

export default function AdminPedidos() {
  const [filtro, setFiltro] = useState<string>('AGUARDANDO_APROVACAO');
  const [msg, setMsg] = useState<string | null>(null);
  const [copiado, setCopiado] = useState<number | null>(null);
  const { data, error, loading, reload } = useLoad(
    () => api<Pedido[]>('/api/admin/pedidos' + (filtro ? `?status=${filtro}` : '')),
    [filtro],
  );

  async function acao(id: number, a: 'aprovar' | 'entregar' | 'confirmar-pagamento') {
    setMsg(null);
    try {
      await api(`/api/admin/pedidos/${id}/${a}`, { method: 'POST' });
      reload();
    } catch (e) {
      setMsg(msgErro(e));
    }
  }

  async function recusar(id: number) {
    const motivo = prompt('Motivo da recusa (o estoque reservado será devolvido; o reembolso é manual):');
    if (!motivo?.trim()) return;
    setMsg(null);
    try {
      await api(`/api/admin/pedidos/${id}/recusar`, { method: 'POST', body: { motivo: motivo.trim() } });
      reload();
    } catch (e) {
      setMsg(msgErro(e));
    }
  }

  async function copiarLink(p: Pedido) {
    try {
      await navigator.clipboard.writeText(linkPedido(p.codigoAcompanhamento));
      setCopiado(p.id);
      setTimeout(() => setCopiado(null), 2000);
    } catch {
      setMsg('Não foi possível copiar o link automaticamente.');
    }
  }

  return (
    <>
      <h2>Pedidos</h2>
      <div className="campo linha">
        <label htmlFor="filtro">Status</label>
        <select id="filtro" value={filtro} onChange={(e) => setFiltro(e.target.value)}>
          <option value="">Todos</option>
          {(Object.keys(STATUS_LABEL) as StatusPedido[]).map((s) => (
            <option key={s} value={s}>{STATUS_LABEL[s]}</option>
          ))}
        </select>
        <button className="link" onClick={reload}>Atualizar</button>
      </div>

      {msg && <p className="erro">{msg}</p>}
      {loading && !data && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}
      {data && data.length === 0 && <p>Nenhum pedido neste status.</p>}

      {data?.map((p) => (
        <div key={p.id} className="painel">
          <div className="linha entre">
            <h3>#{p.id} — {p.cliente.nome} <StatusBadge status={p.status} /></h3>
            <div className="muted">{dataHora(p.data)} · <strong>{brl(p.valorTotal)}</strong></div>
          </div>
          <p className="muted">
            📞 {telefoneBr(p.cliente.telefone)}
            {p.cliente.email && <> · ✉ {p.cliente.email}</>}
            {' · '}
            <a href={whatsapp(p.cliente.telefone, `Olá ${p.cliente.nome}! Sobre o seu pedido de rosetas: ${linkPedido(p.codigoAcompanhamento)}`)} target="_blank" rel="noreferrer">
              Chamar no WhatsApp
            </a>
            {' · '}
            <button className="link" onClick={() => copiarLink(p)}>{copiado === p.id ? 'Link copiado!' : 'Copiar link de acompanhamento'}</button>
          </p>
          <ItensPedido itens={p.itens} />
          {p.observacoes && <p className="muted">Observações: {p.observacoes}</p>}
          <div className="linha">
            {p.status === 'AGUARDANDO_PAGAMENTO' && (
              <button
                className="btn"
                onClick={() => {
                  if (confirm(`Confirmar que recebeu ${brl(p.valorTotal)} de ${p.cliente.nome} no banco?`)) acao(p.id, 'confirmar-pagamento');
                }}
              >
                Confirmar pagamento ({brl(p.valorTotal)})
              </button>
            )}
            {p.status === 'AGUARDANDO_APROVACAO' && (
              <>
                <button className="btn" onClick={() => acao(p.id, 'aprovar')}>Aprovar personalização</button>
                <button className="btn perigo" onClick={() => recusar(p.id)}>Recusar</button>
              </>
            )}
            {p.status === 'EM_PRODUCAO' && (
              <button className="btn" onClick={() => acao(p.id, 'entregar')}>Marcar como entregue</button>
            )}
          </div>
        </div>
      ))}
    </>
  );
}
