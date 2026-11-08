import { firebaseAtivo, idToken } from './firebase';

const KEY = 'rosetas.devUid';

export function getDevUid(): string | null {
  try {
    return localStorage.getItem(KEY);
  } catch {
    return null;
  }
}

export function setDevUid(uid: string | null) {
  try {
    if (uid) localStorage.setItem(KEY, uid);
    else localStorage.removeItem(KEY);
  } catch {
    /* ignora */
  }
}

export class ApiError extends Error {
  status: number;
  campos?: Record<string, string>;
  constructor(status: number, message: string, campos?: Record<string, string>) {
    super(message);
    this.status = status;
    this.campos = campos;
  }
}

interface Opts {
  method?: string;
  body?: unknown;
  headers?: Record<string, string>;
}

/**
 * Modo dev: identifica o usuário pelo header X-Dev-Uid.
 * Com o Firebase, troque por: headers.Authorization = `Bearer ${await user.getIdToken()}`.
 */
export async function api<T = void>(path: string, opts: Opts = {}): Promise<T> {
  const headers: Record<string, string> = { ...opts.headers };
  const form = typeof FormData !== 'undefined' && opts.body instanceof FormData;
  if (opts.body !== undefined && !form) headers['Content-Type'] = 'application/json';
  if (firebaseAtivo) {
    const token = await idToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
  } else {
    const uid = getDevUid();
    if (uid) headers['X-Dev-Uid'] = uid;
  }

  const res = await fetch(path, {
    method: opts.method ?? 'GET',
    headers,
    body: opts.body === undefined ? undefined : form ? (opts.body as FormData) : JSON.stringify(opts.body),
  });

  if (!res.ok) {
    let detail = `Erro ${res.status}`;
    let campos: Record<string, string> | undefined;
    try {
      const pd = await res.json();
      if (pd.detail) detail = pd.detail;
      campos = pd.campos;
    } catch {
      /* corpo vazio */
    }
    throw new ApiError(res.status, detail, campos);
  }
  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}

export function msgErro(e: unknown): string {
  if (e instanceof ApiError) {
    if (e.campos) {
      const lista = Object.entries(e.campos).map(([k, v]) => `${k}: ${v}`);
      return `${e.message} (${lista.join('; ')})`;
    }
    return e.message;
  }
  return e instanceof Error ? e.message : 'Erro inesperado';
}
