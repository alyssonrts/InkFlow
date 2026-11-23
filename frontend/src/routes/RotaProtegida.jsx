import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { rotaInicial, useAuth } from '../contexts/AuthContext.jsx'

// Exige login e, quando informado, um dos perfis permitidos.
export default function RotaProtegida({ perfis }) {
  const { usuario } = useAuth()
  const local = useLocation()

  if (!usuario) {
    return <Navigate to="/login" replace state={{ de: local.pathname }} />
  }
  if (perfis && !perfis.includes(usuario.perfil)) {
    return <Navigate to={rotaInicial(usuario.perfil)} replace />
  }
  return <Outlet />
}
