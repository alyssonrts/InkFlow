import { Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

export default function PainelAdmin() {
  const { usuario } = useAuth()

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-3xl font-bold">Painel gerencial</h1>
        <p className="text-neutral-400">Olá, {usuario.nome}. Os indicadores e o estoque serão exibidos em uma próxima etapa.</p>
      </div>
      <ul className="grid gap-3 sm:grid-cols-2">
        <li className="rounded-md border border-orange-500 bg-neutral-900 p-4">
          <Link to="/admin/tatuadores" className="font-semibold text-orange-400 hover:underline">
            Cadastro de tatuadores
          </Link>
          <p className="text-sm text-neutral-400">Cadastre os profissionais e vincule cada um à sua sala exclusiva.</p>
        </li>
        <li className="rounded-md border border-neutral-800 bg-neutral-900 p-4">Salas, materiais e parâmetros (próxima etapa)</li>
      </ul>
    </div>
  )
}
