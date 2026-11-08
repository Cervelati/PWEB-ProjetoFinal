import { FormEvent, useState } from 'react';
import { api, msgErro } from '../../api';
import StatusBadge from '../../components/StatusBadge';
import { brl, dataHora, linkPedido, telefoneBr, whatsapp } from '../../format';
import { useLoad } from '../../hooks';
import { Cliente, Pedido } from '../../types';

function ClienteCard({ c, onChange, onErro }: { c: Cliente; onChange: () => void; onErro: (m: string) => void }) {
  const [aberto, setAberto] = useState(false);
  const [editando, setEditando] = useState(false);
  const [nome, setNome] = useState(c.nome);
  const [telefone, setTelefone] = useState(c.telefone);
  const [email, setEmail] = useState(c.email ?? '');
  const pedidos = useLoad(() => (aberto ? api<Pedido[]>(`/api/admin/clientes/${c.id}/pedidos`) : Promise.resolve([] as Pedido[])), [aberto, c.id]);

  async function salvar(e: FormEvent) {
    e.preventDefault();
    try {
      await api(`/api/admin/clientes/${c.id}`, { method: 'PUT', body: { nome, telefone, email: email || null } });
      setEditando(false);
      onChange();
    } catch (err) {
      onErro(msgErro(err));
    }
  }

  return (
    <div className="painel">
      {editando ? (
        <form onSubmit={salvar}>
          <div className="linha">
            <input required value={nome} onChange={(e) => setNome(e.target.value)} aria-label="Nome" />
            <input required value={telefone} onChange={(e) => setTelefone(e.target.value)} aria-label="Telefone" />
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="E-mail" aria-label="E-mail" />
          </div>
          <div className="linha">
            <button className="btn pequeno">Salvar</button>
            <button type="button" className="link" onClick={() => setEditando(false)}>Cancelar</button>
          </div>
        </form>
      ) : (
        <div className="linha entre">
          <div>
            <h3>{c.nome}</h3>
            <p className="muted">
              📞 {telefoneBr(c.telefone)}{c.email && <> · ✉ {c.email}</>} ·{' '}
              <a href={whatsapp(c.telefone)} target="_blank" rel="noreferrer">WhatsApp</a>
            </p>
          </div>
          <div className="linha">
            <button className="link" onClick={() => setEditando(true)}>Editar</button>
            <button className="btn pequeno secundario" onClick={() => setAberto(!aberto)}>{aberto ? 'Ocultar pedidos' : 'Ver pedidos'}</button>
          </div>
        </div>
      )}

      {aberto && (
        <>
          {pedidos.loading && <p className="muted">Carregando…</p>}
          {pedidos.data && pedidos.data.length === 0 && <p className="muted">Nenhum pedido.</p>}
          {pedidos.data && pedidos.data.length > 0 && (
            <table className="tabela">
              <thead><tr><th>#</th><th>Data</th><th>Status</th><th>Total</th><th>Link</th></tr></thead>
              <tbody>
                {pedidos.data.map((p) => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{dataHora(p.data)}</td>
                    <td><StatusBadge status={p.status} /></td>
                    <td>{brl(p.valorTotal)}</td>
                    <td><a href={linkPedido(p.codigoAcompanhamento)} target="_blank" rel="noreferrer">Abrir</a></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      )}
    </div>
  );
}

export default function AdminClientes() {
  const [busca, setBusca] = useState('');
  const [termo, setTermo] = useState('');
  const [msg, setMsg] = useState<string | null>(null);
  const { data, error, loading, reload } = useLoad(
    () => api<Cliente[]>('/api/admin/clientes' + (termo ? `?busca=${encodeURIComponent(termo)}` : '')),
    [termo],
  );

  return (
    <>
      <h2>Clientes</h2>
      <p className="muted">Os clientes são criados automaticamente quando fazem um pedido no site (identificados pelo telefone).</p>
      <form className="linha" onSubmit={(e) => { e.preventDefault(); setTermo(busca.trim()); }}>
        <input className="cresce" placeholder="Buscar por nome, telefone ou e-mail" value={busca} onChange={(e) => setBusca(e.target.value)} />
        <button className="btn">Buscar</button>
      </form>
      {msg && <p className="erro">{msg}</p>}
      {loading && !data && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}
      {data && data.length === 0 && <p>Nenhum cliente encontrado.</p>}
      {data?.map((c) => <ClienteCard key={c.id} c={c} onChange={reload} onErro={setMsg} />)}
    </>
  );
}
