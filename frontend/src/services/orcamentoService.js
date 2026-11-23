
import api from './api.js'


export async function listarPedidos() {
  const r = await api.get('/orcamentos')
  return r.data.data
}


export async function solicitarOrcamento(dados) {
  const r = await api.post('/orcamentos', dados)
  return r.data.data
}


export async function emitirOrcamento(id, dados) {
  const r = await api.post(`/orcamentos/${id}/emitir`, dados)
  return r.data.data
}


export async function registrarDesenho(id, desenhoFinalUrl) {
  const resposta = await api.post(
    `/orcamentos/${id}/desenho`,
    { desenhoFinalUrl }
  )

  return resposta.data.data
}



export async function decidirOrcamento(id, aprovarValor, aprovarDesenho) {
  const r = await api.post(`/orcamentos/${id}/decidir`, {
    aprovarValor,
    aprovarDesenho
  }) 

  return r.data.data
}
