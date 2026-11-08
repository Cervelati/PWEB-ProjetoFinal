import { ReactNode, createContext, useContext, useEffect, useState } from 'react';
import { Fita, Franzido, Modelo } from './types';

export interface CartItem {
  key: string;
  modelo: Modelo;
  quantidade: number;
  texto: string;
  franzido: Franzido | null;
  gravacoesExtras: number;
  /** Preço por unidade já com franzido e gravações extras (conferido de novo pelo servidor ao fechar o pedido). */
  precoUnitario: number;
  /** parteId -> fita escolhida */
  escolhas: Record<number, Fita>;
}

interface CartState {
  itens: CartItem[];
  add: (i: Omit<CartItem, 'key'>) => void;
  remove: (key: string) => void;
  clear: () => void;
  total: number;
}

const KEY = 'rosetas.cart.v2';
const CartCtx = createContext<CartState>(null!);

function carregar(): CartItem[] {
  try {
    return JSON.parse(localStorage.getItem(KEY) ?? '[]');
  } catch {
    return [];
  }
}

export function CartProvider({ children }: { children: ReactNode }) {
  const [itens, setItens] = useState<CartItem[]>(carregar);

  useEffect(() => {
    try {
      localStorage.setItem(KEY, JSON.stringify(itens));
    } catch {
      /* ignora */
    }
  }, [itens]);

  const add: CartState['add'] = (i) =>
    setItens((l) => [...l, { ...i, key: `${i.modelo.id}-${Date.now()}` }]);
  const remove = (key: string) => setItens((l) => l.filter((i) => i.key !== key));
  const clear = () => setItens([]);
  const total = itens.reduce((s, i) => s + i.precoUnitario * i.quantidade, 0);

  return <CartCtx.Provider value={{ itens, add, remove, clear, total }}>{children}</CartCtx.Provider>;
}

export const useCart = () => useContext(CartCtx);
