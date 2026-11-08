import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api';
import { especificacao } from '../components/CartaoModelo';
import RosettePreview, { separarCores } from '../components/RosettePreview';
import { useCart } from '../cart';
import { FRANZIDO_LABEL, brl, precoDoFranzido } from '../format';
import { useLoad } from '../hooks';
import { Fita, Franzido, Modelo, Opcoes, Parte } from '../types';

function Seletor({
  titulo,
  partes,
  cores,
  escolhas,
  onEscolher,
  onTodas,
}: {
  titulo: string;
  partes: Parte[];
  cores: Fita[];
  escolhas: Record<number, Fita>;
  onEscolher: (p: Parte, f: Fita) => void;
  onTodas?: (f: Fita) => void;
}) {
  if (partes.length === 0) return null;
  return (
    <fieldset className="grupo">
      <legend>{titulo}</legend>
      {partes.map((p) => (
        <div key={p.id} className="campo">
          <label>
            {p.nome}
            {escolhas[p.id] && <span className="muted"> — {escolhas[p.id].nome}</span>}
          </label>
          <div className="cores">
            {cores.map((f) => (
              <button
                key={f.id}
                type="button"
                title={f.nome}
                aria-label={`${p.nome}: ${f.nome}`}
                className={`cor ${escolhas[p.id]?.id === f.id ? 'sel' : ''}`}
                style={{ background: f.hex ?? undefined }}
                onClick={() => onEscolher(p, f)}
              />
            ))}
          </div>
        </div>
      ))}
      {onTodas && partes.length > 1 && (
        <div className="campo">
          <label>Usar a mesma cor em todas</label>
          <div className="cores">
            {cores.map((f) => (
              <button
                key={f.id}
                type="button"
                title={f.nome}
                aria-label={`Todas: ${f.nome}`}
                className="cor"
                style={{ background: f.hex ?? undefined }}
                onClick={() => onTodas(f)}
              />
            ))}
          </div>
        </div>
      )}
    </fieldset>
  );
}

export default function Personalizar() {
  const { id } = useParams();
  const nav = useNavigate();
  const cart = useCart();
  const modelo = useLoad(() => api<Modelo>(`/api/catalogo/modelos/${id}`), [id]);
  const fitas = useLoad(() => api<Fita[]>('/api/catalogo/fitas'), []);
  const opcoes = useLoad(() => api<Opcoes>('/api/catalogo/opcoes'), []);
  const [escolhas, setEscolhas] = useState<Record<number, Fita>>({});
  const [franzido, setFranzido] = useState<Franzido | null>(null);
  const [qtd, setQtd] = useState(1);
  const [extras, setExtras] = useState(0);
  const [texto, setTexto] = useState('');

  const m = modelo.data;
  // Pré-seleciona o franzido mais barato oferecido pelo modelo.
  useEffect(() => {
    if (!m) return;
    if (m.precoAmericano == null && m.precoNormal == null) setFranzido(null);
    else if (m.precoNormal != null && (m.precoAmericano == null || m.precoNormal <= m.precoAmericano)) setFranzido('NORMAL');
    else setFranzido('AMERICANO');
  }, [m]);

  if (modelo.loading || fitas.loading) return <p className="muted">Carregando…</p>;
  if (modelo.error || !m) return <p className="erro">{modelo.error ?? 'Modelo não encontrado'}</p>;

  const cores = fitas.data ?? [];
  const corpo = m.partes.filter((p) => p.grupo === 'CORPO');
  const rabiola = m.partes.filter((p) => p.grupo === 'RABIOLA');
  const completo = m.partes.every((p) => escolhas[p.id]);
  const temFranzido = m.precoAmericano != null || m.precoNormal != null;
  const gravacaoExtra = opcoes.data?.precoGravacaoExtra ?? 1.2;
  const unitario = precoDoFranzido(m, franzido) + extras * gravacaoExtra;
  const prev = separarCores(m.partes, (p) => escolhas[p.id]?.hex ?? undefined);

  const escolher = (p: Parte, f: Fita) => setEscolhas((e) => ({ ...e, [p.id]: f }));
  const todas = (lista: Parte[]) => (f: Fita) =>
    setEscolhas((e) => ({ ...e, ...Object.fromEntries(lista.map((p) => [p.id, f])) }));

  function adicionar() {
    cart.add({ modelo: m!, quantidade: qtd, texto, franzido, gravacoesExtras: extras, precoUnitario: unitario, escolhas });
    nav('/carrinho');
  }

  return (
    <div className="duas-colunas largo">
      <div className="painel centro">
        <div className={`comparar ${m.imagemBase ? 'duas' : ''}`}>
          {m.imagemBase && (
            <figure>
              <img src={m.imagemBase} alt={`Foto do modelo ${m.nome}`} className="foto-modelo" />
              <figcaption>Foto do modelo</figcaption>
            </figure>
          )}
          <figure>
            <RosettePreview corpo={prev.corpo} rabiola={prev.rabiola} estrela={m.formato === 'estrela'} size={m.imagemBase ? 190 : 260} />
            <figcaption>Suas cores</figcaption>
          </figure>
        </div>
        <p className="aviso">A cor exibida é uma aproximação e pode variar do material real.</p>
      </div>

      <div>
        <h2>{m.nome}</h2>
        <p className="muted">{especificacao(m) || m.descricao}</p>
        {m.formato && <p className="muted">Modelo: {m.formato}</p>}

        {!m.disponivel && <p className="erro">Este modelo está indisponível no momento (falta material em estoque).</p>}
        {cores.length === 0 && <p className="erro">Nenhuma cor em estoque no momento.</p>}

        {temFranzido && (
          <fieldset className="grupo">
            <legend>Tipo de franzido</legend>
            {(['AMERICANO', 'NORMAL'] as Franzido[]).map((f) => {
              const preco = f === 'AMERICANO' ? m.precoAmericano : m.precoNormal;
              return (
                <label key={f} className={`opcao ${preco == null ? 'desligada' : ''}`}>
                  <input type="radio" name="franzido" disabled={preco == null} checked={franzido === f} onChange={() => setFranzido(f)} />{' '}
                  {FRANZIDO_LABEL[f]} {preco == null ? <span className="muted">(não disponível neste modelo)</span> : <strong>{brl(preco)}</strong>}
                </label>
              );
            })}
          </fieldset>
        )}

        <Seletor titulo={corpo.length > 1 ? 'Cores das voltas' : 'Cor da roseta'} partes={corpo} cores={cores} escolhas={escolhas} onEscolher={escolher} onTodas={todas(corpo)} />
        <Seletor titulo={rabiola.length > 1 ? 'Cores das fitas da rabiola' : 'Cor da fita'} partes={rabiola} cores={cores} escolhas={escolhas} onEscolher={escolher} onTodas={todas(rabiola)} />

        <div className="campo">
          <label htmlFor="qtd">Quantidade</label>
          <input id="qtd" type="number" min={1} max={1000} value={qtd} onChange={(e) => setQtd(Math.min(1000, Math.max(1, Number(e.target.value) || 1)))} />
        </div>

        <div className="campo">
          <label htmlFor="texto">Texto da gravação (opcional)</label>
          <input id="texto" maxLength={255} value={texto} onChange={(e) => setTexto(e.target.value)} placeholder="Ex.: nome do evento ou ano" />
          <small className="muted">
            Já incluídas no preço: {m.gravacoesInclusas} gravação(ões) ({m.categoria === 'Diversos' ? 'na fita' : 'no bottom e na fita'}). Para logotipo em cores no bottom,
            envie o arquivo pelo contato.
          </small>
        </div>

        <div className="campo">
          <label htmlFor="extras">Gravações extras por unidade (+ {brl(gravacaoExtra)} cada)</label>
          <input id="extras" type="number" min={0} max={20} value={extras} onChange={(e) => setExtras(Math.min(20, Math.max(0, Number(e.target.value) || 0)))} />
        </div>

        <p className="preco">
          {brl(unitario)} <small className="muted">por unidade</small>
        </p>
        <button className="btn grande" disabled={!completo || cores.length === 0 || !m.disponivel || (temFranzido && !franzido)} onClick={adicionar}>
          Adicionar ao carrinho — {brl(unitario * qtd)}
        </button>
        {!completo && <p className="muted">Escolha a cor de todas as partes para continuar.</p>}
      </div>
    </div>
  );
}
