import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DEV_USERS, useAuth } from '../auth';
import { msgErro } from '../api';
import { firebaseAtivo } from '../firebase';

function mensagem(e: unknown): string {
  const code = (e as { code?: string })?.code ?? '';
  if (code.includes('invalid-credential') || code.includes('wrong-password') || code.includes('user-not-found'))
    return 'E-mail ou senha incorretos.';
  if (code.includes('too-many-requests')) return 'Muitas tentativas. Aguarde alguns minutos e tente de novo.';
  return msgErro(e);
}

export default function Login() {
  const { login, loginEmail } = useAuth();
  const nav = useNavigate();
  const [erro, setErro] = useState<string | null>(null);
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [enviando, setEnviando] = useState(false);

  const destino = (perfil: string) => (perfil === 'FUNCIONARIA' ? '/producao' : '/admin/pedidos');

  async function entrarDev(uid: string) {
    try {
      const u = await login(uid);
      nav(destino(u.perfil));
    } catch (e) {
      setErro(msgErro(e) + ' — o backend está rodando com SPRING_PROFILES_ACTIVE=dev?');
    }
  }

  async function entrar(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      const u = await loginEmail(email.trim(), senha);
      nav(destino(u.perfil));
    } catch (err) {
      setErro(mensagem(err));
    } finally {
      setEnviando(false);
    }
  }

  if (firebaseAtivo) {
    return (
      <form className="estreito painel" onSubmit={entrar}>
        <h2>Entrar</h2>
        <p className="muted">Área restrita à equipe.</p>
        {erro && <p className="erro">{erro}</p>}
        <div className="campo">
          <label htmlFor="email">E-mail</label>
          <input id="email" type="email" autoComplete="username" required value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="campo">
          <label htmlFor="senha">Senha</label>
          <input id="senha" type="password" autoComplete="current-password" required value={senha} onChange={(e) => setSenha(e.target.value)} />
        </div>
        <button className="btn" disabled={enviando}>{enviando ? 'Entrando…' : 'Entrar'}</button>
      </form>
    );
  }

  return (
    <div className="estreito">
      <h2>Entrar (modo desenvolvimento)</h2>
      <p className="muted">Área restrita à equipe. Escolha o perfil para testar; enviamos o header X-Dev-Uid ao backend.</p>
      {erro && <p className="erro">{erro}</p>}
      <div className="grade">
        {DEV_USERS.map((u) => (
          <button key={u.uid} className="cartao clicavel" onClick={() => entrarDev(u.uid)}>
            <strong>{u.label}</strong>
            <span className="muted">{u.descricao}</span>
            <small className="muted">{u.uid}</small>
          </button>
        ))}
      </div>
    </div>
  );
}
