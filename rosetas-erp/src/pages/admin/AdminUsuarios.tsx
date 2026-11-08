import { FormEvent, useState } from 'react';
import { api, msgErro } from '../../api';
import { useLoad } from '../../hooks';
import { Perfil, Usuario } from '../../types';

const PERFIS: Perfil[] = ['FUNCIONARIA', 'ADMIN'];

export default function AdminUsuarios() {
  const { data, error, loading, reload } = useLoad(() => api<Usuario[]>('/api/admin/usuarios'), []);
  const [msg, setMsg] = useState<string | null>(null);

  async function patch(id: string, campo: 'perfil' | 'ativo', body: object) {
    setMsg(null);
    try {
      await api(`/api/admin/usuarios/${id}/${campo}`, { method: 'PATCH', body });
      reload();
    } catch (e) {
      setMsg(msgErro(e));
    }
  }

  const [form, setForm] = useState({ id: '', nome: '', email: '', telefone: '', perfil: 'FUNCIONARIA' as Perfil });

  async function criar(e: FormEvent) {
    e.preventDefault();
    setMsg(null);
    try {
      await api('/api/admin/usuarios', { method: 'POST', body: { ...form, telefone: form.telefone || null } });
      setForm({ id: '', nome: '', email: '', telefone: '', perfil: 'FUNCIONARIA' });
      reload();
    } catch (err) {
      setMsg(msgErro(err));
    }
  }

  return (
    <>
      <h2>Equipe</h2>
      <p className="muted">A conta ROOT não pode ser alterada. Clientes não têm conta: só a equipe acessa este sistema.</p>
      <form className="painel" onSubmit={criar}>
        <h3>Novo membro da equipe</h3>
        <div className="linha">
          <input required placeholder="Nome" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} />
          <input required type="email" placeholder="E-mail" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <input placeholder="Telefone (opcional)" value={form.telefone} onChange={(e) => setForm({ ...form, telefone: e.target.value })} />
        </div>
        <div className="linha">
          <input required placeholder="UID (Firebase ou dev-xxx)" value={form.id} onChange={(e) => setForm({ ...form, id: e.target.value })} />
          <select value={form.perfil} onChange={(e) => setForm({ ...form, perfil: e.target.value as Perfil })}>
            {PERFIS.map((p) => <option key={p}>{p}</option>)}
          </select>
          <button className="btn">Criar</button>
        </div>
        <small className="muted">Em desenvolvimento, o UID é o valor enviado em X-Dev-Uid. Com o Firebase, será o UID do usuário criado lá.</small>
      </form>
      {msg && <p className="erro">{msg}</p>}
      {loading && <p className="muted">Carregando…</p>}
      {error && <p className="erro">{error}</p>}
      {data && (
        <table className="tabela">
          <thead>
            <tr><th>Nome</th><th>E-mail</th><th>Perfil</th><th>Ativo</th></tr>
          </thead>
          <tbody>
            {data.map((u) => (
              <tr key={u.id} className={u.ativo ? '' : 'inativo'}>
                <td>{u.nome}</td>
                <td>{u.email}</td>
                <td>
                  {u.perfil === 'ROOT' ? (
                    <strong>ROOT</strong>
                  ) : (
                    <select value={u.perfil} onChange={(e) => patch(u.id, 'perfil', { perfil: e.target.value })}>
                      {PERFIS.map((p) => <option key={p}>{p}</option>)}
                    </select>
                  )}
                </td>
                <td>
                  <input
                    type="checkbox"
                    checked={u.ativo}
                    disabled={u.perfil === 'ROOT'}
                    onChange={(e) => patch(u.id, 'ativo', { ativo: e.target.checked })}
                    aria-label="Ativo"
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}
