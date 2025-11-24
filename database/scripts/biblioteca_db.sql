-- ============================================
-- Script de Base de Datos: Biblioteca Inteligente
-- ============================================
-- Este script crea la base de datos completa para el sistema
-- de gestión de biblioteca con usuarios, libros y préstamos.
--
-- INSTRUCCIONES:
-- 1. Abre XAMPP y inicia Apache y MySQL
-- 2. Abre phpMyAdmin (http://localhost/phpmyadmin)
-- 3. Copia y pega este script completo
-- 4. Ejecuta el script
-- ============================================

-- Crear la base de datos
DROP DATABASE IF EXISTS biblioteca_db;
CREATE DATABASE biblioteca_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca_db;

-- ============================================
-- TABLA: usuarios
-- ============================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    tipo ENUM('ADMINISTRADOR', 'BIBLIOTECARIO', 'USUARIO') NOT NULL DEFAULT 'USUARIO',
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    fecha_registro DATE NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: libros
-- ============================================
CREATE TABLE libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    anio_publicacion INT,
    isbn VARCHAR(20) UNIQUE,
    editorial VARCHAR(150),
    INDEX idx_titulo (titulo),
    INDEX idx_autor (autor),
    INDEX idx_categoria (categoria),
    INDEX idx_isbn (isbn)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: prestamos
-- ============================================
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado ENUM('PENDIENTE', 'DEVUELTO', 'RETRASADO', 'RENOVADO') NOT NULL DEFAULT 'PENDIENTE',
    observaciones TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_libro) REFERENCES libros(id) ON DELETE RESTRICT,
    INDEX idx_usuario (id_usuario),
    INDEX idx_libro (id_libro),
    INDEX idx_estado (estado),
    INDEX idx_fecha_prestamo (fecha_prestamo)
) ENGINE=InnoDB;

-- ============================================
-- TRIGGERS: Control automático de stock
-- ============================================

-- Trigger: Decrementar stock al crear préstamo
DELIMITER $$
CREATE TRIGGER trg_prestamo_insert
AFTER INSERT ON prestamos
FOR EACH ROW
BEGIN
    UPDATE libros 
    SET stock = stock - 1 
    WHERE id = NEW.id_libro AND stock > 0;
END$$
DELIMITER ;

-- Trigger: Incrementar stock al devolver libro
DELIMITER $$
CREATE TRIGGER trg_prestamo_devolucion
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF OLD.estado != 'DEVUELTO' AND NEW.estado = 'DEVUELTO' THEN
        UPDATE libros 
        SET stock = stock + 1 
        WHERE id = NEW.id_libro;
    END IF;
END$$
DELIMITER ;

-- ============================================
-- PROCEDIMIENTO ALMACENADO: Actualizar préstamos retrasados
-- ============================================
DELIMITER $$
CREATE PROCEDURE sp_actualizar_prestamos_retrasados()
BEGIN
    DECLARE prestamos_actualizados INT;
    
    UPDATE prestamos 
    SET estado = 'RETRASADO'
    WHERE estado IN ('PENDIENTE', 'RENOVADO')
    AND fecha_devolucion_esperada < CURRENT_DATE
    AND fecha_devolucion_real IS NULL;
    
    SET prestamos_actualizados = ROW_COUNT();
    SELECT prestamos_actualizados;
END$$
DELIMITER ;

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre, apellido, tipo, email, telefono, direccion, fecha_registro, username, password, activo) VALUES
('Admin', 'Sistema', 'ADMINISTRADOR', 'admin@biblioteca.com', '555-0001', 'Calle Principal 123', CURDATE(), 'admin', 'admin123', TRUE),
('María', 'García', 'BIBLIOTECARIO', 'maria.garcia@biblioteca.com', '555-0002', 'Av. Central 456', CURDATE(), 'mgarcia', 'biblio123', TRUE),
('Juan', 'Pérez', 'USUARIO', 'juan.perez@email.com', '555-0003', 'Calle Secundaria 789', CURDATE(), 'jperez', 'user123', TRUE),
('Ana', 'Martínez', 'USUARIO', 'ana.martinez@email.com', '555-0004', 'Av. Norte 321', CURDATE(), 'amartinez', 'user123', TRUE);

-- Insertar libros de prueba
INSERT INTO libros (titulo, autor, categoria, stock, anio_publicacion, isbn, editorial) VALUES
('Cien Años de Soledad', 'Gabriel García Márquez', 'Ficción', 5, 1967, '978-0307474728', 'Editorial Sudamericana'),
('Don Quijote de la Mancha', 'Miguel de Cervantes', 'Clásicos', 3, 1605, '978-8420412146', 'Real Academia Española'),
('1984', 'George Orwell', 'Ciencia Ficción', 4, 1949, '978-0451524935', 'Secker & Warburg'),
('El Principito', 'Antoine de Saint-Exupéry', 'Infantil', 6, 1943, '978-0156012195', 'Reynal & Hitchcock'),
('Sapiens', 'Yuval Noah Harari', 'Historia', 2, 2011, '978-0062316097', 'Debate'),
('Clean Code', 'Robert C. Martin', 'Tecnología', 3, 2008, '978-0132350884', 'Prentice Hall'),
('El Alquimista', 'Paulo Coelho', 'Ficción', 4, 1988, '978-0062315007', 'HarperOne'),
('Orgullo y Prejuicio', 'Jane Austen', 'Clásicos', 2, 1813, '978-0141439518', 'Penguin Classics');

-- Insertar préstamos de prueba
INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion_esperada, estado, observaciones) VALUES
(3, 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'PENDIENTE', 'Primer préstamo'),
(4, 3, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'RETRASADO', 'Préstamo retrasado'),
(3, 5, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), CURDATE(), 'DEVUELTO', 'Devuelto a tiempo');

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT 'Base de datos creada exitosamente!' AS Mensaje;
SELECT COUNT(*) AS 'Total Usuarios' FROM usuarios;
SELECT COUNT(*) AS 'Total Libros' FROM libros;
SELECT COUNT(*) AS 'Total Préstamos' FROM prestamos;
