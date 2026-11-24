import api from './api.js'

export async function enviarImagem(arquivo) {
  const formData = new FormData()

  formData.append('arquivo', arquivo)

  const resposta = await api.post(
    '/imagens/upload',
    formData
  )

  return resposta.data.data
}

// Carrega uma imagem armazenada no servidor
// utilizando a autenticação JWT.
export async function carregarImagem(url) {
  if (!url) {
    throw new Error('Imagem não informada.')
  }

  // Mantém a compatibilidade com links externos.
  if (
    url.startsWith('https://') ||
    url.startsWith('http://')
  ) {
    return url
  }

  // Permite somente caminhos de imagens armazenadas.
  if (!/^\/uploads\/[a-f0-9-]{36}\.(jpg|png)$/.test(url)) {
    throw new Error('Caminho da imagem inválido.')
  }

  const resposta = await api.get(url, {
    baseURL: '/',
    responseType: 'blob'
  })

  return URL.createObjectURL(resposta.data)
}