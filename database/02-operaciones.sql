-- LogiChat microservicios: ms-operaciones (sin chat).
-- Referencias a personas (id_cliente, id_conductor) SIN FK: se validan por Feign.
-- Ejecutar: mysql -u root -p < 02-operaciones.sql
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS operaciones_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE operaciones_db;

-- 1. VEHICULO (entidad local)
CREATE TABLE IF NOT EXISTS vehiculo (
    id_vehiculo BIGINT NOT NULL AUTO_INCREMENT,
    placa VARCHAR(20) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    estado ENUM('DISPONIBLE','EN_USO') NOT NULL DEFAULT 'DISPONIBLE',
    PRIMARY KEY (id_vehiculo),
    UNIQUE KEY uk_vehiculo_placa (placa),
    KEY idx_vehiculo_estado (estado)
) ENGINE=InnoDB;

-- 2. PEDIDO (id_cliente sin FK: vive en personas_db, se valida por Feign)
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    direccion_entrega VARCHAR(250) NOT NULL,
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id_pedido),
    KEY idx_pedido_cliente (id_cliente),
    KEY idx_pedido_estado (estado)
) ENGINE=InnoDB;

-- 3. ENTREGA (id_conductor sin FK: vive en personas_db, se valida por Feign)
CREATE TABLE IF NOT EXISTS entrega (
    id_entrega BIGINT NOT NULL AUTO_INCREMENT,
    id_pedido BIGINT NOT NULL,
    id_conductor BIGINT NULL,
    id_vehiculo BIGINT NULL,
    fecha_asignacion DATETIME NULL,
    fecha_salida DATETIME NULL,
    fecha_entrega DATETIME NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id_entrega),
    UNIQUE KEY uk_entrega_pedido (id_pedido),
    KEY idx_entrega_conductor (id_conductor),
    KEY idx_entrega_vehiculo (id_vehiculo),
    KEY idx_entrega_estado (estado),
    CONSTRAINT fk_entrega_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido)
        ON DELETE RESTRICT,
    CONSTRAINT fk_entrega_vehiculo
        FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo(id_vehiculo)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- 4. INSERTAR 5 VEHICULOS DE PRUEBA

INSERT INTO vehiculo (placa, tipo, marca, modelo, estado)
VALUES
('ABC-123', 'CAMION', 'Volvo', 'FH16', 'DISPONIBLE'),

('DEF-456', 'CAMIONETA', 'Toyota', 'Hilux', 'DISPONIBLE'),

('GHI-789', 'FURGONETA', 'Hyundai', 'H350', 'EN_USO'),

('JKL-321', 'CAMION', 'Mercedes-Benz', 'Actros', 'DISPONIBLE'),

('MNO-654', 'CAMIONETA', 'Nissan', 'Frontier', 'EN_USO');
