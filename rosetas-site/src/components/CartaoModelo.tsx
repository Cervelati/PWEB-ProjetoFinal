import { Link } from 'react-router-dom';
import { brl } from '../format';
import { Modelo } from '../types';
import RosettePreview from './RosettePreview';

/** Linha de especificação: diâmetro, voltas e rabiola. */
export function especificacao(m: Modelo): string {
  const p: string[] = [];
  if (m.diametroCm) p.push(`${m.diametroCm} cm`);
  if (m.voltas) p.push(`${m.voltas} volta${m.voltas > 1 ? 's' : ''}`);
  if (m.rabiolaQtd > 0) {
    p.push(
      `rabiola ${m.rabiolaQtd}× ${m.rabiolaComprimentoCm ?? ''}cm${m.larguraFitaCm ? ` (${m.larguraFitaCm}cm larg.)` : ''}`,
    );
  }
  return p.join(' · ');
}

const COR_EXEMPLO = ['#C8102E', '#FFFFFF', '#1F3FBF', '#F2C200', '#1E8E3E'];

export default function CartaoModelo({ m }: { m: Modelo }) {
  const corpo = m.partes.filter((p) => p.grupo === 'CORPO').map((_, i) => COR_EXEMPLO[i % COR_EXEMPLO.length]);
  const rabiola = m.partes.filter((p) => p.grupo === 'RABIOLA').map((_, i) => COR_EXEMPLO[(i + 2) % COR_EXEMPLO.length]);
  const aPartirDe = m.precoAmericano != null && m.precoNormal != null && m.precoAmericano !== m.precoNormal;

  return (
    <Link to={`/catalogo/${m.id}`} className={`cartao clicavel ${m.disponivel ? '' : 'indisponivel'}`}>
      {m.imagemBase ? (
        <img src={m.imagemBase} alt={m.nome} className="foto" />
      ) : (
        <RosettePreview corpo={corpo} rabiola={rabiola} estrela={m.formato === 'estrela'} size={110} />
      )}
      <strong>{m.nome}</strong>
      <span className="muted">{especificacao(m) || m.descricao}</span>
      <span className="preco">
        {aPartirDe && <small className="muted">a partir de </small>}
        {brl(m.preco)}
      </span>
      {!m.disponivel && <span className="selo">Indisponível no momento</span>}
    </Link>
  );
}
