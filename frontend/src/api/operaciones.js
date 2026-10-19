import { get, post, put, del } from './client'

// Vehículos
export const listarVehiculos = () => get('/operaciones/vehiculos')
export const crearVehiculo = (data) => post('/operaciones/vehiculos', data)
export const actualizarVehiculo = (id, data) => put(`/operaciones/vehiculos/${id}`, data)
export const eliminarVehiculo = (id) => del(`/operaciones/vehiculos/${id}`)

// Pedidos (crear valida cliente por Feign en ms-personas)
export const listarPedidos = () => get('/operaciones/pedidos')
export const crearPedido = (data) => post('/operaciones/pedidos', data)
export const estadoPedido = (id, estado) => put(`/operaciones/pedidos/${id}/estado`, { estado })
export const eliminarPedido = (id) => del(`/operaciones/pedidos/${id}`)

// Entregas (asignar valida conductor por Feign)
export const listarEntregas = () => get('/operaciones/entregas')
export const crearEntrega = (pedidoId) => post('/operaciones/entregas', { pedidoId })
export const asignarEntrega = (id, conductorId, vehiculoId) =>
  put(`/operaciones/entregas/${id}/asignar`, { conductorId, vehiculoId })
export const estadoEntrega = (id, estado) => put(`/operaciones/entregas/${id}/estado`, { estado })
export const eliminarEntrega = (id) => del(`/operaciones/entregas/${id}`)
