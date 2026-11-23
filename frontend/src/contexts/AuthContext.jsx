import { createContext, useContext, useMemo, useState } from 'react'
import { cadastrarCliente, entrar } from '../services/authService.js'
import { lerUsuario, limparSessao, salvarSessao } from '../services/sessao.js'

const AuthContext = createContext(null)

// Rota inicial de cada perfil (RF-02: cada perfil vê somente as suas telas).
export function rotaInicial(perfil) {
  if (perfil === 'ADMIN') return '/admin'
  if (perfil === 'TATUADOR') return '/tatuador'
  return '/cliente'
}

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => lerUsuario())

  const valor = useMemo(() => {
    function iniciarSessao(resposta) {
      salvarSessao(resposta.token, resposta.usuario, resposta.expiraEmSegundos)
      setUsuario(resposta.usuario)
      return resposta.usuario
    }

    return {
      usuario,
      async login(email, senha) {
        return iniciarSessao(await entrar(email, senha))
      },
      async cadastrar(dados) {
        return iniciarSessao(await cadastrarCliente(dados))
      },
      logout() {
        limparSessao()
        setUsuario(null)
      },
    }
  }, [usuario])

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const contexto = useContext(AuthContext)
  if (!contexto) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider')
  }
  return contexto
}
