import { StatusPedido } from './types';

export const brl = (v: number) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);

export const dataHora = (iso: string) =>
  new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export const STATUS_LABEL: Record<StatusPedido, string> = {
  AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
  AGUARDANDO_APROVACAO: 'Pagamento confirmado — em análise',
  EM_PRODUCAO: 'Em produção',
  ENTREGUE: 'Entregue',
  CANCELADO: 'Cancelado',
};

export const BRAND = import.meta.env.VITE_BRAND_NAME ?? 'Escarapela';
export const EMPRESA = 'Escarapela Comércio e Confecções Ltda. - ME';
export const CONTATO = { email: 'escarapela.16@gmail.com', telefone: '(11) 97195-5227' };

export const FRANZIDO_LABEL: Record<'AMERICANO' | 'NORMAL', string> = { AMERICANO: 'Americano', NORMAL: 'Normal' };

/** Preço por unidade do franzido (ou o preço base quando o modelo não tem franzido). */
export function precoDoFranzido(
  m: { preco: number; precoAmericano: number | null; precoNormal: number | null },
  f: 'AMERICANO' | 'NORMAL' | null,
): number {
  if (m.precoAmericano == null && m.precoNormal == null) return m.preco;
  return (f === 'AMERICANO' ? m.precoAmericano : m.precoNormal) ?? m.preco;
}
