import { StatusPedido } from './types';

export const brl = (v: number) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);

export const dataHora = (iso: string) =>
  new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export const STATUS_LABEL: Record<StatusPedido, string> = {
  AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
  AGUARDANDO_APROVACAO: 'Aguardando aprovação',
  EM_PRODUCAO: 'Em produção',
  ENTREGUE: 'Entregue',
  CANCELADO: 'Cancelado',
};

export const BRAND = import.meta.env.VITE_BRAND_NAME ?? 'Escarapela';

export const SITE_URL = (import.meta.env.VITE_SITE_URL ?? 'http://localhost:5173').replace(/\/$/, '');

export const linkPedido = (codigo: string) => `${SITE_URL}/pedido/${codigo}`;

/** Link do WhatsApp (assume Brasil quando o número vem sem o 55). */
export function whatsapp(telefone: string, texto?: string) {
  const d = telefone.replace(/\D/g, '');
  const n = d.startsWith('55') && d.length >= 12 ? d : `55${d}`;
  return `https://wa.me/${n}${texto ? `?text=${encodeURIComponent(texto)}` : ''}`;
}

export function telefoneBr(t: string) {
  const d = t.replace(/\D/g, '');
  if (d.length === 11) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
  if (d.length === 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
  return t;
}

export const FRANZIDO_LABEL: Record<'AMERICANO' | 'NORMAL', string> = { AMERICANO: 'Americano', NORMAL: 'Normal' };

export const TIPO_LABEL: Record<string, string> = {
  FITA: 'Fita',
  BOTTON: 'Botton',
  ARGOLA: 'Argola',
  ALFINETE: 'Alfinete',
  OUTRO: 'Outro',
};
