-- 1. Base de datos
CREATE DATABASE IF NOT EXISTS bd_biblioteca;
USE bd_biblioteca;

-- 2. Tabla Roles (Compartida)
CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Tabla Usuarios / Bibliotecarios (Compartida)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- 4. Tabla Categorías de Libros (Módulo Integrante A)
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- 5. Tabla Libros (Módulo Integrante A)
CREATE TABLE IF NOT EXISTS libros (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    ejemplares_disponibles INT NOT NULL DEFAULT 1,
    id_categoria INT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- 6. Tabla Lectores / Estudiantes (Módulo Integrante B)
CREATE TABLE IF NOT EXISTS lectores (
    id_lector INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    estado ENUM('ACTIVO', 'SANCIONADO') DEFAULT 'ACTIVO'
);

-- 7. Tabla Préstamos (Módulo Integrante C)
CREATE TABLE IF NOT EXISTS prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_lector INT NOT NULL,
    id_libro INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_pactada DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado ENUM('PENDIENTE', 'DEVUELTO') DEFAULT 'PENDIENTE',
    FOREIGN KEY (id_lector) REFERENCES lectores(id_lector),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Datos iniciales de prueba
INSERT INTO roles (nombre) VALUES ('ADMINISTRADOR'), ('BIBLIOTECARIO');

INSERT INTO usuarios (nombre, apellido, email, password, id_rol) 
VALUES ('Omar', 'Pérez', 'admin@biblioteca.com', '123456', 1);

INSERT INTO categorias (nombre, descripcion) 
VALUES ('Programación', 'Libros de desarrollo web y software'),
       ('Bases de Datos', 'Modelado y administración SQL/NoSQL'),
       ('Redes', 'Infraestructura y telecomunicaciones');

INSERT INTO libros (isbn, titulo, autor, ejemplares_disponibles, id_categoria) 
VALUES ('978-0134685991', 'Effective Java', 'Joshua Bloch', 5, 1),
       ('978-0596009205', 'Head First Java', 'Kathy Sierra', 3, 1),
       ('978-0133970777', 'Fundamentals of Database Systems', 'Ramez Elmasri', 4, 2);

INSERT INTO lectores (dni, nombre, apellido, email, telefono, estado) 
VALUES 
('74859632', 'Carlos', 'Mendoza', 'cmendoza@correo.com', '987123456', 'ACTIVO'),
('71234568', 'Lucía', 'Torres', 'ltorres@correo.com', '912345678', 'ACTIVO');