import { useEffect, useState } from 'react'
import { listarClientes, crearCliente, eliminarCliente, listarUsuarios } from '../api/personas'

export default function Clientes({ avisar, esAdmin }) {
  const [lista, setLista] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [form, setForm] = useState({ usuarioId: '', telefono: '', direccion: '' })

  async function cargar() {
    try {
      setLista(await listarClientes())
      if (esAdmin) setUsuarios(await listarUsuarios())
    } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try {
      await crearCliente({ ...form, usuarioId: Number(form.usuarioId) })
      setForm({ usuarioId: '', telefono: '', direccion: '' }); cargar(); avisar('Cliente creado')
    } catch (ex) { avisar(ex.message, true) }
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
              <label className="lc-form-label">Teléfono</label>
              <input className="lc-input" placeholder="Teléfono" value={form.telefono} onChange={(e) => setForm({ ...form, telefono: e.target.value })} />
            </div>
          </div>
          <div className="lc-col">
            <div className="lc-form-group">
              <label className="lc-form-label">Dirección</label>
              <input className="lc-input" placeholder="Dirección" value={form.direccion} onChange={(e) => setForm({ ...form, direccion: e.target.value })} />
            </div>
          </div>
          <div className="lc-col-auto">
            <button className="lc-btn lc-btn-primary" type="submit">Crear</button>
          </div>
        </form>
      )}
      <table className="lc-table">
        <thead>
          <tr><th>ID</th><th>Usuario</th><th>Email</th><th>Teléfono</th><th>Dirección</th>{esAdmin && <th></th>}</tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr><td colSpan={esAdmin ? 6 : 5} className="empty-state"><p>No hay clientes registrados.</p></td></tr>
          )}
          {lista.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.nombre}</td>
              <td>{c.email}</td>
              <td>{c.telefono}</td>
              <td>{c.direccion}</td>
              {esAdmin && (
                <td><button className="lc-btn lc-btn-danger lc-btn-sm" onClick={async () => {
                  if (!confirm('¿Eliminar?')) return
                  try { await eliminarCliente(c.id); cargar(); avisar('Eliminado') } catch (ex) { avisar(ex.message, true) }
                }}>X</button></td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
