import { useEffect, useState } from 'react'
import { listarConductores, crearConductor, actualizarConductor, eliminarConductor, listarUsuarios } from '../api/personas'

export default function Conductores({ avisar, esAdmin }) {
  const [lista, setLista] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [form, setForm] = useState({ usuarioId: '', licencia: '', telefono: '' })

  async function cargar() {
    try {
      setLista(await listarConductores())
      if (esAdmin) setUsuarios(await listarUsuarios())
    } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try {
      await crearConductor({ ...form, usuarioId: Number(form.usuarioId) })
      setForm({ usuarioId: '', licencia: '', telefono: '' }); cargar(); avisar('Conductor creado')
    } catch (ex) { avisar(ex.message, true) }
  }

  async function estado(c, estado) {
    try { await actualizarConductor(c.id, { estado }); cargar(); avisar('Estado: ' + estado) }
    catch (ex) { avisar(ex.message, true) }
  }

  return (
    <div className="lc-section">
      {esAdmin && (
        <form className="lc-row" onSubmit={crear}>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Usuario</label>
              <select className="lc-select" value={form.usuarioId} onChange={(e) => setForm({ ...form, usuarioId: e.target.value })} required>
                <option value="">Seleccionar...</option>
                {usuarios.map((u) => <option key={u.id} value={u.id}>{u.id} · {u.nombre} {u.apellido}</option>)}
              </select>
            </div>
          </div>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Licencia</label>
              <input className="lc-input" placeholder="Licencia" value={form.licencia} onChange={(e) => setForm({ ...form, licencia: e.target.value })} required />
            </div>
          </div>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Teléfono</label>
              <input className="lc-input" placeholder="Teléfono" value={form.telefono} onChange={(e) => setForm({ ...form, telefono: e.target.value })} />
            </div>
          </div>
          <div className="lc-col-auto">
            <button className="lc-btn lc-btn-primary" type="submit">Crear</button>
          </div>
        </form>
      )}
      <table className="lc-table">
        <thead>
          <tr><th>ID</th><th>Nombre</th><th>Licencia</th><th>Teléfono</th><th>Estado</th>{esAdmin && <th></th>}</tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr><td colSpan={esAdmin ? 6 : 5} className="empty-state"><p>No hay conductores registrados.</p></td></tr>
          )}
          {lista.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.nombre}</td>
              <td>{c.licencia}</td>
              <td>{c.telefono}</td>
              <td>
                <span className={`lc-badge ${c.estado === 'DISPONIBLE' ? 'lc-badge-green' : 'lc-badge-yellow'}`}>
                  {c.estado}
                </span>
              </td>
              {esAdmin && (
                <td style={{ display: 'flex', gap: 6 }}>
                  <button className="lc-btn lc-btn-outline lc-btn-sm" onClick={() => estado(c, c.estado === 'DISPONIBLE' ? 'OCUPADO' : 'DISPONIBLE')}>↔</button>
                  <button className="lc-btn lc-btn-danger lc-btn-sm" onClick={async () => {
                    if (!confirm('¿Eliminar?')) return
                    try { await eliminarConductor(c.id); cargar(); avisar('Eliminado') } catch (ex) { avisar(ex.message, true) }
                  }}>X</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
