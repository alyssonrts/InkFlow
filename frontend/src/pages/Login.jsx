import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import Marca from '../components/Marca.jsx'
import { botaoPrimario, botaoSecundario } from '../components/estilos.js'
import { rotaInicial, useAuth } from '../contexts/AuthContext.jsx'
import { mensagemDeErro } from '../services/api.js'

export default function Login() {
  const { usuario, login } = useAuth()
  const navigate = useNavigate()
  const local = useLocation()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  if (usuario) {
    return <Navigate to={rotaInicial(usuario.perfil)} replace />
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      const logado = await login(email.trim(), senha)
      navigate(local.state?.de ?? rotaInicial(logado.perfil), { replace: true })
    } catch (falha) {
      setErro(mensagemDeErro(falha))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b border-neutral-800 bg-neutral-900 px-6 py-3">
        <Marca />
      </header>
      <main className="flex flex-1 items-center justify-center p-6">
        <form
          onSubmit={enviar}
          className="w-full max-w-md space-y-4 rounded-lg border border-neutral-800 bg-neutral-900 p-8"
        >
          <div className="text-center">
            <h1 className="text-2xl font-bold">Entrar no InkFlow</h1>
            <p className="text-neutral-400">Acesse com seu e-mail e senha</p>
          </div>

          <Alerta>{erro}</Alerta>

          <Campo
            rotulo="E-mail"
            type="email"
            required
            autoComplete="email"
            placeholder="nome@email.com"
            value={email}
            onChange={(evento) => setEmail(evento.target.value)}
          />
          <Campo
            rotulo="Senha"
            type="password"
            required
            autoComplete="current-password"
            value={senha}
            onChange={(evento) => setSenha(evento.target.value)}
          />

          <div className="text-right">
            <Link to="/esqueci-senha" className="text-sm font-semibold text-orange-400 hover:underline">
              Esqueci minha senha
            </Link>
          </div>

          <button type="submit" disabled={enviando} className={`w-full ${botaoPrimario}`}>
            {enviando ? 'Entrando...' : 'Entrar'}
          </button>

          <div className="border-t border-neutral-800 pt-4 text-center">
            <p className="mb-2 text-neutral-400">Ainda não tem conta?</p>
            <Link to="/cadastro" className={`block ${botaoSecundario}`}>
              Criar conta de cliente
            </Link>
          </div>
        </form>
      </main>
    </div>
  )
}
