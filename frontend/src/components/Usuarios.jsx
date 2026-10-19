import { useEffect, useState } from 'react'
import { listarUsuarios, crearUsuario, cambiarRol, bloquear, desbloquear, eliminarUsuario } from '../api/personas'

const ROLES = ['ADMIN', 'COORDINADOR', 'CONDUCTOR', 'CLIENTE']

export default function Usuarios({ avisar }) {
  const [lista, setLista] = useState([])
  const [form, setForm] = useState({ nombre: '', apellido: '', email: '', password: '', rol: 'CLIENTE' })

  async function cargar() {
    try { setLista(await listarUsuarios()) } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try { await crearUsuario(form); setForm({ nombre: '', apellido: '', email: '', password: '', rol: 'CLIENTE' }); cargar(); avisar('Usuario creado') }
    catch (ex) { avisar(ex.message, true) }
  }

  async function acc(fn, msg) {
    try { await fn(); cargar(); avisar(msg) } catch (ex) { avisar(ex.message, true) }
  }

  return (
    <div className="lc-section">
      <form className="lc-row" onSubmit={crear}>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Nombre</label>
            <input className="lc-input" placeholder="Nombre" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Apellido</label>
            <input className="lc-input" placeholder="Apellido" value={form.apellido} onChange={(e) => setForm({ ...form, apellido: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Email</label>
            <input className="lc-input" type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Clave</label>
            <input className="lc-input" type="password" placeholder="Clave" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Rol</label>
            <select className="lc-select" value={form.rol} onChange={(e) => setForm({ ...form, rol: e.target.value })}>
              {ROLES.map((r) => <option key={r}>{r}</option>)}
            </select>
          </div>
        </div>
        <div className="lc-col-auto">
          <button className="lc-btn lc-btn-primary" type="submit">Crear</button>
        </div>
      </form>
      <table className="lc-table">
        <thead>
          <tr><th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr><td colSpan={6} className="empty-state"><p>No hay usuarios registrados.</p></td></tr>
          )}
          {lista.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.nombre} {u.apellido}</td>
              <td>{u.email}</td>
              <td>
                <span className={`lc-badge ${u.rol === 'ADMIN' ? 'lc-badge-purple' : u.rol === 'COORDINADOR' ? 'lc-badge-blue' : u.rol === 'CONDUCTOR' ? 'lc-badge-purple' : 'lc-badge-gray'}`}>
                  {u.rol}
                </span>
              </td>
              <td>
                <span className={`lc-badge ${u.estado === 'ACTIVO' ? 'lc-badge-green' : 'lc-badge-red'}`}>
                  {u.estado}
                </span>
              </td>
              <td style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                <select className="lc-select" style={{ width: 140, padding: '6px 10px', fontSize: 13 }} value={u.rol} onChange={(e) => acc(() => cambiarRol(u.id, e.target.value), 'Rol cambiado')}>
                  {ROLES.map((r) => <option key={r}>{r}</option>)}
                </select>
                {u.estado === 'ACTIVO'
                  ? <button className="lc-btn lc-btn-warning lc-btn-sm" onClick={() => acc(() => bloquear(u.id), 'Bloqueado')}>Bloquear</button>
                  : <button className="lc-btn lc-btn-success lc-btn-sm" onClick={() => acc(() => desbloquear(u.id), 'Activar')}>Activar</button>}
                <button className="lc-btn lc-btn-danger lc-btn-sm" onClick={() => confirm('¿Eliminar?') && acc(() => eliminarUsuario(u.id), 'Eliminado')}>X</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
