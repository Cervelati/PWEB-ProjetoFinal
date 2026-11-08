const VAZIO = '#d9d3c7';

interface Props {
  /** Uma cor por volta, da externa para a interna. */
  corpo: (string | undefined)[];
  /** Uma cor por fita da rabiola. */
  rabiola?: (string | undefined)[];
  /** Pétalas pontiagudas (modelos "estrela"). */
  estrela?: boolean;
  size?: number;
}

/** Separa as partes de um modelo/item em cores do corpo e da rabiola, na ordem. */
export function separarCores<T extends { grupo?: string; parteGrupo?: string }>(
  partes: T[],
  cor: (p: T) => string | undefined,
): { corpo: (string | undefined)[]; rabiola: (string | undefined)[] } {
  const corpo: (string | undefined)[] = [];
  const rabiola: (string | undefined)[] = [];
  for (const p of partes) ((p.grupo ?? p.parteGrupo) === 'RABIOLA' ? rabiola : corpo).push(cor(p));
  return { corpo, rabiola };
}

/**
 * Prévia em SVG da roseta (RF02/RN15). É uma aproximação: a cor real do material pode variar (RNF11).
 * Cada volta vira um anel de pétalas; cada fita da rabiola, uma ponta.
 */
export default function RosettePreview({ corpo, rabiola = [], estrela = false, size = 220 }: Props) {
  const aneis = corpo.length ? corpo : [undefined];
  const nTails = Math.min(rabiola.length, 9);
  const cx = 120;
  const cy = 112;
  const abertura = nTails > 1 ? Math.min(7 * (nTails - 1), 44) : 0;

  return (
    <svg viewBox="0 0 240 290" width={size} height={(size * 290) / 240} role="img" aria-label="Prévia da roseta">
      {/* rabiola: uma fita por cor, abrindo em leque */}
      {rabiola.slice(0, nTails).map((cor, i) => {
        const ang = nTails > 1 ? -abertura + (2 * abertura * i) / (nTails - 1) : 0;
        return (
          <polygon
            key={`t${i}`}
            points={`${cx - 9},${cy} ${cx + 9},${cy} ${cx + 11},268 ${cx - 11},268`}
            fill={cor ?? VAZIO}
            stroke="rgba(0,0,0,.28)"
            transform={`rotate(${ang} ${cx} ${cy})`}
          />
        );
      })}

      {aneis.map((cor, k) => {
        const total = aneis.length;
        const raio = Math.max(24, 82 - k * (total > 1 ? 54 / (total - 1) : 0) * 0.9);
        const n = Math.max(10, Math.round(30 - k * (total > 3 ? 4 : 6)));
        const ry = Math.max(11, 38 - k * (total > 3 ? 6 : 9));
        const rx = Math.max(6, 15 - k * 2);
        return Array.from({ length: n }, (_, i) =>
          estrela && k === 0 ? (
            <polygon
              key={`${k}-${i}`}
              points={`${cx},${cy - raio - ry * 0.8} ${cx - rx},${cy - raio + ry * 0.3} ${cx + rx},${cy - raio + ry * 0.3}`}
              fill={cor ?? VAZIO}
              stroke="rgba(0,0,0,.25)"
              transform={`rotate(${(360 / n) * i} ${cx} ${cy})`}
            />
          ) : (
            <ellipse
              key={`${k}-${i}`}
              cx={cx}
              cy={cy - raio + ry * 0.2}
              rx={rx}
              ry={ry}
              fill={cor ?? VAZIO}
              stroke="rgba(0,0,0,.22)"
              strokeWidth="1"
              transform={`rotate(${(360 / n) * i + k * 7} ${cx} ${cy})`}
            />
          ),
        );
      })}

      {/* botton */}
      <circle cx={cx} cy={cy} r="17" fill="#f4f1ea" stroke="rgba(0,0,0,.4)" strokeWidth="1.5" />
      <circle cx={cx} cy={cy} r="9" fill="rgba(0,0,0,.08)" />
    </svg>
  );
}
