import { FormEvent, useMemo, useState } from 'react';
import { api, msgErro } from '../../api';
import { TIPO_LABEL } from '../../format';
import { useLoad } from '../../hooks';
import { Material, TipoMaterial, UnidadeMaterial } from '../../types';

const fmt = (m: Material) =>
  `${m.saldo.toLocaleString('pt-BR', { maximumFractionDigits: 3 })} ${m.unidade === 'METRO' ? 'm' : 'un.'}`;

function Linha({ m, onChange, onErro }: { m: Material; onChange: () => void; onErro: (s: string) => void }) {
  const [qtd, setQtd] = useState('');

  async function entrada() {
    const v = Number(qtd.replace(',', '.'));
    if (!(v > 0)) return;
    try {
      await api(`/api/admin/materiais/${m.id}/entrada`, { method: 'POST', body: { quantidade: v } });
      setQtd('');
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }

  async function alternarAtivo() {
    try {
      await api(`/api/admin/materiais/${m.id}`, {
        method: 'PUT',
        body: { nome: m.nome, tipo: m.tipo, unidade: m.unidade, hex: m.hex, foto: m.foto, ativo: !m.ativo },
      });
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }

  return (
    <tr className={m.ativo ? '' : 'inativo'}>
      <td>{m.hex ? <span className="swatch grande" style={{ background: m.hex }} /> : null}</td>
      <td>
        {m.nome} <small className="muted">{TIPO_LABEL[m.tipo]}</small>
      </td>
      <td>{fmt(m)}</td>
      <td>
        <input type="checkbox" checked={m.ativo} onChange={alternarAtivo} aria-label="Ativo" />
      </td>
      <td>
        <div className="linha">
          <input className="curto" inputMode="decimal" placeholder={m.unidade === 'METRO' ? 'metros' : 'unidades'} value={qtd} onChange={(e) => setQtd(e.target.value)} />
          <button className="btn pequeno" onClick={entrada}>Registrar entrada</button>
        </div>
      </td>
    </tr>
  );
}

export default function AdminEstoque() {
  const { data, error, loading, reload } = useLoad(() => api<Material[]>('/api/admin/materiais'), []);
  const [filtro, setFiltro] = useState<'' | TipoMaterial>('');
  const [nome, setNome] = useState('');
  const [tipo, setTipo] = useState<TipoMaterial>('FITA');
  const [hex, setHex] = useState('#c8102e');
  const [msg, setMsg] = useState<string | null>(null);

  const lista = useMemo(() => (data ?? []).filter((m) => !filtro || m.tipo === filtro), [data, filtro]);

  async function criar(e: FormEvent) {
    e.preventDefault();
    setMsg(null);
    const unidade: UnidadeMaterial = tipo === 'FITA' ? 'METRO' : 'UNIDADE';
    try {
      await api('/api/admin/materiais', {
        method: 'POST',
        body: { nome, tipo, unidade, hex: tipo === 'FITA' ? hex : null, foto: null, ativo: true },
      });
      setNome('');
      reload();
    } catch (err) {
      setMsg(msgErro(err));
    }
  }

  return (
    <>
      <h2>Estoque</h2>
      <p className="muted">
        Fitas em metros; botton, argola, alfinete e outros em unidades. O saldo já desconta as reservas de pedidos em aberto.
      </p>

      <form className="painel linha" onSubmit={criar}>
        <select value={tipo} onChange={(e) => setTipo(e.target.value as TipoMaterial)} aria-label="Tipo">
          {Object.entries(TIPO_LABEL).map(([k, v]) => (
            <option key={k} value={k}>{v}</option>
          ))}
        </select>
        <input required placeholder={tipo === 'FITA' ? 'Nome da cor (ex.: Verde bandeira)' : 'Nome do material'} value={nome} onChange={(e) => setNome(e.target.value)} />
        {tipo === 'FITA' && <input type="color" value={hex} onChange={(e) => setHex(e.target.value)} aria-label="Cor" />}
        <button className="btn">Cadastrar</button>
      </form>

      <div className="linha">
        <label htmlFor="filtro">Mostrar</label>
        <select id="filtro" value={filtro} onChange={(e) => setFiltro(e.target.value as '' | TipoMaterial)}>
          <option value="">Todos</option>
          {Object.entries(TIPO_LABEL).map(([k, v]) => (
            <option key={k} value={k}>{v}</option>
          ))}
        </select>
      </div>

      {msg && <p className="erro">{msg}</p>}
      {loading && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}

      {data && (
        <table className="tabela">
          <thead>
            <tr><th /><th>Material</th><th>Disponível</th><th>Ativo</th><th>Entrada de estoque</th></tr>
          </thead>
          <tbody>
            {lista.map((m) => (
              <Linha key={m.id} m={m} onChange={reload} onErro={setMsg} />
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}
