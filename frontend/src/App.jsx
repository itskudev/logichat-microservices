import { useEffect, useState } from 'react'
import Login from './components/Login'
import Usuarios from './components/Usuarios'
import Clientes from './components/Clientes'
import Conductores from './components/Conductores'
import Vehiculos from './components/Vehiculos'
import Pedidos from './components/Pedidos'
import Entregas from './components/Entregas'

const ICONS = {
  pedidos: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><line x1="14" y1="7" x2="14" y2="7"/><line x1="10" y1="7" x2="10" y2="7"/></svg>`,
  entregas: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="10" height="15"/><circle cx="4" cy="20" r="2"/><circle cx="14" cy="20" r="2"/><line x1="6" y1="3" x2="6" y2="15"/><line x1="11" y1="3" x2="11" y2="15"/><path d="M3 11h18"/></svg>`,
  usuarios: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
  clientes: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="9" y1="21" x2="9" y2="3"/></svg>`,
  conductores: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="18" height="16" rx="2" ry="2"/><line x1="4" y1="11" x2="20" y2="11"/><line x1="6" y1="3" x2="6" y2="19"/><line x1="12" y1="3" x2="12" y2="19"/><line x1="18" y1="3" x2="18" y2="19"/></svg>`,
  vehiculos: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="18" height="16" rx="2" ry="2"/><line x1="4" y1="11" x2="20" y2="11"/><circle cx="6" cy="18" r="2"/><circle cx="18" cy="18" r="2"/></svg>`,
}

const NAV = [
  { id: 'pedidos', label: 'Pedidos' },
  { id: 'entregas', label: 'Entregas' },
  { id: 'usuarios', label: 'Usuarios' },
  { id: 'clientes', label: 'Clientes' },
  { id: 'conductores', label: 'Conductores' },
  { id: 'vehiculos', label: 'Vehículos' },
]

const LABELS = {
  pedidos: 'Pedidos',
  entregas: 'Entregas',
  usuarios: 'Usuarios',
  clientes: 'Clientes',
  conductores: 'Conductores',
  vehiculos: 'Vehículos',
}

export default function App() {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('logichat_user')) } catch { return null }
  })
  const [tab, setTab] = useState('pedidos')
  const [toast, setToast] = useState(null)

  useEffect(() => {
    const salir = () => setUser(null)
    window.addEventListener('logichat:unauthorized', salir)
    return () => window.removeEventListener('logichat:unauthorized', salir)
  }, [])

  function avisar(text, mal = false) {
    setToast({ text, mal })
    setTimeout(() => setToast(null), 4000)
  }

  function salir() {
    localStorage.removeItem('logichat_token')
    localStorage.removeItem('logichat_user')
    setUser(null)
  }

  if (!user) return <div className="lc-app"><Login onAuth={setUser} /></div>

  const esStaff = ['ADMIN', 'COORDINADOR'].includes(user.rol)
  const esAdmin = user.rol === 'ADMIN'

  const itemsVisibles = NAV.filter(({ id }) => {
    if (id === 'usuarios') return esAdmin
    if (esStaff) return true
    if (user.rol === 'CLIENTE') return id === 'pedidos'
    if (user.rol === 'CONDUCTOR') return id === 'entregas'
    return false
  })
  const tabActivo = itemsVisibles.some((i) => i.id === tab) ? tab : itemsVisibles[0]?.id

  return (
    <div className="lc-app">
      <header className="lc-header">
        <div className="lc-header-inner">
          <div className="lc-brand">
            <div className="lc-brand-icon">LC</div>
            <div className="lc-brand-text">
              LogiChat <small>· {user.nombre}</small>
            </div>
          </div>
          <div className="lc-user">
            <span className="lc-user-badge">{user.rol}</span>
            <button className="lc-btn lc-btn-outline lc-btn-sm" onClick={salir}>Salir</button>
          </div>
        </div>
      </header>

      <div className="lc-layout">
        <aside className="lc-sidebar">
          <nav className="lc-sidebar-inner">
            <ul className="lc-nav">
              {itemsVisibles.map((item) => (
                <li key={item.id}>
                  <button
                    className={`lc-nav-item ${tabActivo === item.id ? 'active' : ''}`}
                    onClick={() => setTab(item.id)}
                  >
                    <span className="lc-icon" dangerouslySetInnerHTML={{ __html: ICONS[item.id] }} />
                    {item.label}
                  </button>
                </li>
              ))}
            </ul>
          </nav>
        </aside>

        <main className="lc-main">
          <div className="lc-page-header">
            <h5 className="lc-h5">{LABELS[tabActivo]}</h5>
          </div>
          {tabActivo === 'pedidos' && <Pedidos avisar={avisar} user={user} esStaff={esStaff} />}
          {tabActivo === 'entregas' && <Entregas avisar={avisar} user={user} esStaff={esStaff} />}
          {tabActivo === 'usuarios' && <Usuarios avisar={avisar} />}
          {tabActivo === 'clientes' && <Clientes avisar={avisar} esAdmin={esAdmin} />}
          {tabActivo === 'conductores' && <Conductores avisar={avisar} esAdmin={esAdmin} />}
          {tabActivo === 'vehiculos' && <Vehiculos avisar={avisar} />}
          {toast && (
            <div className={`toast ${toast.mal ? 'toast-error' : 'toast-success'}`}>
              {toast.text}
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
