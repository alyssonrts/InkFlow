import { useEffect, useState } from 'react'
import { mensagemDeErro } from '../services/api.js'
import { carregarImagem, enviarImagem } from '../services/imagemService.js'
import {
  listarPedidos,
  emitirOrcamento,
  registrarDesenho
} from '../services/orcamentoService.js'

function LinkImagem({ url, children }) {
  const [imagem, setImagem] = useState(null)
  const [erro, setErro] = useState(false)

  useEffect(() => {
    let ativo = true
    let temporaria = null
    setImagem(null)
    setErro(false)

    carregarImagem(url)
      .then(resultado => {
        if (resultado.startsWith('blob:')) {
          temporaria = resultado
        }
        if (ativo) {
          setImagem({ origem: url, endereco: resultado })
        } else if (temporaria) {
          URL.revokeObjectURL(temporaria)
        }
      })
      .catch(() => {
        if (ativo) setErro(true)
      })

    return () => {
      ativo = false
      if (temporaria) URL.revokeObjectURL(temporaria)
    }
  }, [url])

  if (erro) {
    return <p role="alert" className="text-sm text-red-400">Não foi possível carregar a imagem.</p>
  }

  if (!imagem || imagem.origem !== url) {
    return <p className="text-sm text-neutral-400">Carregando imagem...</p>
  }

  return (
    <a
      href={imagem.endereco}
      target="_blank"
      rel="noopener noreferrer"
      className="text-orange-400 underline"
    >
      {children}
    </a>
  )
}

function FormularioDesenho({ onEnviar }) {
  const [arquivo, setArquivo] = useState(null)
  const [preview, setPreview] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!arquivo) {
      setPreview('')
      return
    }
    const url = URL.createObjectURL(arquivo)
    setPreview(url)
    return () => URL.revokeObjectURL(url)
  }, [arquivo])

  function selecionar(e) {
    const selecionado = e.target.files?.[0]
    e.target.value = ''
    if (!selecionado) return
    setErro('')
    if (!['image/jpeg', 'image/png'].includes(selecionado.type)) {
      setErro('Selecione uma imagem JPG ou PNG.')
      return
    }
    if (selecionado.size > 10 * 1024 * 1024 || selecionado.size === 0) {
      setErro('Selecione uma imagem válida de até 10 MB.')
      return
    }
    setArquivo(selecionado)
  }

  async function enviar(e) {
    e.preventDefault()
    if (!arquivo || enviando) return
    setErro('')
    setEnviando(true)
    try {
      await onEnviar(arquivo)
    } catch (e) {
      setErro(mensagemDeErro(e))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={enviar} className="space-y-4 rounded-xl border border-orange-500/30 bg-[#1e2028] p-4">
      <h3 className="font-semibold text-orange-400">Apresentar desenho final</h3>
      <div className="flex min-h-[250px] flex-col items-center justify-center gap-4 rounded-lg border border-dashed border-orange-500/60 bg-[#15161c] p-5">
        {arquivo ? (
          <>
            {preview && <img src={preview} alt="Pré-visualização do desenho final" className="max-h-64 max-w-full rounded-md object-contain" />}
            <p className="break-all text-sm">{arquivo.name}</p>
            <p className="text-xs text-neutral-400">{(arquivo.size / 1024 / 1024).toFixed(2)} MB</p>
          </>
        ) : (
          <>
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="text-orange-500" aria-hidden="true"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
            <p className="text-sm text-neutral-400">Selecione o desenho final para apresentar ao cliente.</p>
          </>
        )}
        <div className="flex flex-wrap justify-center gap-3">
          <label className="cursor-pointer rounded-lg bg-[#2b2e38] px-4 py-3 text-sm font-semibold focus-within:ring-2 focus-within:ring-orange-500">
            {arquivo ? 'Substituir imagem' : 'Selecionar imagem'}
            <input type="file" accept="image/jpeg,image/png" onChange={selecionar} disabled={enviando} className="sr-only" />
          </label>
          {arquivo && <button type="button" disabled={enviando} onClick={() => { setArquivo(null); setErro('') }} className="rounded-lg border border-red-500/40 px-4 py-3 text-sm text-red-400 disabled:opacity-50">Remover</button>}
        </div>
      </div>
      <p className="text-xs text-neutral-400">JPG ou PNG, até 10 MB.</p>
      {erro && <p role="alert" className="text-sm text-red-400">{erro}</p>}
      <button type="submit" disabled={!arquivo || enviando} className="rounded-lg bg-orange-500 px-4 py-3 font-semibold text-black disabled:opacity-50">
        {enviando ? 'Enviando...' : 'Enviar desenho ao cliente'}
      </button>
    </form>
  )
}

export default function PedidosTatuador() {
  const [pedidos, setPedidos] = useState([])
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [ocupado, setOcupado] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [valores, setValores] = useState({})


  async function enviarDesenho(id, arquivo) {
    setErro('')
    setSucesso('')
    const imagem = await enviarImagem(arquivo)
    const atualizado = await registrarDesenho(id, imagem.url)
    setPedidos(anteriores => anteriores.map(p =>
      p.id === id ? { ...p, ...atualizado, desenhoFinalUrl: imagem.url } : p
    ))
    setSucesso('Desenho final registrado com sucesso!')
  }

  async function carregar() {
    try {
      const dados = await listarPedidos()
      setPedidos(dados)
      setErro('')
    } catch (e) {
      setErro(mensagemDeErro(e))
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
  }, [])

  async function emitir(e, id) {
    e.preventDefault()
    setErro('')
    setSucesso('')
    setOcupado(id)

    try {
      const v = valores[id] || {}

      await emitirOrcamento(id, {
        valorTotal: Number(v.valorTotal),
        sessoesEstimadas: Number(v.sessoesEstimadas),
        duracaoSessaoMinutos: Number(v.duracaoSessaoMinutos)
      })

      await carregar()
      setSucesso('Orçamento emitido com sucesso!')
    } catch (e) {
      setErro(mensagemDeErro(e))
    } finally {
      setOcupado(null)
    }
  }

  function situacao(valor) {
    if (valor === true) return 'Aprovado'
    if (valor === false) return 'Recusado'
    return 'Pendente'
  }

  function corSituacao(valor) {
    if (valor === true) return 'text-green-400'
    if (valor === false) return 'text-red-400'
    return 'text-yellow-400'
  }

  function formatarData(data) {
    return data
      ? new Date(data).toLocaleString('pt-BR')
      : '—'
  }

  const input =
    'w-full rounded border border-neutral-700 bg-neutral-950 p-2'

  return (
    <section className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-3xl font-bold">
            Pedidos de orçamento
          </h1>

          <p className="mt-2 text-neutral-400">
            Gerencie os orçamentos e acompanhe as decisões dos clientes.
          </p>
        </div>

        <button
          type="button"
          onClick={carregar}
          className="rounded border border-neutral-700 px-4 py-2 hover:bg-neutral-800"
        >
          Atualizar pedidos
        </button>
      </div>

      {erro && (
        <p role="alert" className="text-red-400">
          {erro}
        </p>
      )}

      {sucesso && (
        <p role="status" className="text-green-400">
          {sucesso}
        </p>
      )}

      {carregando && (
        <p className="text-neutral-400">
          Carregando pedidos...
        </p>
      )}

      {!carregando && pedidos.length === 0 && (
        <p className="text-neutral-400">
          Nenhum pedido recebido.
        </p>
      )}

      {pedidos.map(p => (
        <article
          key={p.id}
          className="space-y-4 rounded-lg border border-neutral-800 bg-neutral-900 p-5"
        >

          {p.status === 'DESENHO_EM_APROVACAO' && !p.desenhoFinalUrl && (
            <FormularioDesenho onEnviar={arquivo => enviarDesenho(p.id, arquivo)} />
          )}

{p.desenhoFinalUrl && (
  <div className="mt-4">
    <p className="mb-2 font-semibold text-orange-400">
      Desenho final apresentado
    </p>

    <LinkImagem url={p.desenhoFinalUrl}>
              Visualizar desenho final
            </LinkImagem>
  </div>
)}

          <div className="flex flex-wrap justify-between gap-2">
            <h2 className="text-lg font-semibold">
              Pedido #{p.id} — {p.clienteNome}
            </h2>

            <span className="text-orange-400">
              
          {({
            ORCAMENTO_SOLICITADO: 'ORÇAMENTO SOLICITADO',
            DESENHO_EM_APROVACAO: 'DESENHO EM APROVAÇÃO',
            APROVADO: 'APROVADO',
            RECUSADO: 'RECUSADO'
          }[p.status] || p.status.replaceAll('_', ' '))}

            </span>
          </div>

          <div>
            <p>{p.descricao}</p>

            <p className="mt-2 text-sm text-neutral-400">
              {p.regiaoCorpo} · {p.tamanhoAproximado}
            </p>
          </div>

          {p.imagemReferenciaUrl && (
            <LinkImagem url={p.imagemReferenciaUrl}>
              Ver imagem de referência
            </LinkImagem>
          )}

          {p.valorTotal != null ? (
            <div className="space-y-3">
              <div className="rounded bg-neutral-800 p-4">
                <h3 className="mb-2 font-semibold">
                  Orçamento emitido
                </h3>

                <p className="text-xl font-bold text-orange-400">
                  {Number(p.valorTotal).toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                  })}
                </p>

                <p className="text-sm text-neutral-300">
                  {p.sessoesEstimadas} sessões estimadas
                </p>

                <p className="text-sm text-neutral-300">
                  {p.duracaoSessaoMinutos ?? 'Não informada'} minutos por sessão
                </p>
              </div>

              <div className="space-y-3 rounded border border-neutral-700 p-4">
                <h3 className="font-semibold">
                  Decisões do cliente
                </h3>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <p className="text-sm text-neutral-400">
                      Aprovação do valor
                    </p>

                    <p className={`font-semibold ${corSituacao(p.valorAprovado)}`}>
                      {situacao(p.valorAprovado)}
                    </p>

                    <p className="text-xs text-neutral-400">
                      {formatarData(p.dataDecisaoValor)}
                    </p>
                  </div>

                  <div>
                    <p className="text-sm text-neutral-400">
                      Aprovação do desenho
                    </p>

                    <p className={`font-semibold ${corSituacao(p.desenhoAprovado)}`}>
                      {situacao(p.desenhoAprovado)}
                    </p>

                    <p className="text-xs text-neutral-400">
                      {formatarData(p.dataDecisaoDesenho)}
                    </p>
                  </div>
                </div>
              </div>

              {p.status === 'APROVADO' && (
                <p className="font-semibold text-green-400">
                  Orçamento e desenho aprovados pelo cliente!
                </p>
              )}

              {p.status === 'RECUSADO' && (
                <p className="font-semibold text-red-400">
                  O cliente recusou o orçamento ou o desenho.
                </p>
              )}

              {p.status === 'DESENHO_EM_APROVACAO' && (
                <p className="text-sm text-yellow-400">
                  {p.desenhoFinalUrl ? 'Aguardando a decisão do cliente.' : 'Aguardando o envio do desenho final.'}
                </p>
              )}
            </div>
          ) : p.status === 'ORCAMENTO_SOLICITADO' ? (
            <form
              onSubmit={e => emitir(e, p.id)}
              className="grid gap-3 sm:grid-cols-4"
            >
              <label>
                Valor (R$)
                <input
                  className={input}
                  required
                  type="number"
                  min="0.01"
                  step="0.01"
                  onChange={e =>
                    setValores(v => ({
                      ...v,
                      [p.id]: {
                        ...v[p.id],
                        valorTotal: e.target.value
                      }
                    }))
                  }
                />
              </label>

              <label>
                Sessões
                <input
                  className={input}
                  required
                  type="number"
                  min="1"
                  step="1"
                  onChange={e =>
                    setValores(v => ({
                      ...v,
                      [p.id]: {
                        ...v[p.id],
                        sessoesEstimadas: e.target.value
                      }
                    }))
                  }
                />
              </label>

              <label>
                Minutos/sessão
                <input
                  className={input}
                  required
                  type="number"
                  min="1"
                  step="1"
                  onChange={e =>
                    setValores(v => ({
                      ...v,
                      [p.id]: {
                        ...v[p.id],
                        duracaoSessaoMinutos: e.target.value
                      }
                    }))
                  }
                />
              </label>

              <button
                type="submit"
                disabled={ocupado === p.id}
                className="self-end rounded bg-orange-500 p-2 font-semibold text-neutral-950 disabled:opacity-50"
              >
                {ocupado === p.id
                  ? 'Emitindo...'
                  : 'Emitir orçamento'}
              </button>
            </form>
          ) : null}
        </article>
      ))}
    </section>
  )
}
