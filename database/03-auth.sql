-- LogiChat microservicios: ms-auth (identidad y seguridad).
-- Dueño de rol + usuario. Claves: se siembran con BCrypt desde DataInitializer,
-- aquí solo DDL + catálogo de roles.
-- Ejecutar: mysql -u root -p < 03-auth.sql
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS auth_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE auth_db;

-- 1. ROL (catálogo, creado por el profe como tabla)
CREATE TABLE IF NOT EXISTS rol (
    id_rol BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_rol),
    UNIQUE KEY uk_rol_nombre (nombre)
) ENGINE=InnoDB;

INSERT INTO rol (nombre)
SELECT * FROM (
    SELECT 'ADMIN' UNION ALL
    SELECT 'COORDINADOR' UNION ALL
    SELECT 'CONDUCTOR' UNION ALL
    SELECT 'CLIENTE'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM rol);

-- 2. USUARIO (vive solo aquí; personas/operaciones lo referencian por id sin FK)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    id_rol BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuario_email (email),
    KEY idx_usuario_rol (id_rol),
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
        ON DELETE RESTRICT
) ENGINE=InnoDB;
