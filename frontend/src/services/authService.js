import api from './api.js'

export async function entrar(email, senha) {
  const resposta = await api.post('/auth/login', { email, senha })
  return resposta.data.data
}

export async function cadastrarCliente(dados) {
  const resposta = await api.post('/auth/registrar', dados)
  return resposta.data.data
}
