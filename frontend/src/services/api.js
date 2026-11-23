import axios from 'axios'
import { lerToken, limparSessao } from './sessao.js'

// Cliente HTTP único para a API REST do backend (prefixo /api/v1).
const api = axios.create({
  baseURL: '/api/v1',
})

api.interceptors.request.use((configuracao) => {
  const token = lerToken()
  if (token) {
    configuracao.headers.Authorization = `Bearer ${token}`
  }
  return configuracao
})

// Token vencido ou inválido: encerra a sessão e volta para o login.
api.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const url = erro.config?.url ?? ''
    if (erro.response?.status === 401 && !url.startsWith('/auth/')) {
      limparSessao()
      window.location.assign('/login')
    }
    return Promise.reject(erro)
  },
)

// Todas as respostas seguem o envelope { data, message, error }.
export function mensagemDeErro(erro) {
  return erro?.response?.data?.message ?? 'Não foi possível conectar ao servidor. Tente novamente.'
}

// Erros de validação por campo: { campo: 'mensagem' }.
export function camposDeErro(erro) {
  return erro?.response?.data?.data?.campos ?? {}
}

export default api
