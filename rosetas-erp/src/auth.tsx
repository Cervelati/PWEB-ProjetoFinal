import { signInWithEmailAndPassword, signOut, onAuthStateChanged } from 'firebase/auth';
import { ReactNode, createContext, useContext, useEffect, useState } from 'react';
import { api, getDevUid, setDevUid } from './api';
import { firebaseAtivo, getFirebaseAuth } from './firebase';
import { Perfil, Usuario } from './types';

/** Usuários da equipe criados pelo seed do backend (perfil "dev"). */
export const DEV_USERS: { uid: string; label: string; perfil: Perfil; descricao: string }[] = [
  { uid: 'dev-func', label: 'Funcionária', perfil: 'FUNCIONARIA', descricao: 'Vê só as fichas de produção' },
  { uid: 'dev-admin', label: 'Administrador', perfil: 'ADMIN', descricao: 'Pedidos, clientes, estoque e preços' },
  { uid: 'dev-root', label: 'Root', perfil: 'ROOT', descricao: 'Tudo + gestão da equipe' },
];

interface AuthState {
  usuario: Usuario | null;
  loading: boolean;
  /** Modo desenvolvimento (sem Firebase). */
  login: (uid: string) => Promise<Usuario>;
  /** Login real com e-mail e senha (Firebase). */
  loginEmail: (email: string, senha: string) => Promise<Usuario>;
  logout: () => void;
}

const AuthCtx = createContext<AuthState>(null!);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [loading, setLoading] = useState(firebaseAtivo || !!getDevUid());

  useEffect(() => {
    if (firebaseAtivo) {
      // Restaura a sessão do Firebase ao abrir/recarregar a página.
      return onAuthStateChanged(getFirebaseAuth(), (fb) => {
        if (!fb) {
          setUsuario(null);
          setLoading(false);
          return;
        }
        api<Usuario>('/api/usuarios/me')
          .then(setUsuario)
          .catch(() => {
            setUsuario(null);
            void signOut(getFirebaseAuth());
          })
          .finally(() => setLoading(false));
      });
    }
    if (!getDevUid()) return;
    api<Usuario>('/api/usuarios/me')
      .then(setUsuario)
      .catch(() => setDevUid(null))
      .finally(() => setLoading(false));
  }, []);

  const login = async (uid: string) => {
    setDevUid(uid);
    try {
      const me = await api<Usuario>('/api/usuarios/me');
      setUsuario(me);
      return me;
    } catch (e) {
      setDevUid(null);
      throw e;
    }
  };

  const loginEmail = async (email: string, senha: string) => {
    await signInWithEmailAndPassword(getFirebaseAuth(), email, senha);
    try {
      const me = await api<Usuario>('/api/usuarios/me');
      setUsuario(me);
      return me;
    } catch (e) {
      await signOut(getFirebaseAuth());
      throw new Error('Seu usuário existe no login, mas ainda não foi cadastrado na equipe. Fale com o responsável (Root).');
    }
  };

  const logout = () => {
    if (firebaseAtivo) void signOut(getFirebaseAuth());
    else setDevUid(null);
    setUsuario(null);
  };

  return <AuthCtx.Provider value={{ usuario, loading, login, loginEmail, logout }}>{children}</AuthCtx.Provider>;
}

export const useAuth = () => useContext(AuthCtx);

export const isGestao = (u: Usuario | null) => u?.perfil === 'ADMIN' || u?.perfil === 'ROOT';
