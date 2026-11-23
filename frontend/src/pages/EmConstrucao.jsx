import { Link, useParams } from 'react-router-dom'
import { botaoSecundario } from '../components/estilos.js'

// Tela provisória das funcionalidades previstas para as próximas etapas.
export default function EmConstrucao({ titulo, soltaDoPainel = false }) {
  const { secao } = useParams()
  const nome = titulo ?? (secao ? secao.replace(/-/g, ' ') : 'Esta tela')
  const nomeFormatado = nome.charAt(0).toUpperCase() + nome.slice(1)

  const conteudo = (
    <div className="max-w-xl rounded-lg border border-neutral-800 bg-neutral-900 p-8">
      <h1 className="text-2xl font-bold">{nomeFormatado}</h1>
      <p className="mt-2 text-neutral-400">Esta funcionalidade será disponibilizada em uma próxima etapa do projeto.</p>
      {soltaDoPainel && (
        <Link to="/login" className={`mt-6 inline-block ${botaoSecundario}`}>
          Voltar ao login
        </Link>
      )}
    </div>
  )

  if (soltaDoPainel) {
    return <div className="flex min-h-screen items-center justify-center p-6">{conteudo}</div>
  }
  return conteudo
}
