// Guarda no navegador o token e os dados do usuário logado.
const CHAVE_TOKEN = 'inkflow.token'
const CHAVE_USUARIO = 'inkflow.usuario'
const CHAVE_EXPIRACAO = 'inkflow.expiraEm'

export function salvarSessao(token, usuario, expiraEmSegundos) {
  localStorage.setItem(CHAVE_TOKEN, token)
  localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario))
  localStorage.setItem(CHAVE_EXPIRACAO, String(Date.now() + expiraEmSegundos * 1000))
}

export function limparSessao() {
  localStorage.removeItem(CHAVE_TOKEN)
  localStorage.removeItem(CHAVE_USUARIO)
  localStorage.removeItem(CHAVE_EXPIRACAO)
}

export function lerToken() {
  return localStorage.getItem(CHAVE_TOKEN)
}

// Devolve o usuário salvo, ou null se não houver sessão ou se ela já expirou.
export function lerUsuario() {
  const expiraEm = Number(localStorage.getItem(CHAVE_EXPIRACAO))
  const usuario = localStorage.getItem(CHAVE_USUARIO)
  if (!usuario || !expiraEm || Date.now() >= expiraEm) {
    limparSessao()
    return null
  }
  try {
    return JSON.parse(usuario)
  } catch {
    limparSessao()
    return null
  }
}
