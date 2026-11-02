-- ============================================================================
-- SISTEMA DE GESTION DE BIBLIOTECA - ESQUEMA INTEGRADO
-- Modulos: USUARIO, LIBRO y PRESTAMO
-- Motor: MySQL 8.x / InnoDB
--
-- Este archivo crea las 6 tablas necesarias para los 3 modulos:
--  USUARIO  -> roles, usuarios
--  LIBRO    -> categorias, libros
--  PRESTAMO -> lectores, prestamos (relacionada con libros y usuarios)
--
-- IMPORTANTE:
--  1. Pensado para una instalacion nueva. CREATE TABLE IF NOT EXISTS NO
--     modifica tablas ya existentes ni migra columnas anteriores.
--  2. NO BORRA datos ni incluye contrasenas ni usuarios de demostracion.
--  3. Para la base local bd_biblioteca ya creada, NO necesitas reejecutarlo.
--  4. La rama feature/libro tiene nombres de atributos diferentes a este
--     esquema (id/stock/categoria/editorial vs. id_libro/
--     ejemplares_disponibles/id_categoria). Hay que unificar el modelo Java
--     antes de fusionar los modulos; el SQL no corrige ese desajuste.
--  5. El control automatico del stock al prestar/devolver debe implementarse
--     en la logica de negocio, idealmente con transacciones.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS bd_biblioteca
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bd_biblioteca;

-- ======================= MODULO USUARIO ======================================
-- Un usuario (bibliotecario) pertenece a un rol.
CREATE TABLE IF NOT EXISTS roles (
    id_rol INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_rol),
    CONSTRAINT uq_roles_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_email UNIQUE (email),
    KEY idx_usuarios_rol (id_rol),
    CONSTRAINT fk_usuarios_roles
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================== MODULO LIBRO =======================================
-- Un libro pertenece a una categoria.
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS libros (
    id_libro INT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    ejemplares_disponibles INT NOT NULL DEFAULT 1,
    id_categoria INT NOT NULL,
    PRIMARY KEY (id_libro),
    CONSTRAINT uq_libros_isbn UNIQUE (isbn),
    KEY idx_libros_categoria (id_categoria),
    CONSTRAINT fk_libros_categorias
        FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_libros_ejemplares CHECK (ejemplares_disponibles >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================= MODULO PRESTAMO =====================================
-- Lector: persona que recibe el libro prestado.
CREATE TABLE IF NOT EXISTS lectores (
    id_lector INT NOT NULL AUTO_INCREMENT,
    dni VARCHAR(15) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    estado ENUM('ACTIVO', 'SANCIONADO') NOT NULL DEFAULT 'ACTIVO',
    PRIMARY KEY (id_lector),
    CONSTRAINT uq_lectores_dni UNIQUE (dni),
    CONSTRAINT uq_lectores_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Prestamo: se vincula al lector, libro y usuario que registro la operacion.
CREATE TABLE IF NOT EXISTS prestamos (
    id_prestamo INT NOT NULL AUTO_INCREMENT,
    id_lector INT NOT NULL,
    id_libro INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_pactada DATE NOT NULL,
    fecha_devolucion_real DATE DEFAULT NULL,
    estado ENUM('PENDIENTE', 'DEVUELTO') NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id_prestamo),
    KEY idx_prestamos_lector_libro_estado (id_lector, id_libro, estado),
    KEY idx_prestamos_libro (id_libro),
    KEY idx_prestamos_usuario (id_usuario),
    CONSTRAINT fk_prestamos_lectores
        FOREIGN KEY (id_lector) REFERENCES lectores (id_lector)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_prestamos_libros
        FOREIGN KEY (id_libro) REFERENCES libros (id_libro)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_prestamos_usuarios
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_prestamos_fechas
        CHECK (fecha_devolucion_pactada >= fecha_prestamo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================== VERIFICACION ====================================
-- Consultas de solo lectura para revisar la creacion del esquema.
SHOW TABLES;
-- DESCRIBE usuarios;
-- DESCRIBE libros;
-- DESCRIBE prestamos;
-- SELECT * FROM prestamos;
