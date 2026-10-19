import { useEffect, useState } from 'react'
import { listarEntregas, crearEntrega, asignarEntrega, estadoEntrega, eliminarEntrega } from '../api/operaciones'
import { listarConductoresDisponibles, miConductor } from '../api/personas'
import { listarVehiculos } from '../api/operaciones'
import { get } from '../api/client'

export default function Entregas({ avisar, user, esStaff }) {
  const [lista, setLista] = useState([])
  const [conductores, setConductores] = useState([])
  const [vehiculos, setVehiculos] = useState([])
  const [pedidoId, setPedidoId] = useState('')
  const [asig, setAsig] = useState({})

  async function cargar() {
    try {
      if (esStaff) {
        setLista(await listarEntregas())
        setConductores(await listarConductoresDisponibles())
        setVehiculos((await listarVehiculos()).filter((v) => v.estado === 'DISPONIBLE'))
      } else {
        // CONDUCTOR: solo sus entregas asignadas (sin combos staff, sin 403).
        const mio = await miConductor()
        setLista(await get(`/operaciones/entregas/conductor/${mio.id}`))
      }
    } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try { await crearEntrega(Number(pedidoId)); setPedidoId(''); cargar(); avisar('Entrega creada') }
    catch (ex) { avisar(ex.message, true) }
  }

  async function asignar(id) {
    const a = asig[id]
    if (!a?.conductorId || !a?.vehiculoId) { avisar('Elige conductor y vehículo', true); return }
    try {
      await asignarEntrega(id, Number(a.conductorId), Number(a.vehiculoId))
      cargar(); avisar('Asignada (conductor validado por Feign)')
    } catch (ex) { avisar(ex.message, true) }
  }

  async function estado(id, est) {
    try { await estadoEntrega(id, est); cargar(); avisar('Entrega ' + est) }
    catch (ex) { avisar(ex.message, true) }
  }

  const puedeAvanzar = esStaff || user?.rol === 'CONDUCTOR'

  return (
    <div className="lc-section">
      {esStaff && (
        <form className="lc-row" onSubmit={crear}>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">ID Pedido</label>
              <input className="lc-input" type="number" min="1" placeholder="ID Pedido" value={pedidoId} onChange={(e) => setPedidoId(e.target.value)} required />
            </div>
          </div>
          <div className="lc-col-auto">
            <button className="lc-btn lc-btn-primary" type="submit">Crear entrega</button>
          </div>
        </form>
      )}
      <table className="lc-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Pedido</th>
            <th>Detalle</th>
            <th>Conductor</th>
            <th>Vehículo</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr>
              <td colSpan={7} className="empty-state">
                <p>No hay entregas registradas.</p>
              </td>
            </tr>
          )}
          {lista.map((e) => (
            <tr key={e.id}>
              <td>#{e.id}</td>
              <td>#{e.pedidoId}</td>
              <td>
                <div>{e.pedidoDescripcion || '—'}</div>
                <small className="text-muted">{e.pedidoDireccionEntrega || ''}</small>
              </td>
              <td>{e.conductorId ? '#' + e.conductorId : '—'}</td>
              <td>{e.vehiculoPlaca || (e.vehiculoId ? '#' + e.vehiculoId : '—')}</td>
              <td>
                <span className={`lc-badge ${e.estado === 'PENDIENTE' ? 'lc-badge-yellow' : e.estado === 'ASIGNADA' ? 'lc-badge-blue' : e.estado === 'EN_RUTA' ? 'lc-badge-purple' : e.estado === 'ENTREGADA' ? 'lc-badge-green' : 'lc-badge-red'}`}>
                  {e.estado}
                </span>
              </td>
              <td style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                {e.estado === 'PENDIENTE' ? (
                  esStaff ? (
                    <>
                      <select className="lc-select" style={{ width: 140, padding: '6px 10px', fontSize: 13 }} value={asig[e.id]?.conductorId || ''} onChange={(ev) => setAsig({ ...asig, [e.id]: { ...asig[e.id], conductorId: ev.target.value } })}>
                        <option value="">Conductor...</option>
                        {conductores.map((c) => <option key={c.id} value={c.id}>{c.id} · {c.nombre}</option>)}
                      </select>
                      <select className="lc-select" style={{ width: 120, padding: '6px 10px', fontSize: 13 }} value={asig[e.id]?.vehiculoId || ''} onChange={(ev) => setAsig({ ...asig, [e.id]: { ...asig[e.id], vehiculoId: ev.target.value } })}>
                        <option value="">Vehículo...</option>
                        {vehiculos.map((v) => <option key={v.id} value={v.id}>{v.placa}</option>)}
                      </select>
                      <button className="lc-btn lc-btn-primary lc-btn-sm" onClick={() => asignar(e.id)}>Asignar</button>
                    </>
                  ) : <small className="text-muted">Pendiente de asignación</small>
                ) : (
                  <>
                    {puedeAvanzar && e.estado === 'ASIGNADA' && <button className="lc-btn lc-btn-outline lc-btn-sm" onClick={() => estado(e.id, 'EN_RUTA')}>Salir a ruta</button>}
                    {puedeAvanzar && e.estado === 'EN_RUTA' && <button className="lc-btn lc-btn-success lc-btn-sm" onClick={() => estado(e.id, 'ENTREGADA')}>Entregada</button>}
                    {esStaff && !['ENTREGADA', 'CANCELADA'].includes(e.estado) && <button className="lc-btn lc-btn-warning lc-btn-sm" onClick={() => estado(e.id, 'CANCELADA')}>Cancelar</button>}
                    {esStaff && <button className="lc-btn lc-btn-danger lc-btn-sm" onClick={async () => {
                      if (!confirm('¿Eliminar?')) return
                      try { await eliminarEntrega(e.id); cargar(); avisar('Eliminada') } catch (ex) { avisar(ex.message, true) }
                    }}>X</button>}
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
