
import { useEffect, useState } from 'react'
import { mensagemDeErro } from '../services/api.js'
import {
  listarPedidos,
  emitirOrcamento,
  registrarDesenho
} from '../services/orcamentoService.js'

export default function PedidosTatuador() {
  const [pedidos, setPedidos] = useState([])
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [ocupado, setOcupado] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [valores, setValores] = useState({})


async function enviarDesenho(id, url) {
  try {
    if (!url || !url.trim()) {
      alert('Informe o link do desenho final.')
      return
    }

    await registrarDesenho(id, url.trim())

    alert('Desenho final registrado com sucesso!')

    const pedidosAtualizados = await listarPedidos()
    setPedidos(pedidosAtualizados)

  } catch (erro) {
    alert(
      erro.response?.data?.message ||
      erro.response?.data?.mensagem ||
      'Não foi possível registrar o desenho.'
    )
  }
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

{p.status === 'DESENHO_EM_APROVACAO' &&
  !p.desenhoFinalUrl && (
    <form
      onSubmit={(e) => {
        e.preventDefault()

        const url = e.currentTarget.elements.desenhoFinalUrl.value
        enviarDesenho(p.id, url)
      }}
      className="mt-4 space-y-3 rounded-xl border border-orange-500/30 p-4"
    >
      <h3 className="font-semibold text-orange-400">
        Apresentar desenho final
      </h3>

      <input
        type="url"
        name="desenhoFinalUrl"
        placeholder="https://exemplo.com/desenho.png"
        required
        className="w-full rounded-lg border border-zinc-700 bg-zinc-900 p-3 text-white"
      />

      <button
        type="submit"
        className="rounded-lg bg-orange-500 px-4 py-2 font-semibold text-black hover:bg-orange-400"
      >
        Enviar desenho ao cliente
      </button>
    </form>
  )}

{p.desenhoFinalUrl && (
  <div className="mt-4">
    <p className="mb-2 font-semibold text-orange-400">
      Desenho final apresentado
    </p>

    <a
      href={p.desenhoFinalUrl}
      target="_blank"
      rel="noopener noreferrer"
      className="text-orange-300 underline"
    >
      Visualizar desenho final
    </a>
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
            <a
              className="inline-block text-orange-400 underline"
              href={p.imagemReferenciaUrl}
              target="_blank"
              rel="noopener noreferrer"
            >
              Ver imagem de referência
            </a>
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
                  Aguardando a decisão do cliente.
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
