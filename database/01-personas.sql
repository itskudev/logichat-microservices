-- LogiChat microservicios: ms-personas (sin chat).
-- El dueño de usuario+rol es ms-auth/auth_db. Aquí id_usuario es referencia
-- SIN FK: se valida por Feign contra ms-auth.
-- Ejecutar: mysql -u root -p < 01-personas.sql
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS personas_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE personas_db;

-- 1. CLIENTE (id_usuario sin FK: vive en auth_db, se valida por Feign)
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(250),
    PRIMARY KEY (id_cliente),
    UNIQUE KEY uk_cliente_usuario (id_usuario),
    KEY idx_cliente_usuario (id_usuario)
) ENGINE=InnoDB;

-- 2. CONDUCTOR (id_usuario sin FK: vive en auth_db, se valida por Feign)
CREATE TABLE IF NOT EXISTS conductor (
    id_conductor BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    licencia VARCHAR(50) NOT NULL,
    telefono VARCHAR(20),
    estado ENUM('DISPONIBLE','OCUPADO') NOT NULL DEFAULT 'DISPONIBLE',
    PRIMARY KEY (id_conductor),
    UNIQUE KEY uk_conductor_usuario (id_usuario),
    UNIQUE KEY uk_conductor_licencia (licencia),
    KEY idx_conductor_estado (estado)
) ENGINE=InnoDB;
