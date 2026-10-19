import { get, post, put, del } from './client'

// Auth (ms-personas)
export const login = (email, password) => post('/auth/login', { email, password })
export const register = (data) => post('/auth/register', data)

// Usuarios (viven en ms-auth)
export const listarUsuarios = () => get('/auth/usuarios')
export const crearUsuario = (data) => post('/auth/usuarios', data)
export const cambiarRol = (id, rol) => put(`/auth/usuarios/${id}/rol`, { rol })
export const bloquear = (id) => put(`/auth/usuarios/${id}/bloquear`)
export const desbloquear = (id) => put(`/auth/usuarios/${id}/desbloquear`)
export const eliminarUsuario = (id) => del(`/auth/usuarios/${id}`)

// Clientes
export const listarClientes = () => get('/personas/clientes')
export const miCliente = () => get('/personas/clientes/mio')
export const crearCliente = (data) => post('/personas/clientes', data)
export const actualizarCliente = (id, data) => put(`/personas/clientes/${id}`, data)
export const eliminarCliente = (id) => del(`/personas/clientes/${id}`)

// Conductores
export const listarConductores = () => get('/personas/conductores')
export const miConductor = () => get('/personas/conductores/mio')
export const listarConductoresDisponibles = () => get('/personas/conductores/disponibles')
export const crearConductor = (data) => post('/personas/conductores', data)
export const actualizarConductor = (id, data) => put(`/personas/conductores/${id}`, data)
export const eliminarConductor = (id) => del(`/personas/conductores/${id}`)
