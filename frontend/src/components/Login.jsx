import { useState } from 'react'
import { login, register } from '../api/personas'

const ICONS = {
  lock: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>`,
  user: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  mail: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2"/><polyline points="22,6 12,13 2,6"/></svg>`,
}

export default function Login({ onAuth }) {
  const [modo, setModo] = useState('login')
  const [form, setForm] = useState({ nombre: '', apellido: '', email: 'admin@logichat.com', password: '123456' })
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setError('')
    setCargando(true)
    try {
      let data
      if (modo === 'login') {
        data = await login(form.email, form.password)
      } else {
        await register(form)
        data = await login(form.email, form.password)
      }
      localStorage.setItem('logichat_token', data.token)
      localStorage.setItem('logichat_user', JSON.stringify(data.usuario))
      onAuth(data.usuario)
    } catch (ex) {
      setError(ex.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="lc-login-wrap">
      <div className="lc-login-card">
        <div className="lc-login-header">
          <div className="lc-brand-icon">LC</div>
          <div className="lc-login-title">
            <span className="lc-login-app">LogiChat</span>
            <span className="lc-login-sub">{modo === 'login' ? 'Iniciar sesión' : 'Crear cuenta'}</span>
          </div>
        </div>

        <form onSubmit={enviar} className="lc-login-form">
          {modo === 'register' && (
            <>
              <div className="lc-login-field">
                <span className="lc-login-field-icon" dangerouslySetInnerHTML={{ __html: ICONS.user }} />
                <input className="lc-login-input" placeholder="Nombre" value={form.nombre} onChange={set('nombre')} required />
              </div>
              <div className="lc-login-field">
                <span className="lc-login-field-icon" dangerouslySetInnerHTML={{ __html: ICONS.user }} />
                <input className="lc-login-input" placeholder="Apellido" value={form.apellido} onChange={set('apellido')} required />
              </div>
            </>
          )}
          <div className="lc-login-field">
            <span className="lc-login-field-icon" dangerouslySetInnerHTML={{ __html: ICONS.mail }} />
            <input className="lc-login-input" type="email" placeholder="Correo electrónico" value={form.email} onChange={set('email')} required />
          </div>
          <div className="lc-login-field">
            <span className="lc-login-field-icon" dangerouslySetInnerHTML={{ __html: ICONS.lock }} />
            <input className="lc-login-input" type="password" placeholder="Contraseña" value={form.password} onChange={set('password')} required />
          </div>

          {error && (
            <div className="lc-login-error">{error}</div>
          )}

          <button className="lc-login-btn" type="submit" disabled={cargando}>
            {cargando ? 'Ingresando…' : modo === 'login' ? 'Entrar' : 'Registrarme'}
          </button>
        </form>

        <p className="lc-login-toggle">
          {modo === 'login' ? '¿No tienes cuenta?' : '¿Ya tienes cuenta?'}
          <button type="button" className="lc-login-toggle-link" onClick={() => setModo(modo === 'login' ? 'register' : 'login')}>
            {modo === 'login' ? 'Regístrate' : 'Inicia sesión'}
          </button>
        </p>
      </div>
    </div>
  )
}
