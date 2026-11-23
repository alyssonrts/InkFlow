// Mensagem de erro ou de sucesso. Não renderiza nada quando não há texto.
export default function Alerta({ tipo = 'erro', children }) {
  if (!children) {
    return null
  }
  const cores =
    tipo === 'sucesso'
      ? 'border-green-700 bg-green-950 text-green-200'
      : 'border-red-700 bg-red-950 text-red-200'
  return (
    <div role="alert" className={`rounded-md border px-3 py-2 text-sm ${cores}`}>
      {children}
    </div>
  )
}
