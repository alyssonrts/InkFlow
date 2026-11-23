import { useAuth } from '../contexts/AuthContext.jsx'

export default function PainelTatuador() {
  const { usuario } = useAuth()

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-3xl font-bold">Agenda do dia</h1>
        <p className="text-neutral-400">Olá, {usuario.nome}. Aqui ficarão a sua agenda e os pedidos de orçamento.</p>
      </div>
      <ul className="grid gap-3 sm:grid-cols-2">
        <li className="rounded-md border border-neutral-800 bg-neutral-900 p-4">Pedidos aguardando resposta (próxima etapa)</li>
        <li className="rounded-md border border-neutral-800 bg-neutral-900 p-4">Registro de sessão e materiais (próxima etapa)</li>
        <li className="rounded-md border border-neutral-800 bg-neutral-900 p-4">Meus projetos (próxima etapa)</li>
        <li className="rounded-md border border-neutral-800 bg-neutral-900 p-4">Portfólio (próxima etapa)</li>
      </ul>
    </div>
  )
}
