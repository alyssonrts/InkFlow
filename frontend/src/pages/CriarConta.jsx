import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import Marca from '../components/Marca.jsx'
import { botaoPrimario } from '../components/estilos.js'
import { rotaInicial, useAuth } from '../contexts/AuthContext.jsx'
import { camposDeErro, mensagemDeErro } from '../services/api.js'

const FORMULARIO_VAZIO = {
  nome: '',
  email: '',
  telefone: '',
  cpf: '',
  dataNascimento: '',
  senha: '',
  autorizaPortfolio: false,
}

export default function CriarConta() {
  const { usuario, cadastrar } = useAuth()
  const navigate = useNavigate()
  const [formulario, setFormulario] = useState(FORMULARIO_VAZIO)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  if (usuario) {
    return <Navigate to={rotaInicial(usuario.perfil)} replace />
  }

  function alterar(campo) {
    return (evento) => {
      const valor = evento.target.type === 'checkbox' ? evento.target.checked : evento.target.value
      setFormulario((atual) => ({ ...atual, [campo]: valor }))
    }
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setErros({})
    setEnviando(true)
    try {
      const criado = await cadastrar(formulario)
      navigate(rotaInicial(criado.perfil), { replace: true })
    } catch (falha) {
      setErro(mensagemDeErro(falha))
      setErros(camposDeErro(falha))
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
          className="w-full max-w-xl space-y-4 rounded-lg border border-neutral-800 bg-neutral-900 p-8"
        >
          <div>
            <h1 className="text-2xl font-bold">Criar conta de cliente</h1>
            <p className="text-neutral-400">Preencha seus dados para acompanhar seus projetos.</p>
          </div>

          <Alerta>{erro}</Alerta>

          <Campo rotulo="Nome completo *" required maxLength={100} value={formulario.nome}
            onChange={alterar('nome')} erro={erros.nome} />
          <Campo rotulo="E-mail *" type="email" required maxLength={150} autoComplete="email"
            placeholder="nome@email.com" value={formulario.email} onChange={alterar('email')} erro={erros.email} />

          <div className="grid gap-4 sm:grid-cols-2">
            <Campo rotulo="Telefone *" required maxLength={20} placeholder="(87) 99999-0000"
              value={formulario.telefone} onChange={alterar('telefone')} erro={erros.telefone} />
            <Campo rotulo="CPF *" required maxLength={14} placeholder="000.000.000-00"
              value={formulario.cpf} onChange={alterar('cpf')} erro={erros.cpf} />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Campo rotulo="Data de nascimento *" type="date" required value={formulario.dataNascimento}
              onChange={alterar('dataNascimento')} erro={erros.dataNascimento} />
            <Campo rotulo="Senha *" type="password" required minLength={8} maxLength={72}
              autoComplete="new-password" placeholder="Mínimo de 8 caracteres" value={formulario.senha}
              onChange={alterar('senha')} erro={erros.senha} />
          </div>

          <label className="flex items-start gap-2 text-sm text-neutral-300">
            <input type="checkbox" className="mt-1" checked={formulario.autorizaPortfolio}
              onChange={alterar('autorizaPortfolio')} />
            <span>Autorizo o uso das imagens do meu projeto no portfólio do estúdio.</span>
          </label>

          <button type="submit" disabled={enviando} className={`w-full ${botaoPrimario}`}>
            {enviando ? 'Criando conta...' : 'Criar conta'}
          </button>

          <p className="text-center text-sm text-neutral-400">
            Já tem conta?{' '}
            <Link to="/login" className="font-semibold text-orange-400 hover:underline">
              Voltar ao login
            </Link>
          </p>
        </form>
      </main>
    </div>
  )
}
