# LogiChat

Sistema de gestión de logística y reparto basado en **microservicios Java (Spring Boot)** con frontend **React + Vite**. A pesar del nombre, no es un sistema de mensajería: gestiona clientes, conductores, vehículos, pedidos y entregas con autenticación JWT y control de acceso por roles.

## Arquitectura

```
┌──────────────┐      ┌──────────────────┐
│  React + Vite│─────►│   Gateway (8080) │
│  (puerto 5173)│      │  Spring Cloud GW │
└──────────────┘      └────────┬─────────┘
                               │
        ┌──────────┬───────────┼───────────┬──────────┐
        ▼          ▼           ▼           ▼          ▼
   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
   │ ms-auth │ │ms-person│ │ms-opera │ │Eureka   │
   │         │ │   8091  │ │   8090  │ │  8761   │
   │ auth_db │ │personas │ │operaciones│ │registro │
   └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

| Servicio | Puerto | Base de datos | Responsabilidad |
|----------|--------|---------------|-----------------|
| Servidor-Eureka | 8761 | — | Descubrimiento de servicios |
| Gateway | 8080 | — | Enrutamiento `/auth`, `/personas`, `/operaciones`, JWT, CORS |
| ms-auth | — | `auth_db` | Usuarios, roles (ADMIN, COORDINADOR, CONDUCTOR, CLIENTE), BCrypt |
| ms-personas | 8091 | `personas_db` | Clientes y conductores |
| ms-operaciones | 8090 | `operaciones_db` | Vehículos, pedidos y entregas |

La comunicación entre servicios usa **Feign clients**. Las referencias entre bases de datos no tienen claves foráneas cruzadas (se validan por Feign).

## Roles y permisos (frontend)

| Rol | Módulos visibles |
|-----|------------------|
| ADMIN | Todos |
| COORDINADOR | Todos menos usuarios |
| CLIENTE | Pedidos |
| CONDUCTOR | Entregas |

## Tecnologías

### Backend
- Java 17+
- Spring Boot
- Spring Cloud Gateway (API Gateway)
- Netflix Eureka (servicio de descubrimiento)
- Spring Cloud OpenFeign (comunicación entre microservicios)
- Spring Data JPA + Hibernate
- MySQL 8 (`auth_db`, `personas_db`, `operaciones_db`)
- JWT (autenticación) + BCrypt (contraseñas)
- Maven (mvnw)

### Frontend
- React 19
- Vite
- Bootstrap 5.3.8
- ESLint

### Infraestructura
- Node.js + npm
- MySQL 8+

## Requisitos previos

- Java 17+
- Maven (o `./mvnw`)
- MySQL 8+
- Node.js 18+ y npm

## Bases de datos

Ejecutar en este orden en MySQL:

```bash
mysql -u root -p < database/03-auth.sql
mysql -u root -p < database/01-personas.sql
mysql -u root -p < database/02-operaciones.sql
```

Esto crea `auth_db`, `personas_db` y `operaciones_db`, siembra roles y 5 vehículos de prueba.

## Cómo ejecutar

1. **Eureka**: `cd backend/Servidor-Eureka && ./mvnw spring-boot:run`
2. **Microservicios** (en terminales separados): `ms-auth`, `ms-personas`, `ms-operaciones`
3. **Gateway**: `./mvnw spring-boot:run` (puerto 8080)
4. **Frontend**: `cd frontend && npm install && npm run dev` → `http://localhost:5173`

El frontend apunta a `http://localhost:8080` (`VITE_API` en `.env`).

## Estructura del repositorio

```
backend/
  Servidor-Eureka/      # Service discovery
  Gateway/              # API Gateway + JWT + CORS
  ms-auth/              # Identidad y seguridad
  ms-personas/          # Clientes y conductores
  ms-operaciones/       # Vehículos, pedidos, entregas
database/
  01-personas.sql
  02-operaciones.sql
  03-auth.sql
frontend/
  src/
    api/                # client.js, operaciones.js, personas.js
    components/         # Login, Usuarios, Clientes, Conductores, Vehiculos, Pedidos, Entregas
    App.jsx
```

## Variables de entorno clave

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (por servicio)
- `JWT_SECRET`, `JWT_EXPIRATION`
- `GATEWAY_SECURITY_ENABLED`
- `VITE_API` (frontend)
