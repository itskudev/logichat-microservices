// Cliente HTTP base contra el Gateway.
// El backend responde ApiResponse {success, mensaje, data}: aquí se desempaqueta.
// Envía el JWT guardado al login (localStorage logichat_token).
const BASE = import.meta.env.VITE_API || 'http://localhost:8080'

function mensajeError(body, status) {
  if (body && typeof body.mensaje === 'string' && body.mensaje) return body.mensaje
  if (body && typeof body.message === 'string' && body.message) return body.message
  if (body && body.messages && typeof body.messages === 'object') return Object.values(body.messages).join('. ')
  return `Error ${status}`
}

async function request(path, options = {}) {
  const headers = new Headers(options.headers || {})
  headers.set('Accept', 'application/json')
  if (options.body) headers.set('Content-Type', 'application/json')
  const token = localStorage.getItem('logichat_token')
  if (token) headers.set('Authorization', 'Bearer ' + token)
  let res
  try {
    res = await fetch(BASE + path, { ...options, headers })
  } catch {
    throw new Error('No se pudo conectar con el Gateway (http://localhost:8080).')
  }
  if (res.status === 204) return null
  const body = await res.json().catch(() => ({}))
  if (!res.ok) {
    if (res.status === 401) {
      localStorage.removeItem('logichat_token')
      localStorage.removeItem('logichat_user')
      window.dispatchEvent(new Event('logichat:unauthorized'))
    }
    throw new Error(mensajeError(body, res.status))
  }
  // Desempaqueta ApiResponse; si el endpoint responde crudo (interno), lo devuelve tal cual.
  if (body && typeof body === 'object' && 'success' in body && 'data' in body) return body.data
  return body
}

export const get = (path) => request(path)
export const post = (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) })
export const put = (path, body = {}) => request(path, { method: 'PUT', body: JSON.stringify(body) })
export const del = (path) => request(path, { method: 'DELETE' })
