import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'
import Marca from './Marca.jsx'

// Menu de cada perfil, conforme os wireframes: cada usuário vê somente as suas telas.
const MENUS = {
  CLIENTE: {
    titulo: 'Cliente',
    itens: [
      { rotulo: 'Meus projetos', rota: '/cliente', exato: true },
      { rotulo: 'Pedir orçamento', rota: '/cliente/pedir-orcamento' },
      { rotulo: 'Meus dados', rota: '/cliente/meus-dados' },
    ],
  },
  TATUADOR: {
    titulo: 'Tatuador',
    itens: [
      { rotulo: 'Agenda do dia', rota: '/tatuador', exato: true },
      { rotulo: 'Pedidos de orçamento', rota: '/tatuador/pedidos-de-orcamento' },
      { rotulo: 'Meus projetos', rota: '/tatuador/meus-projetos' },
      { rotulo: 'Portfólio', rota: '/tatuador/portfolio' },
    ],
  },
  ADMIN: {
    titulo: 'Administrador',
    itens: [
      { rotulo: 'Painel gerencial', rota: '/admin', exato: true },
      { rotulo: 'Tatuadores', rota: '/admin/tatuadores' },
      { rotulo: 'Salas', rota: '/admin/salas' },
      { rotulo: 'Materiais e lotes', rota: '/admin/materiais-e-lotes' },
      { rotulo: 'Parâmetros', rota: '/admin/parametros' },
      { rotulo: 'Relatórios', rota: '/admin/relatorios' },
    ],
  },
}

function iniciais(nome) {
  return nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((parte) => parte[0])
    .join('')
    .toUpperCase()
}

export default function Layout() {
  const { usuario, logout } = useAuth()
  const navigate = useNavigate()
  const menu = MENUS[usuario.perfil]

  function sair() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen flex-col">
      <header className="flex items-center justify-between border-b border-neutral-800 bg-neutral-900 px-6 py-3">
        <Marca />
        <div className="flex items-center gap-3">
          <span className="font-semibold">{usuario.nome}</span>
          <span className="flex h-9 w-9 items-center justify-center rounded-full border border-neutral-600 text-sm font-bold">
            {iniciais(usuario.nome)}
          </span>
        </div>
      </header>

      <div className="flex flex-1 flex-col md:flex-row">
        <aside className="border-b border-neutral-800 bg-neutral-900 p-4 md:w-64 md:border-b-0 md:border-r">
          <p className="mb-2 px-3 text-xs font-bold uppercase tracking-wide text-neutral-500">{menu.titulo}</p>
          <nav className="flex flex-wrap gap-1 md:flex-col">
            {menu.itens.map((item) => (
              <NavLink
                key={item.rota}
                to={item.rota}
                end={item.exato}
                className={({ isActive }) =>
                  `rounded-md px-3 py-2 ${
                    isActive ? 'bg-orange-500 font-semibold text-neutral-950' : 'text-neutral-200 hover:bg-neutral-800'
                  }`
                }
              >
                {item.rotulo}
              </NavLink>
            ))}
          </nav>
          <button type="button" onClick={sair} className="mt-6 px-3 py-2 text-neutral-400 hover:text-neutral-100">
            Sair
          </button>
        </aside>

        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
