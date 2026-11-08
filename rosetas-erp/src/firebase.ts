import { FirebaseApp, initializeApp } from 'firebase/app';
import { Auth, getAuth } from 'firebase/auth';

const cfg = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY as string | undefined,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN as string | undefined,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID as string | undefined,
  appId: import.meta.env.VITE_FIREBASE_APP_ID as string | undefined,
};

/** Com a chave do Firebase configurada o ERP usa login de verdade; sem ela, cai no seletor de desenvolvimento. */
export const firebaseAtivo = !!cfg.apiKey;

let app: FirebaseApp | null = null;
let auth: Auth | null = null;

export function getFirebaseAuth(): Auth {
  if (!auth) {
    app = initializeApp(cfg);
    auth = getAuth(app);
  }
  return auth;
}

/** ID token atual (renovado automaticamente pelo SDK), ou null se não há usuário logado. */
export async function idToken(): Promise<string | null> {
  const a = getFirebaseAuth();
  await a.authStateReady();
  return a.currentUser ? a.currentUser.getIdToken() : null;
}
