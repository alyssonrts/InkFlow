
import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { mensagemDeErro } from '../services/api.js'
import { solicitarOrcamento } from '../services/orcamentoService.js'
import { listarTatuadores } from '../services/tatuadorService.js'
import { enviarImagem } from '../services/imagemService.js'

export default function PedirOrcamento() {
  const navigate = useNavigate()
  const inputArquivo = useRef(null)

  const [tatuadores, setTatuadores] = useState([])
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [arquivo, setArquivo] = useState(null)
  const [preview, setPreview] = useState('')
  const [arrastando, setArrastando] = useState(false)
  const [etapaEnvio, setEtapaEnvio] = useState('')

  const [form, setForm] = useState({
    tatuadorId: '',
    descricao: '',
    regiaoCorpo: '',
    tamanhoAproximado: '',
    imagemReferenciaUrl: ''
  })

  useEffect(() => {
    listarTatuadores()
      .then(dados => {
        setTatuadores(dados.filter(t => t.ativo))
      })
      .catch(e => setErro(mensagemDeErro(e)))
  }, [])

  useEffect(() => {
    if (!arquivo) {
      setPreview('')
      return
    }

    const url = URL.createObjectURL(arquivo)

    setPreview(url)

    return () => URL.revokeObjectURL(url)
  }, [arquivo])

  function atualizar(campo, valor) {
    setForm(anterior => ({
      ...anterior,
      [campo]: valor
    }))
  }

  function selecionarImagem(imagem) {
    if (!imagem || enviando) return

    setErro('')

    const formatosPermitidos = [
      'image/jpeg',
      'image/png'
    ]

    if (!formatosPermitidos.includes(imagem.type)) {
      setErro('Selecione uma imagem JPG ou PNG.')
      return
    }

    if (imagem.size > 10 * 1024 * 1024) {
      setErro('A imagem deve ter no máximo 10 MB.')
      return
    }

    setArquivo(imagem)
  }

  function removerImagem() {
    setArquivo(null)
    setPreview('')
    setErro('')

    if (inputArquivo.current) {
      inputArquivo.current.value = ''
    }
  }

  function receberArquivo(e) {
    e.preventDefault()
    setArrastando(false)

    if (enviando) return

    const imagem = e.dataTransfer.files[0]

    selecionarImagem(imagem)
  }

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setEnviando(true)

    try {
      let imagemReferenciaUrl = ''

      if (arquivo) {
        setEtapaEnvio('Enviando imagem...')

        const imagemEnviada = await enviarImagem(arquivo)

        imagemReferenciaUrl = imagemEnviada.url
      }

      setEtapaEnvio('Enviando pedido...')

      await solicitarOrcamento({
        ...form,
        tatuadorId: Number(form.tatuadorId),
        imagemReferenciaUrl
      })

      navigate('/cliente')

    } catch (e) {
      setErro(mensagemDeErro(e))
    } finally {
      setEnviando(false)
      setEtapaEnvio('')
    }
  }

  const campo =
    'mt-2 w-full rounded-md border border-[#30323b] bg-[#15161c] px-4 py-3 text-sm text-neutral-100 outline-none focus:border-orange-500'

  const rotulo =
    'block text-sm font-medium text-neutral-300'

  return (
    <section className="mx-auto w-full max-w-6xl space-y-6">

      <div>
        <h1 className="text-3xl font-bold">
          Pedir orçamento
        </h1>

        <p className="mt-2 text-sm text-neutral-400">
          Conte sua ideia ao tatuador. Você receberá
          o valor e o número estimado de sessões.
        </p>
      </div>

      <form
        onSubmit={enviar}
        className="rounded-xl border border-[#30323b] bg-[#1e2028] p-5 md:p-7"
      >

        <div className="grid grid-cols-1 gap-7 lg:grid-cols-2">

          {/* COLUNA ESQUERDA */}

          <div className="space-y-5">

            <label className={rotulo}>
              Descreva sua ideia *

              <textarea
                className={campo}
                required
                disabled={enviando}
                maxLength={255}
                rows={5}
                placeholder="Ex.: Uma fênix com asas abertas, em cores quentes, estilo aquarela..."
                value={form.descricao}
                onChange={e =>
                  atualizar('descricao', e.target.value)
                }
              />
            </label>

            <label className={rotulo}>
              Região do corpo *

              <select
                className={campo}
                required
                disabled={enviando}
                value={form.regiaoCorpo}
                onChange={e =>
                  atualizar('regiaoCorpo', e.target.value)
                }
              >
                <option value="">Selecione a região</option>
                <option>Antebraço direito</option>
                <option>Antebraço esquerdo</option>
                <option>Braço direito</option>
                <option>Braço esquerdo</option>
                <option>Ombro</option>
                <option>Costas</option>
                <option>Costela</option>
                <option>Peito</option>
                <option>Perna direita</option>
                <option>Perna esquerda</option>
                <option>Panturrilha</option>
                <option>Coxa</option>
                <option>Pulso</option>
                <option>Outra região</option>
              </select>
            </label>

            <label className={rotulo}>
              Tamanho aproximado (cm) *

              <input
                className={campo}
                required
                disabled={enviando}
                maxLength={60}
                placeholder="Ex.: 15 x 10"
                value={form.tamanhoAproximado}
                onChange={e =>
                  atualizar('tamanhoAproximado', e.target.value)
                }
              />
            </label>

            <label className={rotulo}>
              Tatuador *

              <select
                className={campo}
                required
                disabled={enviando}
                value={form.tatuadorId}
                onChange={e =>
                  atualizar('tatuadorId', e.target.value)
                }
              >
                <option value="">
                  Selecione um tatuador
                </option>

                {tatuadores.map(t => (
                  <option key={t.id} value={t.id}>
                    {t.nome} — {t.especialidade}
                  </option>
                ))}
              </select>
            </label>

          </div>

          {/* COLUNA DIREITA */}

          <div className="space-y-4">

            <div>
              <p className={rotulo}>
                Imagem de referência
              </p>

              <input
                ref={inputArquivo}
                type="file"
                accept="image/jpeg,image/png"
                className="hidden"
                disabled={enviando}
                onChange={e => {
                  selecionarImagem(e.target.files[0])
                  e.target.value = ''
                }}
              />

              <div
                onDragOver={e => {
                  e.preventDefault()

                  if (!enviando) {
                    setArrastando(true)
                  }
                }}
                onDragLeave={() => setArrastando(false)}
                onDrop={receberArquivo}
                className={`mt-2 flex min-h-[250px] flex-col items-center justify-center gap-4 overflow-hidden rounded-lg border border-dashed p-5 transition ${
                  arrastando
                    ? 'border-orange-400 bg-orange-500/10'
                    : 'border-orange-500/60 bg-[#15161c]'
                }`}
              >

                {preview ? (
                  <>
                    <img
                      src={preview}
                      alt="Pré-visualização da imagem de referência"
                      className="max-h-64 max-w-full rounded-md object-contain"
                    />

                    <p className="max-w-full break-all text-center text-sm text-neutral-300">
                      {arquivo?.name}
                    </p>

                    <p className="text-xs text-neutral-500">
                      {(arquivo.size / 1024 / 1024).toFixed(2)} MB
                    </p>

                    <div className="flex flex-wrap justify-center gap-3">

                      <button
                        type="button"
                        disabled={enviando}
                        onClick={() =>
                          inputArquivo.current?.click()
                        }
                        className="rounded-md bg-[#2b2e38] px-4 py-2 text-sm font-medium text-neutral-200 hover:bg-[#383b46] disabled:opacity-50"
                      >
                        Substituir imagem
                      </button>

                      <button
                        type="button"
                        disabled={enviando}
                        onClick={removerImagem}
                        className="rounded-md border border-red-500/40 px-4 py-2 text-sm font-medium text-red-400 hover:bg-red-500/10 disabled:opacity-50"
                      >
                        Remover
                      </button>

                    </div>
                  </>
                ) : (
                  <>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="48"
                      height="48"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      className="text-orange-500"
                    >
                      <rect
                        x="3"
                        y="3"
                        width="18"
                        height="18"
                        rx="2"
                      />
                      <circle cx="8.5" cy="8.5" r="1.5" />
                      <path d="m21 15-5-5L5 21" />
                    </svg>

                    <p className="text-center text-sm font-medium text-neutral-200">
                      Arraste e solte sua imagem aqui
                    </p>

                    <p className="text-center text-sm text-neutral-400">
                      ou selecione um arquivo do seu computador
                    </p>

                    <button
                      type="button"
                      disabled={enviando}
                      onClick={() =>
                        inputArquivo.current?.click()
                      }
                      className="rounded-md bg-[#2b2e38] px-5 py-2 text-sm font-semibold text-neutral-200 transition hover:bg-[#383b46] disabled:opacity-50"
                    >
                      Selecionar imagem
                    </button>

                    <p className="text-xs text-neutral-500">
                      JPG ou PNG — até 10 MB
                    </p>
                  </>
                )}

              </div>

              <p className="mt-2 text-xs text-neutral-500">
                A imagem de referência é opcional.
              </p>

            </div>

            <div className="rounded-lg border border-blue-500/30 bg-blue-500/10 p-4">
              <p className="text-sm leading-relaxed text-blue-200">
                O agendamento só é liberado depois que
                você aprovar o orçamento e o desenho
                enviados pelo tatuador.
              </p>
            </div>

          </div>

        </div>

        {erro && (
          <p
            role="alert"
            className="mt-5 rounded-md border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400"
          >
            {erro}
          </p>
        )}

        {/* BOTÕES */}

        <div className="mt-6 flex flex-wrap justify-end gap-3">

          <button
            type="button"
            disabled={enviando}
            onClick={() => navigate('/cliente')}
            className="rounded-lg bg-[#2b2e38] px-6 py-3 text-sm font-semibold text-neutral-200 transition hover:bg-[#383b46] disabled:opacity-50"
          >
            Cancelar
          </button>

          <button
            type="submit"
            disabled={enviando}
            className="rounded-lg bg-[#ff572f] px-6 py-3 text-sm font-semibold text-white transition hover:bg-[#e94b26] disabled:opacity-50"
          >
            {enviando ? etapaEnvio || 'Enviando...' : 'Enviar pedido'}
          </button>

        </div>

      </form>

    </section>
  )
}
