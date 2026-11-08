import { useMemo, useState } from 'react';
import { api } from '../api';
import CartaoModelo from '../components/CartaoModelo';
import { useLoad } from '../hooks';
import { Modelo } from '../types';

const ORDEM = ['1 volta', '2 voltas', '3 voltas', '4 voltas', 'Especiais', 'Grande', 'Extra grande', 'Diversos'];
const ROTULO: Record<string, string> = {
  '1 volta': 'Rosetas de 1 volta',
  '2 voltas': 'Rosetas de 2 voltas',
  '3 voltas': 'Rosetas de 3 voltas',
  '4 voltas': 'Rosetas de 4 voltas',
  Especiais: 'Especiais',
  Grande: 'Grandes',
  'Extra grande': 'Extra grandes',
  Diversos: 'Fitas e bottons',
};

export default function Catalogo() {
  const { data, error, loading } = useLoad(() => api<Modelo[]>('/api/catalogo/modelos'), []);
  const [cat, setCat] = useState<string>('');
  const [busca, setBusca] = useState('');

  const categorias = useMemo(() => {
    const presentes = new Set((data ?? []).map((m) => m.categoria ?? 'Outros'));
    return [...ORDEM.filter((c) => presentes.has(c)), ...[...presentes].filter((c) => !ORDEM.includes(c))];
  }, [data]);

  const filtrados = useMemo(() => {
    const b = busca.trim().toLowerCase();
    return (data ?? []).filter(
      (m) =>
        (!cat || (m.categoria ?? 'Outros') === cat) &&
        (!b || `${m.referencia ?? ''} ${m.nome} ${m.formato ?? ''}`.toLowerCase().includes(b)),
    );
  }, [data, cat, busca]);

  if (loading) return <p className="muted">Carregando…</p>;
  if (error) return <p className="erro">{error}</p>;
  if (!data?.length) return <p>Nenhum modelo disponível no momento.</p>;

  return (
    <>
      <h2>Catálogo</h2>
      <div className="filtros">
        <input
          type="search"
          placeholder="Buscar por referência (ex.: 12.30.3 T)"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          aria-label="Buscar modelo"
        />
        <div className="chips">
          <button className={`chip ${cat === '' ? 'sel' : ''}`} onClick={() => setCat('')}>
            Todos
          </button>
          {categorias.map((c) => (
            <button key={c} className={`chip ${cat === c ? 'sel' : ''}`} onClick={() => setCat(c)}>
              {ROTULO[c] ?? c}
            </button>
          ))}
        </div>
      </div>
      {filtrados.length === 0 && <p>Nenhum modelo encontrado.</p>}
      <div className="grade">
        {filtrados.map((m) => (
          <CartaoModelo key={m.id} m={m} />
        ))}
      </div>
    </>
  );
}
