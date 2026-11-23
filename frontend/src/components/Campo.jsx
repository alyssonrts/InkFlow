import { estiloCampo } from './estilos.js'

// Campo de formulário com rótulo e mensagem de erro. Sem filhos, renderiza um <input>.
export default function Campo({ rotulo, erro, children, ...propriedades }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-neutral-300">{rotulo}</span>
      {children ?? <input className={estiloCampo} {...propriedades} />}
      {erro && <span className="mt-1 block text-sm text-red-400">{erro}</span>}
    </label>
  )
}
