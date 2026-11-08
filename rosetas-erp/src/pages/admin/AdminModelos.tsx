import { useMemo, useState } from 'react';
import { api, msgErro } from '../../api';
import RosettePreview from '../../components/RosettePreview';
import { brl } from '../../format';
import { useLoad } from '../../hooks';
import { Componente, Grupo, Material, Modelo, Parte } from '../../types';

const num = (s: string) => Number(s.replace(',', '.'));
const opt = (s: string) => (s.trim() === '' ? null : num(s));
const txt = (v: number | null | undefined) => (v == null ? '' : String(v));

type Erro = (m: string) => void;

function ParteLinha({ p, onChange, onErro }: { p: Parte; onChange: () => void; onErro: Erro }) {
  const [nome, setNome] = useState(p.nome);
  const [grupo, setGrupo] = useState<Grupo>(p.grupo);
  const [metros, setMetros] = useState(String(p.metrosConsumidos));

  async function salvar() {
    try {
      await api(`/api/admin/modelos/partes/${p.id}`, { method: 'PUT', body: { nome, grupo, metrosConsumidos: num(metros) } });
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }
  async function remover() {
    if (!confirm(`Remover "${p.nome}"?`)) return;
    try {
      await api(`/api/admin/modelos/partes/${p.id}`, { method: 'DELETE' });
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }
  return (
    <div className="linha">
      <input value={nome} onChange={(e) => setNome(e.target.value)} aria-label="Nome da parte" />
      <select value={grupo} onChange={(e) => setGrupo(e.target.value as Grupo)} aria-label="Grupo">
        <option value="CORPO">Corpo</option>
        <option value="RABIOLA">Rabiola</option>
      </select>
      <input className="curto" inputMode="decimal" value={metros} onChange={(e) => setMetros(e.target.value)} aria-label="Metros" />
      <span className="muted">m</span>
      <button className="btn pequeno secundario" onClick={salvar}>Salvar</button>
      <button className="link" onClick={remover}>Remover</button>
    </div>
  );
}

function ComponenteLinha({ c, onChange, onErro }: { c: Componente; onChange: () => void; onErro: Erro }) {
  const [qtd, setQtd] = useState(String(c.quantidade));
  async function salvar() {
    try {
      await api(`/api/admin/modelos/componentes/${c.id}`, { method: 'PUT', body: { materialId: c.materialId, quantidade: num(qtd) } });
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }
  async function remover() {
    try {
      await api(`/api/admin/modelos/componentes/${c.id}`, { method: 'DELETE' });
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }
  return (
    <div className="linha">
      <span>{c.materialNome}</span>
      <input className="curto" inputMode="decimal" value={qtd} onChange={(e) => setQtd(e.target.value)} aria-label="Quantidade" />
      <span className="muted">{c.unidade === 'METRO' ? 'm' : 'un.'} por unidade</span>
      <button className="btn pequeno secundario" onClick={salvar}>Salvar</button>
      <button className="link" onClick={remover}>Remover</button>
    </div>
  );
}

function ModeloCard({ m, materiais, onChange, onErro }: { m: Modelo; materiais: Material[]; onChange: () => void; onErro: Erro }) {
  const [aberto, setAberto] = useState(false);
  const [f, setF] = useState({
    referencia: m.referencia ?? '',
    nome: m.nome,
    descricao: m.descricao ?? '',
    categoria: m.categoria ?? '',
    formato: m.formato ?? '',
    diametroCm: txt(m.diametroCm),
    voltas: txt(m.voltas),
    rabiolaQtd: String(m.rabiolaQtd),
    rabiolaComprimentoCm: txt(m.rabiolaComprimentoCm),
    larguraFitaCm: txt(m.larguraFitaCm),
    gravacoesInclusas: String(m.gravacoesInclusas),
  });
  const [preco, setPreco] = useState(txt(m.preco));
  const [pAmer, setPAmer] = useState(txt(m.precoAmericano));
  const [pNorm, setPNorm] = useState(txt(m.precoNormal));
  const [np, setNp] = useState({ nome: '', grupo: 'CORPO' as Grupo, metros: '' });
  const [nc, setNc] = useState({ materialId: '', quantidade: '1' });
  const set = (k: keyof typeof f) => (e: { target: { value: string } }) => setF({ ...f, [k]: e.target.value });

  async function run(fn: () => Promise<unknown>) {
    try {
      await fn();
      onChange();
    } catch (e) {
      onErro(msgErro(e));
    }
  }

  const salvarPreco = () =>
    run(() =>
      api(`/api/admin/modelos/${m.id}/preco`, {
        method: 'PUT',
        body: { preco: num(preco), precoAmericano: opt(pAmer), precoNormal: opt(pNorm) },
      }),
    );

  const salvarDados = (ativo: boolean) =>
    run(() =>
      api(`/api/admin/modelos/${m.id}`, {
        method: 'PUT',
        body: {
          referencia: f.referencia || null,
          nome: f.nome,
          descricao: f.descricao || null,
          categoria: f.categoria || null,
          formato: f.formato || null,
          diametroCm: opt(f.diametroCm),
          voltas: opt(f.voltas),
          rabiolaQtd: num(f.rabiolaQtd) || 0,
          rabiolaComprimentoCm: opt(f.rabiolaComprimentoCm),
          larguraFitaCm: opt(f.larguraFitaCm),
          preco: num(preco),
          precoAmericano: opt(pAmer),
          precoNormal: opt(pNorm),
          gravacoesInclusas: num(f.gravacoesInclusas) || 0,
          imagemBase: m.imagemBase,
          ativo,
        },
      }),
    );

  const corpo = m.partes.filter((p) => p.grupo === 'CORPO').map(() => undefined);
  const rabiola = m.partes.filter((p) => p.grupo === 'RABIOLA').map(() => undefined);

  return (
    <div className={`painel ${m.ativo ? '' : 'inativo'}`}>
      <div className="linha entre">
        <h3>
          {m.referencia && <small className="muted">{m.referencia} · </small>}
          {m.nome} {!m.ativo && <small className="muted">(inativo)</small>}
          {m.ativo && !m.disponivel && <small className="erro"> (falta material)</small>}
        </h3>
        <div className="linha">
          <strong>{brl(m.preco)}</strong>
          <button className="btn pequeno secundario" onClick={() => setAberto(!aberto)}>{aberto ? 'Fechar' : 'Editar'}</button>
        </div>
      </div>

      {aberto && (
        <div className="duas-colunas pequeno">
          <RosettePreview corpo={corpo} rabiola={rabiola} estrela={m.formato === 'estrela'} size={90} />
          <div>
            <h4>Foto do modelo</h4>
            <div className="linha">
              {m.imagemBase ? (
                <img src={m.imagemBase} alt={m.nome} className="foto-mini" />
              ) : (
                <span className="muted">Sem foto.</span>
              )}
              <input
                type="file"
                accept="image/jpeg,image/png,image/webp"
                aria-label="Enviar foto"
                onChange={(e) => {
                  const arq = e.target.files?.[0];
                  e.target.value = '';
                  if (!arq) return;
                  if (arq.size > 5 * 1024 * 1024) return onErro('A foto deve ter no máximo 5 MB');
                  const fd = new FormData();
                  fd.append('arquivo', arq);
                  run(() => api(`/api/admin/modelos/${m.id}/foto`, { method: 'POST', body: fd }));
                }}
              />
              {m.imagemBase && (
                <button className="link" onClick={() => run(() => api(`/api/admin/modelos/${m.id}/foto`, { method: 'DELETE' }))}>
                  Remover foto
                </button>
              )}
            </div>

            <h4>Preços</h4>
            <div className="linha destaque">
              <label>Base (R$)</label>
              <input className="curto" inputMode="decimal" value={preco} onChange={(e) => setPreco(e.target.value)} />
              <label>Americano</label>
              <input className="curto" inputMode="decimal" placeholder="não oferece" value={pAmer} onChange={(e) => setPAmer(e.target.value)} />
              <label>Normal</label>
              <input className="curto" inputMode="decimal" placeholder="não oferece" value={pNorm} onChange={(e) => setPNorm(e.target.value)} />
              <button className="btn pequeno" disabled={!(num(preco) > 0)} onClick={salvarPreco}>Salvar preços</button>
            </div>
            <small className="muted">Vale para novos pedidos; pedidos já feitos mantêm o valor. Deixe em branco o franzido que o modelo não oferece.</small>

            <h4>Dados do modelo</h4>
            <div className="linha">
              <input className="curto" placeholder="Referência" value={f.referencia} onChange={set('referencia')} />
              <input placeholder="Nome" value={f.nome} onChange={set('nome')} />
              <input placeholder="Categoria" value={f.categoria} onChange={set('categoria')} />
              <input className="curto" placeholder="Formato" value={f.formato} onChange={set('formato')} />
            </div>
            <div className="linha">
              <input className="curto" inputMode="decimal" placeholder="Diâm. cm" value={f.diametroCm} onChange={set('diametroCm')} />
              <input className="curto" inputMode="numeric" placeholder="Voltas" value={f.voltas} onChange={set('voltas')} />
              <input className="curto" inputMode="numeric" placeholder="Fitas rabiola" value={f.rabiolaQtd} onChange={set('rabiolaQtd')} />
              <input className="curto" inputMode="decimal" placeholder="Compr. rabiola cm" value={f.rabiolaComprimentoCm} onChange={set('rabiolaComprimentoCm')} />
              <input className="curto" inputMode="decimal" placeholder="Largura cm" value={f.larguraFitaCm} onChange={set('larguraFitaCm')} />
              <input className="curto" inputMode="numeric" placeholder="Gravações incl." value={f.gravacoesInclusas} onChange={set('gravacoesInclusas')} />
            </div>
            <input placeholder="Descrição" value={f.descricao} onChange={set('descricao')} />
            <div className="linha">
              <button className="btn pequeno" onClick={() => salvarDados(m.ativo)}>Salvar dados</button>
              <button className="btn pequeno secundario" onClick={() => salvarDados(!m.ativo)}>{m.ativo ? 'Desativar' : 'Ativar'}</button>
            </div>

            <h4>Ficha técnica — fitas (uma cor escolhida pelo cliente por linha)</h4>
            {m.partes.map((p) => (
              <ParteLinha key={p.id} p={p} onChange={onChange} onErro={onErro} />
            ))}
            <div className="linha">
              <input placeholder="Nova parte" value={np.nome} onChange={(e) => setNp({ ...np, nome: e.target.value })} />
              <select value={np.grupo} onChange={(e) => setNp({ ...np, grupo: e.target.value as Grupo })}>
                <option value="CORPO">Corpo</option>
                <option value="RABIOLA">Rabiola</option>
              </select>
              <input className="curto" inputMode="decimal" placeholder="metros" value={np.metros} onChange={(e) => setNp({ ...np, metros: e.target.value })} />
              <button
                className="btn pequeno"
                disabled={!np.nome || !(num(np.metros) > 0)}
                onClick={() =>
                  run(async () => {
                    await api(`/api/admin/modelos/${m.id}/partes`, { method: 'POST', body: { nome: np.nome, grupo: np.grupo, metrosConsumidos: num(np.metros) } });
                    setNp({ nome: '', grupo: 'CORPO', metros: '' });
                  })
                }
              >
                Adicionar parte
              </button>
            </div>

            <h4>Materiais fixos (botton, argola, alfinete…)</h4>
            {m.componentes.map((c) => (
              <ComponenteLinha key={c.id} c={c} onChange={onChange} onErro={onErro} />
            ))}
            <div className="linha">
              <select value={nc.materialId} onChange={(e) => setNc({ ...nc, materialId: e.target.value })} aria-label="Material">
                <option value="">Material…</option>
                {materiais.filter((x) => x.tipo !== 'FITA').map((x) => (
                  <option key={x.id} value={x.id}>{x.nome}</option>
                ))}
              </select>
              <input className="curto" inputMode="decimal" value={nc.quantidade} onChange={(e) => setNc({ ...nc, quantidade: e.target.value })} />
              <button
                className="btn pequeno"
                disabled={!nc.materialId || !(num(nc.quantidade) > 0)}
                onClick={() =>
                  run(async () => {
                    await api(`/api/admin/modelos/${m.id}/componentes`, { method: 'POST', body: { materialId: Number(nc.materialId), quantidade: num(nc.quantidade) } });
                    setNc({ materialId: '', quantidade: '1' });
                  })
                }
              >
                Adicionar material
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default function AdminModelos() {
  const { data, error, loading, reload } = useLoad(() => api<Modelo[]>('/api/admin/modelos'), []);
  const mats = useLoad(() => api<Material[]>('/api/admin/materiais'), []);
  const [msg, setMsg] = useState<string | null>(null);
  const [busca, setBusca] = useState('');
  const [cat, setCat] = useState('');
  const [novo, setNovo] = useState({ referencia: '', nome: '', categoria: '', preco: '' });

  const categorias = useMemo(() => [...new Set((data ?? []).map((m) => m.categoria ?? 'Outros'))], [data]);
  const lista = useMemo(() => {
    const b = busca.trim().toLowerCase();
    return (data ?? []).filter(
      (m) => (!cat || (m.categoria ?? 'Outros') === cat) && (!b || `${m.referencia ?? ''} ${m.nome}`.toLowerCase().includes(b)),
    );
  }, [data, busca, cat]);

  async function criar() {
    setMsg(null);
    try {
      await api('/api/admin/modelos', {
        method: 'POST',
        body: {
          referencia: novo.referencia || null,
          nome: novo.nome,
          descricao: null,
          categoria: novo.categoria || null,
          formato: null,
          diametroCm: null,
          voltas: null,
          rabiolaQtd: 0,
          rabiolaComprimentoCm: null,
          larguraFitaCm: null,
          preco: num(novo.preco),
          precoAmericano: null,
          precoNormal: null,
          gravacoesInclusas: 2,
          imagemBase: null,
          partes: [{ nome: 'Fita', grupo: 'CORPO', metrosConsumidos: 1 }],
          componentes: [],
        },
      });
      setNovo({ referencia: '', nome: '', categoria: '', preco: '' });
      reload();
    } catch (err) {
      setMsg(msgErro(err));
    }
  }

  return (
    <>
      <h2>Modelos e preços</h2>
      <p className="muted">
        Catálogo importado da lista de preços 2026. Abra “Editar” para ajustar preços, ficha técnica (consumo de fita) e materiais fixos de cada modelo.
      </p>

      <div className="painel">
        <h3>Novo modelo</h3>
        <div className="linha">
          <input className="curto" placeholder="Referência" value={novo.referencia} onChange={(e) => setNovo({ ...novo, referencia: e.target.value })} />
          <input placeholder="Nome" value={novo.nome} onChange={(e) => setNovo({ ...novo, nome: e.target.value })} />
          <input placeholder="Categoria" value={novo.categoria} onChange={(e) => setNovo({ ...novo, categoria: e.target.value })} />
          <input className="curto" inputMode="decimal" placeholder="Preço" value={novo.preco} onChange={(e) => setNovo({ ...novo, preco: e.target.value })} />
          <button className="btn" disabled={!novo.nome || !(num(novo.preco) > 0)} onClick={criar}>Criar</button>
        </div>
        <small className="muted">Cria com uma fita de 1 m; depois abra “Editar” para detalhar voltas, rabiola, consumo e botton.</small>
      </div>

      <div className="linha">
        <input type="search" placeholder="Buscar por referência ou nome" value={busca} onChange={(e) => setBusca(e.target.value)} />
        <select value={cat} onChange={(e) => setCat(e.target.value)} aria-label="Categoria">
          <option value="">Todas as categorias</option>
          {categorias.map((c) => (
            <option key={c} value={c}>{c}</option>
          ))}
        </select>
        <span className="muted">{lista.length} modelo(s)</span>
      </div>

      {msg && <p className="erro">{msg}</p>}
      {loading && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}
      {lista.map((m) => (
        <ModeloCard key={m.id} m={m} materiais={mats.data ?? []} onChange={reload} onErro={setMsg} />
      ))}
    </>
  );
}
