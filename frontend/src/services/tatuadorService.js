import api from './api.js'

export async function listarTatuadores() {
  const resposta = await api.get('/tatuadores')
  return resposta.data.data
}

export async function listarSalas() {
  const resposta = await api.get('/salas')
  return resposta.data.data
}

export async function criarTatuador(dados) {
  const resposta = await api.post('/tatuadores', dados)
  return resposta.data
}

export async function atualizarTatuador(id, dados) {
  const resposta = await api.put(`/tatuadores/${id}`, dados)
  return resposta.data
}

export async function alterarSituacaoDoTatuador(id, ativo) {
  const resposta = await api.patch(`/tatuadores/${id}/situacao`, { ativo })
  return resposta.data
}
