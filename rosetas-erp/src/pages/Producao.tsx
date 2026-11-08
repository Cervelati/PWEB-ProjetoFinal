import { api } from '../api';
import ItensPedido from '../components/ItensPedido';
import { dataHora } from '../format';
import { useLoad } from '../hooks';
import { Ficha } from '../types';

/** Fichas de produção: não exibem nenhum valor financeiro. */
export default function Producao() {
  const { data, error, loading, reload } = useLoad(() => api<Ficha[]>('/api/producao/pedidos'), []);

  return (
    <>
      <div className="linha entre">
        <h2>Fichas de produção</h2>
        <div className="linha">
          <button className="link" onClick={reload}>Atualizar</button>
          <button className="link" onClick={() => window.print()}>Imprimir</button>
        </div>
      </div>
      {loading && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}
      {data && data.length === 0 && <p>Nenhum pedido em produção.</p>}
      {data?.map((f) => (
        <div key={f.pedidoId} className="painel">
          <h3>Pedido #{f.pedidoId} <small className="muted">{dataHora(f.data)}</small></h3>
          <ItensPedido itens={f.itens} />
          {f.observacoes && <p className="muted">Observações: {f.observacoes}</p>}
        </div>
      ))}
    </>
  );
}
