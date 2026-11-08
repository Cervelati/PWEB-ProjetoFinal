import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';

/** Para quem perdeu a aba: cola o link (ou só o código) recebido ao fazer o pedido. */
export default function Acompanhar() {
  const [texto, setTexto] = useState('');
  const nav = useNavigate();

  function abrir(e: FormEvent) {
    e.preventDefault();
    const codigo = texto.trim().split('/').filter(Boolean).pop();
    if (codigo) nav(`/pedido/${codigo}`);
  }

  return (
    <div className="estreito">
      <h2>Acompanhar pedido</h2>
      <p className="muted">Cole o link ou o código que você recebeu ao finalizar o pedido.</p>
      <form className="linha" onSubmit={abrir}>
        <input required className="cresce" placeholder="https://…/pedido/código" value={texto} onChange={(e) => setTexto(e.target.value)} />
        <button className="btn">Abrir</button>
      </form>
    </div>
  );
}
