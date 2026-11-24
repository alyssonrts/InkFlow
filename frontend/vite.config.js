import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Encaminha as requisições da API e das imagens ao backend.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080',
    },
  },
})