import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { mensagemDeErro } from '../services/api.js'
import { solicitarOrcamento } from '../services/orcamentoService.js'
import { listarTatuadores } from '../services/tatuadorService.js'
export default function PedirOrcamento() {
 const [tatuadores,setTatuadores]=useState([]),[erro,setErro]=useState(''),[enviando,setEnviando]=useState(false)
 const [form,setForm]=useState({tatuadorId:'',descricao:'',regiaoCorpo:'',tamanhoAproximado:'',imagemReferenciaUrl:''})
 const navigate=useNavigate()
 useEffect(()=>{listarTatuadores().then(d=>setTatuadores(d.filter(t=>t.ativo))).catch(e=>setErro(mensagemDeErro(e)))},[])
 async function enviar(e){e.preventDefault();setErro('');setEnviando(true);try{await solicitarOrcamento({...form,tatuadorId:Number(form.tatuadorId)});navigate('/cliente')}catch(e){setErro(mensagemDeErro(e))}finally{setEnviando(false)}}
 const campo='w-full rounded-md border border-neutral-700 bg-neutral-900 p-3 text-neutral-100'
 return <section className="max-w-2xl space-y-5"><h1 className="text-3xl font-bold">Solicitar orçamento</h1><p className="text-neutral-400">Conte sua ideia para o tatuador. Você poderá acompanhar o pedido em Meus projetos.</p>
 <form onSubmit={enviar} className="space-y-4">
 <label className="block">Tatuador<select className={campo} required value={form.tatuadorId} onChange={e=>setForm({...form,tatuadorId:e.target.value})}><option value="">Selecione</option>{tatuadores.map(t=><option key={t.id} value={t.id}>{t.nome} — {t.especialidade}</option>)}</select></label>
 <label className="block">Descrição da ideia<textarea className={campo} required maxLength={255} rows={4} value={form.descricao} onChange={e=>setForm({...form,descricao:e.target.value})}/></label>
 <label className="block">Região do corpo<input className={campo} required maxLength={60} value={form.regiaoCorpo} onChange={e=>setForm({...form,regiaoCorpo:e.target.value})}/></label>
 <label className="block">Tamanho aproximado<input className={campo} required maxLength={60} placeholder="Ex.: 15 cm" value={form.tamanhoAproximado} onChange={e=>setForm({...form,tamanhoAproximado:e.target.value})}/></label>
 <label className="block">Link da imagem de referência (opcional)<input className={campo} type="url" maxLength={1000} placeholder="https://..." value={form.imagemReferenciaUrl} onChange={e=>setForm({...form,imagemReferenciaUrl:e.target.value})}/></label>
 {erro&&<p role="alert" className="text-red-400">{erro}</p>}<button disabled={enviando} className="rounded-md bg-orange-500 px-5 py-3 font-semibold text-neutral-950 disabled:opacity-50">{enviando?'Enviando...':'Enviar pedido'}</button></form></section>
}
