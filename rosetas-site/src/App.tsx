import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import Acompanhar from './pages/Acompanhar';
import Carrinho from './pages/Carrinho';
import Catalogo from './pages/Catalogo';
import Landing from './pages/Landing';
import PedidoPublico from './pages/PedidoPublico';
import Personalizar from './pages/Personalizar';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Landing />} />
        <Route path="catalogo" element={<Catalogo />} />
        <Route path="catalogo/:id" element={<Personalizar />} />
        <Route path="carrinho" element={<Carrinho />} />
        <Route path="acompanhar" element={<Acompanhar />} />
        <Route path="pedido/:codigo" element={<PedidoPublico />} />
        <Route path="*" element={<p>Página não encontrada.</p>} />
      </Route>
    </Routes>
  );
}
