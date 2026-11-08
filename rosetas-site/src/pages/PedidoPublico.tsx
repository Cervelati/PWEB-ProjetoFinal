import { useEffect, useState } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { useParams } from 'react-router-dom';
import { api, msgErro } from '../api';
import ItensPedido from '../components/ItensPedido';
import StatusBadge from '../components/StatusBadge';
import { brl, dataHora } from '../format';
import { useLoad } from '../hooks';
import { PedidoPublico as Pedido } from '../types';

const STUB = 'PIX-STUB-';
const ACOMPANHAR = ['AGUARDANDO_PAGAMENTO', 'AGUARDANDO_APROVACAO', 'EM_PRODUCAO'];
const DEV_TOOLS = import.meta.env.DEV || import.meta.env.VITE_DEV_TOOLS === 'true';

export default function PedidoPublico() {
  const { codigo } = useParams();
  const { data: p, error, loading, reload } = useLoad(() => api<Pedido>(`/api/publico/pedidos/${codigo}`), [codigo]);
  const [msg, setMsg] = useState<string | null>(null);
  const [copiado, setCopiado] = useState<string | null>(null);

  // Atualiza sozinho enquanto o pedido está andando (ex.: confirmação do pagamento)
  const status = p?.status;
  useEffect(() => {
    if (!status || !ACOMPANHAR.includes(status)) return;
    const t = setInterval(reload, 10000);
    return () => clearInterval(t);
  }, [status, reload]);

  if (loading && !p) return <p className="muted">Carregando…</p>;
  if (error && !p) return <p className="erro">Pedido não encontrado. Confira o link ou o código.</p>;
  if (!p) return null;

  const pix = p.pagamento?.pixCopiaCola ?? null;
  const aguardando = p.status === 'AGUARDANDO_PAGAMENTO';

  async function copiar(texto: string, rotulo: string) {
    try {
      await navigator.clipboard.writeText(texto);
      setCopiado(rotulo);
      setTimeout(() => setCopiado(null), 2000);
    } catch {
      setMsg('Não foi possível copiar automaticamente. Selecione o texto e copie.');
    }
  }

  async function cancelar() {
    if (!confirm('Cancelar este pedido?')) return;
    try {
      await api(`/api/publico/pedidos/${codigo}/cancelar`, { method: 'POST' });
      reload();
    } catch (e) {
      setMsg(msgErro(e));
    }
  }

  /** Só em desenvolvimento, com o gateway stub: simula o webhook de pagamento aprovado. */
  async function simularPagamento() {
    try {
      await api('/api/pagamentos/webhook', {
        method: 'POST',
        headers: { 'X-Webhook-Secret': import.meta.env.VITE_DEV_WEBHOOK_SECRET ?? 'troque-este-segredo' },
        body: { gatewayTransacaoId: pix!.substring(STUB.length), status: 'APROVADO' },
      });
      reload();
    } catch (e) {
      setMsg(msgErro(e));
    }
  }

  return (
    <>
      <h2>
        Pedido de {p.clienteNome} <StatusBadge status={p.status} />
      </h2>
      <p className="muted">Feito em {dataHora(p.data)}</p>

      <div className="info">
        Guarde este link: é por ele que você acompanha o pedido, sem precisar de conta.{' '}
        <button className="link" onClick={() => copiar(window.location.href, 'link')}>
          {copiado === 'link' ? 'Copiado!' : 'Copiar link'}
        </button>
      </div>

      {aguardando && pix && (
        <div className="painel">
          <h3>Pague via Pix — {brl(p.valorTotal)}</h3>
          <p className="muted">
            Escaneie o QR code ou copie o código abaixo e pague no app do seu banco (Pix copia e cola). Pague exatamente {brl(p.valorTotal)}.
            A confirmação é feita pela nossa equipe após conferir o recebimento, então pode levar um tempo; esta página atualiza sozinha.
          </p>
          <div className="qr"><QRCodeSVG value={pix} size={200} includeMargin /></div>
          <textarea readOnly value={pix} rows={3} onFocus={(e) => e.target.select()} />
          <div className="linha">
            <button className="btn" onClick={() => copiar(pix, 'pix')}>
              {copiado === 'pix' ? 'Copiado!' : 'Copiar código Pix'}
            </button>
            {DEV_TOOLS && pix.startsWith(STUB) && (
              <button className="btn secundario" onClick={simularPagamento}>Simular pagamento (dev)</button>
            )}
            <button className="link" onClick={cancelar}>Cancelar pedido</button>
          </div>
        </div>
      )}

      {p.status === 'AGUARDANDO_APROVACAO' && <p className="info">Pagamento confirmado. Estamos conferindo a personalização do seu pedido.</p>}
      {p.status === 'EM_PRODUCAO' && <p className="info">Seu pedido está em produção. Entraremos em contato para combinar a entrega.</p>}
      {p.status === 'ENTREGUE' && <p className="info">Pedido entregue. Obrigado!</p>}
      {p.status === 'CANCELADO' && <p className="erro">Pedido cancelado.{p.observacoes ? ` ${p.observacoes}` : ''}</p>}
      {msg && <p className="erro">{msg}</p>}

      <ItensPedido itens={p.itens} />
      {p.observacoes && p.status !== 'CANCELADO' && <p className="muted">Observações: {p.observacoes}</p>}
      <div className="total"><span>Total</span><strong>{brl(p.valorTotal)}</strong></div>
    </>
  );
}
