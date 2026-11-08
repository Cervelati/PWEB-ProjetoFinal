import { FRANZIDO_LABEL } from '../format';
import { ItemPedido } from '../types';
import RosettePreview, { separarCores } from './RosettePreview';

/** Lista os itens com a prévia das cores. Não mostra valores (serve também para a ficha de produção). */
export default function ItensPedido({ itens }: { itens: ItemPedido[] }) {
  return (
    <div className="itens">
      {itens.map((i) => {
        const { corpo, rabiola } = separarCores(i.partes, (p) => p.fitaHex);
        return (
          <div key={i.id} className="item">
            <RosettePreview corpo={corpo} rabiola={rabiola} size={90} />
            <div>
              <strong>
                {i.quantidade}× {i.modeloNome}
              </strong>
              <div className="muted">
                {i.franzido && <>Franzido {FRANZIDO_LABEL[i.franzido].toLowerCase()}</>}
                {i.gravacoesExtras > 0 && <> · {i.gravacoesExtras} gravação(ões) extra</>}
              </div>
              {i.textoPersonalizado && <div className="muted">Texto: “{i.textoPersonalizado}”</div>}
              <ul className="partes">
                {i.partes.map((p) => (
                  <li key={p.parteId}>
                    <span className="swatch" style={{ background: p.fitaHex }} /> {p.parteNome}: {p.fitaNome}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        );
      })}
    </div>
  );
}
