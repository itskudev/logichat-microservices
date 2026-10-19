import { useEffect, useState } from 'react'
import { listarPedidos, crearPedido, estadoPedido, eliminarPedido, crearEntrega } from '../api/operaciones'
import { listarClientes, miCliente } from '../api/personas'
import { get } from '../api/client'

const ESTADOS = ['PENDIENTE', 'PREPARANDO', 'LISTO', 'CANCELADO']

export default function Pedidos({ avisar, esStaff }) {
  const [lista, setLista] = useState([])
  const [clientes, setClientes] = useState([])
  const [miId, setMiId] = useState(null)
  const [form, setForm] = useState({ clienteId: '', descripcion: '', direccionEntrega: '' })

  async function cargar() {
    try {
      if (esStaff) {
        setLista(await listarPedidos())
        setClientes(await listarClientes())
      } else {
        const mio = await miCliente()
        setMiId(mio.id)
        setLista(await get(`/operaciones/pedidos/cliente/${mio.id}`))
      }
    } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try {
      const clienteId = esStaff ? Number(form.clienteId) : miId
      await crearPedido({ ...form, clienteId })
      setForm({ clienteId: '', descripcion: '', direccionEntrega: '' })
      cargar(); avisar('Pedido creado')
    } catch (ex) { avisar(ex.message, true) }
  }

  return (
    <div className="lc-section">
      {esStaff && (
        <form className="lc-row" onSubmit={crear}>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Cliente</label>
              <select className="lc-select" value={form.clienteId} onChange={(e) => setForm({ ...form, clienteId: e.target.value })} required>
                <option value="">Seleccionar...</option>
                {clientes.map((c) => <option key={c.id} value={c.id}>{c.id} · {c.nombre}</option>)}
              </select>
            </div>
          </div>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Descripción</label>
              <input className="lc-input" placeholder="Descripción" value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} required />
            </div>
          </div>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Dirección entrega</label>
              <input className="lc-input" placeholder="Dirección" value={form.direccionEntrega} onChange={(e) => setForm({ ...form, direccionEntrega: e.target.value })} required />
            </div>
          </div>
          <div className="lc-col-auto">
            <button className="lc-btn lc-btn-primary" type="submit">Crear</button>
          </div>
        </form>
      )}
      {!esStaff && (
        <div className="lc-row">
          <div className="lc-col-auto">
            <button className="lc-btn lc-btn-outline lc-btn-sm" onClick={cargar}>Actualizar</button>
          </div>
        </div>
      )}
      <table className="lc-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Descripción</th>
            <th>Dirección</th>
            <th>Estado</th>
            {esStaff && <th>Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr>
              <td colSpan={esStaff ? 6 : 5} className="empty-state">
                <p>No hay pedidos registrados.</p>
              </td>
            </tr>
          )}
          {lista.map((p) => (
            <tr key={p.id}>
              <td>#{p.id}</td>
              <td>#{p.clienteId}</td>
              <td>{p.descripcion}</td>
              <td>{p.direccionEntrega}</td>
              <td>
                <span className={`lc-badge ${p.estado === 'PENDIENTE' ? 'lc-badge-yellow' : p.estado === 'LISTO' ? 'lc-badge-blue' : p.estado === 'CANCELADO' ? 'lc-badge-red' : 'lc-badge-green'}`}>
                  {p.estado}
                </span>
                {p.tieneEntrega && <span className="lc-badge lc-badge-blue" style={{ fontSize: 11, marginLeft: 6 }}>· en entrega</span>}
              </td>
              {esStaff && (
                <td className="d-flex gap-1" style={{ display: 'flex', gap: 6 }}>
                  {!p.tieneEntrega ? (
                    <>
                      <select className="lc-select" style={{ width: 150, padding: '6px 10px', fontSize: 13 }} value={['PENDIENTE','PREPARANDO','LISTO','CANCELADO'].includes(p.estado) ? p.estado : 'PENDIENTE'} onChange={async (e) => {
                        try { await estadoPedido(p.id, e.target.value); cargar(); avisar('Estado actualizado') } catch (ex) { avisar(ex.message, true) }
                      }}>
                        {ESTADOS.map((s) => <option key={s}>{s}</option>)}
                      </select>
                      <button className="lc-btn lc-btn-primary lc-btn-sm" onClick={async () => {
                        try { await crearEntrega(p.id); cargar(); avisar('Entrega creada para pedido #' + p.id) } catch (ex) { avisar(ex.message, true) }
                      }}>→ Entrega</button>
                      <button className="lc-btn lc-btn-danger lc-btn-sm" onClick={async () => {
                        if (!confirm('¿Eliminar?')) return
                        try { await eliminarPedido(p.id); cargar(); avisar('Eliminado') } catch (ex) { avisar(ex.message, true) }
                      }}>X</button>
                    </>
                  ) : <small className="text-muted">Lo mueve la entrega</small>}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
