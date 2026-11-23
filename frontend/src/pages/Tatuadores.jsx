import { useCallback, useEffect, useState } from 'react'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import { botaoPrimario, botaoSecundario, estiloCampo } from '../components/estilos.js'
import { camposDeErro, mensagemDeErro } from '../services/api.js'
import {
  alterarSituacaoDoTatuador,
  atualizarTatuador,
  criarTatuador,
  listarSalas,
  listarTatuadores,
} from '../services/tatuadorService.js'

const FORMULARIO_VAZIO = {
  nome: '',
  email: '',
  senhaInicial: '',
  especialidade: '',
  registroProfissional: '',
  salaPadraoId: '',
}

// Cadastro de tatuadores pelo administrador (RF-03) com vínculo à sala exclusiva (RN-03).
export default function Tatuadores() {
  const [tatuadores, setTatuadores] = useState([])
  const [salas, setSalas] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [formularioAberto, setFormularioAberto] = useState(false)
  const [editando, setEditando] = useState(null)
  const [formulario, setFormulario] = useState(FORMULARIO_VAZIO)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [sucesso, setSucesso] = useState('')
  const [salvando, setSalvando] = useState(false)

  const carregar = useCallback(async () => {
    try {
      const [listaDeTatuadores, listaDeSalas] = await Promise.all([listarTatuadores(), listarSalas()])
      setTatuadores(listaDeTatuadores)
      setSalas(listaDeSalas)
    } catch (falha) {
      setErro(mensagemDeErro(falha))
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    carregar()
  }, [carregar])

  // Somente salas exclusivas, ativas e livres (ou a sala atual do tatuador em edição).
  const salasDisponiveis = salas.filter(
    (sala) => sala.tipo === 'EXCLUSIVA' && sala.ativa && (!sala.ocupada || sala.id === editando?.salaId),
  )

  function limparMensagens() {
    setErro('')
    setErros({})
    setSucesso('')
  }

  function abrirNovo() {
    limparMensagens()
    setEditando(null)
    setFormulario(FORMULARIO_VAZIO)
    setFormularioAberto(true)
  }

  function abrirEdicao(tatuador) {
    limparMensagens()
    setEditando(tatuador)
    setFormulario({
      nome: tatuador.nome,
      email: '',
      senhaInicial: '',
      especialidade: tatuador.especialidade ?? '',
      registroProfissional: tatuador.registroProfissional ?? '',
      salaPadraoId: String(tatuador.salaId),
    })
    setFormularioAberto(true)
  }

  function cancelar() {
    limparMensagens()
    setFormularioAberto(false)
    setEditando(null)
  }

  function alterar(campo) {
    return (evento) => setFormulario((atual) => ({ ...atual, [campo]: evento.target.value }))
  }

  async function salvar(evento) {
    evento.preventDefault()
    limparMensagens()
    setSalvando(true)
    try {
      const salaPadraoId = formulario.salaPadraoId ? Number(formulario.salaPadraoId) : null
      const resposta = editando
        ? await atualizarTatuador(editando.id, {
            nome: formulario.nome,
            especialidade: formulario.especialidade,
            registroProfissional: formulario.registroProfissional,
            salaPadraoId,
          })
        : await criarTatuador({ ...formulario, salaPadraoId })
      setFormularioAberto(false)
      setEditando(null)
      setSucesso(resposta.message)
      await carregar()
    } catch (falha) {
      setErro(mensagemDeErro(falha))
      setErros(camposDeErro(falha))
    } finally {
      setSalvando(false)
    }
  }

  async function alternarSituacao(tatuador) {
    limparMensagens()
    try {
      const resposta = await alterarSituacaoDoTatuador(tatuador.id, !tatuador.ativo)
      setSucesso(resposta.message)
      await carregar()
    } catch (falha) {
      setErro(mensagemDeErro(falha))
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-3xl font-bold">Tatuadores</h1>
        <p className="text-neutral-400">Cadastre os profissionais e vincule cada um à sua sala exclusiva.</p>
      </div>

      <Alerta>{!formularioAberto ? erro : ''}</Alerta>
      <Alerta tipo="sucesso">{sucesso}</Alerta>

      <div className="grid gap-4 lg:grid-cols-3">
        <section className={`rounded-lg border border-neutral-800 bg-neutral-900 p-5 ${formularioAberto ? 'lg:col-span-2' : 'lg:col-span-3'}`}>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-bold">Profissionais cadastrados</h2>
            <button type="button" onClick={abrirNovo} className={botaoPrimario}>
              + Novo tatuador
            </button>
          </div>

          {carregando ? (
            <p className="text-neutral-400">Carregando...</p>
          ) : tatuadores.length === 0 ? (
            <p className="text-neutral-400">Nenhum tatuador cadastrado ainda.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-neutral-700 text-xs uppercase text-neutral-500">
                    <th className="py-2 pr-3">Nome</th>
                    <th className="py-2 pr-3">Especialidade</th>
                    <th className="py-2 pr-3">Sala</th>
                    <th className="py-2 pr-3">Situação</th>
                    <th className="py-2">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {tatuadores.map((tatuador) => (
                    <tr key={tatuador.id} className="border-b border-neutral-800">
                      <td className="py-3 pr-3">{tatuador.nome}</td>
                      <td className="py-3 pr-3">{tatuador.especialidade ?? '—'}</td>
                      <td className="py-3 pr-3">{tatuador.salaNome}</td>
                      <td className="py-3 pr-3">
                        <span className={`rounded-full border px-3 py-0.5 text-sm ${tatuador.ativo ? 'border-green-600 text-green-300' : 'border-neutral-600 text-neutral-400'}`}>
                          {tatuador.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                      </td>
                      <td className="space-x-3 py-3">
                        <button type="button" onClick={() => abrirEdicao(tatuador)} className="text-orange-400 hover:underline">
                          Editar
                        </button>
                        <button type="button" onClick={() => alternarSituacao(tatuador)} className="text-neutral-300 hover:underline">
                          {tatuador.ativo ? 'Inativar' : 'Reativar'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          <p className="mt-4 rounded-md border border-neutral-700 bg-neutral-950 p-3 text-sm text-neutral-300">
            Cada tatuador tem uma sala exclusiva. As salas compartilhadas ficam disponíveis a todos os profissionais [RN-03].
          </p>
        </section>

        {formularioAberto && (
          <form onSubmit={salvar} className="space-y-3 rounded-lg border border-neutral-800 bg-neutral-900 p-5">
            <h2 className="text-lg font-bold">{editando ? 'Editar tatuador' : 'Novo tatuador'}</h2>

            <Alerta>{erro}</Alerta>

            <Campo rotulo="Nome *" required maxLength={100} placeholder="Nome completo"
              value={formulario.nome} onChange={alterar('nome')} erro={erros.nome} />

            {!editando && (
              <>
                <Campo rotulo="E-mail de acesso *" type="email" required maxLength={150}
                  value={formulario.email} onChange={alterar('email')} erro={erros.email} />
                <Campo rotulo="Senha inicial *" type="password" required minLength={8} maxLength={72}
                  autoComplete="new-password" placeholder="Mínimo de 8 caracteres"
                  value={formulario.senhaInicial} onChange={alterar('senhaInicial')} erro={erros.senhaInicial} />
              </>
            )}

            <Campo rotulo="Especialidade" maxLength={80} placeholder="ex.: Realismo"
              value={formulario.especialidade} onChange={alterar('especialidade')} erro={erros.especialidade} />

            <Campo rotulo="Sala exclusiva *" erro={erros.salaPadraoId}>
              <select required className={estiloCampo} value={formulario.salaPadraoId} onChange={alterar('salaPadraoId')}>
                <option value="">Selecione uma sala</option>
                {salasDisponiveis.map((sala) => (
                  <option key={sala.id} value={sala.id}>
                    {sala.nome}
                  </option>
                ))}
              </select>
            </Campo>

            <Campo rotulo="Registro profissional" maxLength={30}
              value={formulario.registroProfissional} onChange={alterar('registroProfissional')}
              erro={erros.registroProfissional} />

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={cancelar} className={botaoSecundario}>
                Cancelar
              </button>
              <button type="submit" disabled={salvando} className={botaoPrimario}>
                {salvando ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
