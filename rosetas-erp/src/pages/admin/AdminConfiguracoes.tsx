import { FormEvent, useEffect, useState } from 'react';
import { api, msgErro } from '../../api';
import { useLoad } from '../../hooks';

interface PixConfig {
  chave: string;
  nome: string;
  cidade: string;
  configurado: boolean;
}

export default function AdminConfiguracoes() {
  const { data, error, loading, reload } = useLoad(() => api<PixConfig>('/api/admin/configuracoes/pix'), []);
  const [form, setForm] = useState({ chave: '', nome: '', cidade: '' });
  const [msg, setMsg] = useState<string | null>(null);
  const [ok, setOk] = useState(false);

  useEffect(() => {
    if (data) setForm({ chave: data.chave, nome: data.nome, cidade: data.cidade });
  }, [data]);

  async function salvar(e: FormEvent) {
    e.preventDefault();
    setMsg(null);
    setOk(false);
    try {
      await api('/api/admin/configuracoes/pix', { method: 'PUT', body: form });
      setOk(true);
      reload();
    } catch (err) {
      setMsg(msgErro(err));
    }
  }

  const grav = useLoad(() => api<{ precoGravacaoExtra: number }>('/api/admin/configuracoes/gravacao'), []);
  const [precoGrav, setPrecoGrav] = useState('');
  const [okGrav, setOkGrav] = useState(false);
  useEffect(() => {
    if (grav.data) setPrecoGrav(String(grav.data.precoGravacaoExtra));
  }, [grav.data]);

  async function salvarGrav(e: FormEvent) {
    e.preventDefault();
    setMsg(null);
    setOkGrav(false);
    try {
      await api('/api/admin/configuracoes/gravacao', { method: 'PUT', body: { preco: Number(precoGrav.replace(',', '.')) } });
      setOkGrav(true);
      grav.reload();
    } catch (err) {
      setMsg(msgErro(err));
    }
  }

  return (
    <>
      <h2>Configurações</h2>
      <form className="painel" onSubmit={salvarGrav}>
        <h3>Gravação extra</h3>
        <p className="muted">Valor cobrado por unidade em cada gravação adicional além das incluídas no modelo.</p>
        <div className="linha">
          <input required className="curto" inputMode="decimal" value={precoGrav} onChange={(e) => setPrecoGrav(e.target.value)} aria-label="Preço da gravação extra" />
          <button className="btn">Salvar</button>
        </div>
        {okGrav && <p className="info">Valor salvo.</p>}
      </form>
      <form className="painel" onSubmit={salvar}>
        <h3>Chave Pix para recebimento</h3>
        <p className="muted">
          É a chave que aparece no QR code e no “copia e cola” dos pedidos. O valor do pedido já vai dentro do código. Vale só para pedidos
          novos; confira o recebimento no app do banco e use “Confirmar pagamento” em Pedidos.
        </p>
        {loading && !data && <p className="muted">Carregando…</p>}
        {error && <p className="erro">{error}</p>}
        {data && !data.configurado && (
          <p className="erro">Chave não configurada: os pedidos estão gerando um código de teste que não funciona no banco.</p>
        )}
        <div className="campo">
          <label htmlFor="chave">Chave Pix *</label>
          <input id="chave" required maxLength={77} value={form.chave} onChange={(e) => setForm({ ...form, chave: e.target.value })} />
          <small className="muted">CPF/CNPJ só com números · telefone com +55 e DDD (ex.: +5518999999999) · e-mail · ou chave aleatória.</small>
        </div>
        <div className="campo">
          <label htmlFor="nome">Nome do recebedor * (até 25 caracteres)</label>
          <input id="nome" required maxLength={25} value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} />
        </div>
        <div className="campo">
          <label htmlFor="cidade">Cidade * (até 15 caracteres)</label>
          <input id="cidade" required maxLength={15} value={form.cidade} onChange={(e) => setForm({ ...form, cidade: e.target.value })} />
        </div>
        {msg && <p className="erro">{msg}</p>}
        {ok && <p className="info">Configuração salva.</p>}
        <button className="btn">Salvar</button>
      </form>
    </>
  );
}
