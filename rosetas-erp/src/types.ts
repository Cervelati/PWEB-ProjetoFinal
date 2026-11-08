export type Perfil = 'FUNCIONARIA' | 'ADMIN' | 'ROOT';

export type StatusPedido =
  | 'AGUARDANDO_PAGAMENTO'
  | 'AGUARDANDO_APROVACAO'
  | 'EM_PRODUCAO'
  | 'ENTREGUE'
  | 'CANCELADO';

export interface Usuario {
  id: string;
  nome: string;
  email: string;
  telefone: string | null;
  perfil: Perfil;
  ativo: boolean;
}

export type TipoMaterial = 'FITA' | 'BOTTON' | 'ARGOLA' | 'ALFINETE' | 'OUTRO';
export type UnidadeMaterial = 'METRO' | 'UNIDADE';
export type Grupo = 'CORPO' | 'RABIOLA';
export type Franzido = 'AMERICANO' | 'NORMAL';

export interface Parte {
  id: number;
  nome: string;
  grupo: Grupo;
  metrosConsumidos: number;
}

export interface Modelo {
  id: number;
  referencia: string | null;
  nome: string;
  descricao: string | null;
  categoria: string | null;
  formato: string | null;
  diametroCm: number | null;
  voltas: number | null;
  rabiolaQtd: number;
  rabiolaComprimentoCm: number | null;
  larguraFitaCm: number | null;
  /** Menor preço ("a partir de"); modelos sem franzido usam só este. */
  preco: number;
  precoAmericano: number | null;
  precoNormal: number | null;
  gravacoesInclusas: number;
  imagemBase: string | null;
  ativo: boolean;
  /** false quando falta material fixo (botton, argola...) em estoque. */
  disponivel: boolean;
  partes: Parte[];
  componentes: Componente[];
}

export interface Opcoes {
  precoGravacaoExtra: number;
}

export interface Componente {
  id: number;
  materialId: number;
  materialNome: string;
  tipo: TipoMaterial;
  unidade: UnidadeMaterial;
  quantidade: number;
}

export interface Fita {
  id: number;
  nome: string;
  hex: string;
  foto: string | null;
}

export interface Personalizacao {
  parteId: number;
  parteNome: string;
  parteGrupo: Grupo;
  fitaId: number;
  fitaNome: string;
  fitaHex: string;
}

export interface ItemPedido {
  id: number;
  modeloId: number;
  modeloNome: string;
  referencia: string | null;
  quantidade: number;
  franzido: Franzido | null;
  gravacoesExtras: number;
  textoPersonalizado: string | null;
  partes: Personalizacao[];
}

export interface Material {
  id: number;
  nome: string;
  tipo: TipoMaterial;
  unidade: UnidadeMaterial;
  hex: string | null;
  saldo: number;
  foto: string | null;
  ativo: boolean;
}

export interface Pagamento {
  id: number;
  valor: number;
  metodo: 'PIX';
  status: 'PENDENTE' | 'APROVADO' | 'FALHOU' | 'EXPIRADO';
  pixCopiaCola: string | null;
}

export interface Cliente {
  id: number;
  nome: string;
  telefone: string;
  email: string | null;
}

export interface Pedido {
  id: number;
  cliente: Cliente;
  codigoAcompanhamento: string;
  status: StatusPedido;
  valorTotal: number;
  data: string;
  observacoes: string | null;
  itens: ItemPedido[];
  pagamento: Pagamento | null;
}

export interface Ficha {
  pedidoId: number;
  status: StatusPedido;
  data: string;
  observacoes: string | null;
  itens: ItemPedido[];
}
