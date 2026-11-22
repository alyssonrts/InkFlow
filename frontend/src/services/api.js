import axios from 'axios'

// Cliente HTTP único para a API REST do backend (prefixo /api/v1).
const api = axios.create({
  baseURL: '/api/v1',
})

export default api
