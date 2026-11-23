
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'
import { mensagemDeErro } from '../services/api.js'
import {
  listarPedidos,
  decidirOrcamento
} from '../services/orcamentoService.js'

export default function PainelCliente() {
  const { usuario } = useAuth()

  const [pedidos, setPedidos] = useState([])
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [carregando, setCarregando] = useState(true)
  const [salvandoId, setSalvandoId] = useState(null)
  const [decisoes, setDecisoes] = useState({})

  useEffect(() => {
    listarPedidos()
      .then(setPedidos)
      .catch(e => setErro(mensagemDeErro(e)))
      .finally(() => setCarregando(false))
  }, [])

  function alterarDecisao(id, campo, valor) {
    setDecisoes(anteriores => ({
      ...anteriores,
      [id]: {
        ...anteriores[id],
        [campo]: valor
      }
    }))
  }

  async function enviarDecisao(pedido) {
    const decisao = decisoes[pedido.id]

    if (
      typeof decisao?.valor !== 'boolean' ||
      typeof decisao?.desenho !== 'boolean'
    ) {
      setErro('Escolha uma opção para o valor e para o desenho.')
      return
    }

    const confirmar = window.confirm(
      'Deseja confirmar as duas decisões? ' +
      'Depois de enviadas, elas não poderão ser alteradas.'
    )

    if (!confirmar) return

    setErro('')
    setSucesso('')
    setSalvandoId(pedido.id)

    try {
      const atualizado = await decidirOrcamento(
        pedido.id,
        decisao.valor,
        decisao.desenho
      )

      setPedidos(anteriores =>
        anteriores.map(p =>
          p.id === pedido.id ? atualizado : p
        )
      )

      setSucesso('Decisão registrada com sucesso!')
    } catch (e) {
      setErro(mensagemDeErro(e))
    } finally {
      setSalvandoId(null)
    }
  }

  function situacao(valor) {
    if (valor === true) return 'Aprovado'
    if (valor === false) return 'Recusado'
    return 'Pendente'
  }

  function formatarData(data) {
    return data ? new Date(data).toLocaleString('pt-BR') : '—'
  }

  return (
    <section className="space-y-5">
      <h1 className="text-3xl font-bold">
        Meus projetos
      </h1>

      <p className="text-neutral-400">
        Olá, {usuario?.nome}. Acompanhe seus pedidos de orçamento.
      </p>

      <Link
        className="inline-block rounded bg-orange-500 px-4 py-3 font-semibold text-neutral-950"
        to="/cliente/pedir-orcamento"
      >
        Pedir orçamento
      </Link>

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
          Você ainda não possui pedidos.
        </p>
      )}

      {pedidos.map(p => {
        
        const podeDecidir =
        p.status === 'DESENHO_EM_APROVACAO' &&
        p.valorTotal != null &&
        p.valorAprovado == null &&
        p.desenhoAprovado == null &&
        Boolean(p.desenhoFinalUrl)


        const decisao = decisoes[p.id] || {}

        return (
          <article
            key={p.id}
            className="space-y-4 rounded-lg border border-neutral-800 bg-neutral-900 p-5"
          >
            <div>
              <h2 className="text-lg font-bold">
                Pedido #{p.id} — {p.tatuadorNome}
              </h2>

              <p className="mt-2">{p.descricao}</p>

              <p className="mt-2 text-sm text-orange-400">
          {({
          ORCAMENTO_SOLICITADO: 'ORÇAMENTO SOLICITADO',
          DESENHO_EM_APROVACAO: 'DESENHO EM APROVAÇÃO',
          APROVADO: 'APROVADO',
          RECUSADO: 'RECUSADO'
          }[p.status] || p.status.replaceAll('_', ' '))}
              </p>
            </div>

            {p.valorTotal != null && (
              <div className="rounded bg-neutral-800 p-4">
                <h3 className="mb-2 font-semibold">
                  Orçamento do tatuador
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
            )}

            {p.imagemReferenciaUrl && (
              <div>
                <h3 className="mb-2 font-semibold">
                  Imagem de referência
                </h3>

                <a
                  href={p.imagemReferenciaUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-orange-400 underline"
                >
                  Visualizar referência
                </a>
              </div>
            )} 
            
            {p.desenhoFinalUrl && (
              <div className="mt-4 rounded-lg border border-orange-500/30 bg-neutral-900 p-4">
                <h3 className="mb-2 font-semibold text-orange-400">
                  Desenho final apresentado
                </h3>

                <p className="mb-3 text-sm text-neutral-400"

                ></p>

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

            {p.valorTotal != null && (
              <div className="space-y-2 rounded border border-neutral-700 p-4">
                <h3 className="font-semibold">
                  Situação das aprovações
                </h3>

                <p>
                  Valor: <strong>{situacao(p.valorAprovado)}</strong>
                </p>

                <p>
                  Desenho: <strong>{situacao(p.desenhoAprovado)}</strong>
                </p>

                {p.dataDecisaoValor && (
                  <p className="text-sm text-neutral-400">
                    Decisão do valor: {formatarData(p.dataDecisaoValor)}
                  </p>
                )}

                {p.dataDecisaoDesenho && (
                  <p className="text-sm text-neutral-400">
                    Decisão do desenho: {formatarData(p.dataDecisaoDesenho)}
                  </p>
                )}
              </div>
            )}

            {podeDecidir && (
              <div className="space-y-4 rounded border border-orange-500/40 p-4">
                <h3 className="text-lg font-bold">
                  Aprovar ou recusar
                </h3>

                <p className="text-sm text-neutral-400">
                  Avalie o orçamento e o desenho apresentado
                  pelo tatuador antes de confirmar.
                </p>

                <fieldset className="space-y-2">
                  <legend className="font-semibold">
                    1. Você aprova o valor?
                  </legend>

                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name={`valor-${p.id}`}
                      checked={decisao.valor === true}
                      onChange={() =>
                        alterarDecisao(p.id, 'valor', true)
                      }
                    />
                    Aprovar valor
                  </label>

                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name={`valor-${p.id}`}
                      checked={decisao.valor === false}
                      onChange={() =>
                        alterarDecisao(p.id, 'valor', false)
                      }
                    />
                    Recusar valor
                  </label>
                </fieldset>

                <fieldset className="space-y-2">
                  <legend className="font-semibold">
                    2. Você aprova o desenho?
                  </legend>

                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name={`desenho-${p.id}`}
                      checked={decisao.desenho === true}
                      onChange={() =>
                        alterarDecisao(p.id, 'desenho', true)
                      }
                    />
                    Aprovar desenho
                  </label>

                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name={`desenho-${p.id}`}
                      checked={decisao.desenho === false}
                      onChange={() =>
                        alterarDecisao(p.id, 'desenho', false)
                      }
                    />
                    Recusar desenho
                  </label>
                </fieldset>

                <button
                  type="button"
                  disabled={salvandoId === p.id}
                  onClick={() => enviarDecisao(p)}
                  className="rounded bg-orange-500 px-4 py-3 font-semibold text-neutral-950 disabled:opacity-50"
                >
                  {salvandoId === p.id
                    ? 'Salvando...'
                    : 'Confirmar decisões'}
                </button>
              </div>
            )}

            {p.status === 'APROVADO' && (
              <p className="font-semibold text-green-400">
                Orçamento e desenho aprovados!
              </p>
            )}

            {p.status === 'RECUSADO' && (
              <p className="font-semibold text-red-400">
                O orçamento ou o desenho foi recusado.
              </p>
            )}
          </article>
        )
      })}
    </section>
  )
}
