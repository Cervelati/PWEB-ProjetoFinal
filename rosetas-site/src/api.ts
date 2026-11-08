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

/** O site público não tem login: todas as chamadas são anônimas. */
export async function api<T = void>(path: string, opts: Opts = {}): Promise<T> {
  const headers: Record<string, string> = { ...opts.headers };
  if (opts.body !== undefined) headers['Content-Type'] = 'application/json';

  const res = await fetch(path, {
    method: opts.method ?? 'GET',
    headers,
    body: opts.body !== undefined ? JSON.stringify(opts.body) : undefined,
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
