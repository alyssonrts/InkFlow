import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from '../components/Layout.jsx'
import { rotaInicial, useAuth } from '../contexts/AuthContext.jsx'
import CriarConta from '../pages/CriarConta.jsx'
import EmConstrucao from '../pages/EmConstrucao.jsx'
import Login from '../pages/Login.jsx'
import PedirOrcamento from '../pages/PedirOrcamento.jsx'
import PedidosTatuador from '../pages/PedidosTatuador.jsx'
import PainelAdmin from '../pages/PainelAdmin.jsx'
import PainelCliente from '../pages/PainelCliente.jsx'
import PainelTatuador from '../pages/PainelTatuador.jsx'
import Tatuadores from '../pages/Tatuadores.jsx'
import RotaProtegida from './RotaProtegida.jsx'

function RedirecionarPorPerfil() {
  const { usuario } = useAuth()
  return <Navigate to={usuario ? rotaInicial(usuario.perfil) : '/login'} replace />
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<RedirecionarPorPerfil />} />
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<CriarConta />} />
      <Route
        path="/esqueci-senha"
        element={<EmConstrucao titulo="Redefinição de senha" soltaDoPainel />}
      />

      <Route element={<RotaProtegida perfis={['CLIENTE']} />}>
        <Route element={<Layout />}>
          <Route path="/cliente" element={<PainelCliente />} />
          <Route path="/cliente/pedir-orcamento" element={<PedirOrcamento />} />
          <Route path="/cliente/:secao" element={<EmConstrucao />} />
        </Route>
      </Route>

      <Route element={<RotaProtegida perfis={['TATUADOR']} />}>
        <Route element={<Layout />}>
          <Route path="/tatuador" element={<PainelTatuador />} />
          <Route path="/tatuador/pedidos-de-orcamento" element={<PedidosTatuador />} />
          <Route path="/tatuador/:secao" element={<EmConstrucao />} />
        </Route>
      </Route>

      <Route element={<RotaProtegida perfis={['ADMIN']} />}>
        <Route element={<Layout />}>
          <Route path="/admin" element={<PainelAdmin />} />
          <Route path="/admin/tatuadores" element={<Tatuadores />} />
          <Route path="/admin/:secao" element={<EmConstrucao />} />
        </Route>
      </Route>

      <Route path="*" element={<RedirecionarPorPerfil />} />
    </Routes>
  )
}
