import { useEffect, useState } from 'react'
import { listarVehiculos, crearVehiculo, eliminarVehiculo } from '../api/operaciones'

export default function Vehiculos({ avisar }) {
  const [lista, setLista] = useState([])
  const [form, setForm] = useState({ placa: '', tipo: '', marca: '', modelo: '' })

  async function cargar() {
    try { setLista(await listarVehiculos()) } catch (ex) { avisar(ex.message, true) }
  }
  useEffect(() => { cargar() }, [])

  async function crear(e) {
    e.preventDefault()
    try { await crearVehiculo(form); setForm({ placa: '', tipo: '', marca: '', modelo: '' }); cargar(); avisar('Vehículo creado') }
    catch (ex) { avisar(ex.message, true) }
  }

  return (
    <div className="lc-section">
      <form className="lc-row" onSubmit={crear}>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Placa</label>
            <input className="lc-input" placeholder="Placa" value={form.placa} onChange={(e) => setForm({ ...form, placa: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Tipo</label>
            <input className="lc-input" placeholder="Tipo" value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })} required />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Marca</label>
            <input className="lc-input" placeholder="Marca" value={form.marca} onChange={(e) => setForm({ ...form, marca: e.target.value })} />
          </div>
        </div>
        <div className="lc-col">
          <div className="lc-form-group">
            <label className="lc-form-label">Modelo</label>
            <input className="lc-input" placeholder="Modelo" value={form.modelo} onChange={(e) => setForm({ ...form, modelo: e.target.value })} />
          </div>
        </div>
        <div className="lc-col-auto">
          <button className="lc-btn lc-btn-primary" type="submit">Crear</button>
        </div>
      </form>
      <table className="lc-table">
        <thead>
          <tr><th>ID</th><th>Placa</th><th>Tipo</th><th>Marca</th><th>Modelo</th><th>Estado</th><th></th></tr>
        </thead>
        <tbody>
          {lista.length === 0 && (
            <tr><td colSpan={7} className="empty-state"><p>No hay vehículos registrados.</p></td></tr>
          )}
          {lista.map((v) => (
            <tr key={v.id}>
              <td>{v.id}</td>
              <td>{v.placa}</td>
              <td>{v.tipo}</td>
              <td>{v.marca}</td>
              <td>{v.modelo}</td>
              <td>
                <span className={`lc-badge ${v.estado === 'DISPONIBLE' ? 'lc-badge-green' : 'lc-badge-yellow'}`}>
                  {v.estado}
                </span>
              </td>
              <td><button className="lc-btn lc-btn-danger lc-btn-sm" onClick={async () => {
                if (!confirm('¿Eliminar?')) return
                try { await eliminarVehiculo(v.id); cargar(); avisar('Eliminado') } catch (ex) { avisar(ex.message, true) }
              }}>X</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
