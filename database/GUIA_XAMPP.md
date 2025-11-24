# Guía: Configurar Biblioteca Inteligente con XAMPP

## 📋 Requisitos Previos
- XAMPP instalado en tu computadora
- Proyecto BibliotecaInteligente descargado

## 🚀 Paso 1: Iniciar XAMPP

1. Abre el **Panel de Control de XAMPP**
2. Inicia los siguientes módulos:
   - ✅ **Apache** (servidor web)
   - ✅ **MySQL** (base de datos)

## 💾 Paso 2: Crear la Base de Datos

### Opción A: Usando phpMyAdmin (Recomendado)

1. Abre tu navegador y ve a: **http://localhost/phpmyadmin**
2. Haz clic en la pestaña **"SQL"** en el menú superior
3. Abre el archivo `database/scripts/biblioteca_db.sql` con un editor de texto
4. Copia **TODO** el contenido del archivo
5. Pégalo en el área de texto de phpMyAdmin
6. Haz clic en el botón **"Continuar"** o **"Go"**
7. Deberías ver mensajes de éxito indicando que se crearon las tablas

### Opción B: Usando línea de comandos

```bash
# Navega a la carpeta de XAMPP MySQL
cd C:\xampp\mysql\bin

# Ejecuta el script
mysql -u root < "C:\Users\rogel\OneDrive\Escritorio\ProyJava\BibliotecaInteligente\database\scripts\biblioteca_db.sql"
```

## ⚙️ Paso 3: Configurar la Contraseña de MySQL

Por defecto, XAMPP usa:
- **Usuario**: `root`
- **Contraseña**: `` (vacía, sin contraseña)

### Si tu XAMPP tiene contraseña vacía:
No necesitas cambiar nada en el código.

### Si configuraste una contraseña en XAMPP:
Edita el archivo `DatabaseConnection.java` y cambia la línea 23 con tu contraseña de MySQL.

## ✅ Paso 4: Verificar la Instalación

1. En phpMyAdmin, selecciona la base de datos **`biblioteca_db`** en el panel izquierdo
2. Deberías ver 3 tablas:
   - `usuarios` (4 registros)
   - `libros` (8 registros)
   - `prestamos` (3 registros)

## 🎯 Paso 5: Ejecutar la Aplicación

1. Abre el proyecto en tu IDE (IntelliJ IDEA, Eclipse, etc.)
2. Ejecuta la clase principal de la aplicación
3. Usa estas credenciales para probar:

### Usuarios de Prueba:

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | Administrador |
| `mgarcia` | `biblio123` | Bibliotecario |
| `jperez` | `user123` | Usuario |
| `amartinez` | `user123` | Usuario |

## 🔧 Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"
- Verifica que MySQL esté iniciado en XAMPP
- Verifica la contraseña en `DatabaseConnection.java`

### Error: "Unknown database 'biblioteca_db'"
- Asegúrate de haber ejecutado el script SQL completo
- Verifica en phpMyAdmin que la base de datos exista

### Error: "Communications link failure"
- Verifica que MySQL esté corriendo en el puerto 3306
- Revisa que no haya otro servicio usando el puerto 3306

## 📊 Estructura de la Base de Datos

### Tabla: usuarios
- Almacena información de usuarios del sistema
- Tipos: ADMINISTRADOR, BIBLIOTECARIO, USUARIO

### Tabla: libros
- Catálogo de libros disponibles
- Control automático de stock mediante triggers

### Tabla: prestamos
- Registro de préstamos de libros
- Estados: PENDIENTE, DEVUELTO, RETRASADO, RENOVADO
- Triggers automáticos para actualizar stock

## 🎉 ¡Listo!
Tu aplicación Biblioteca Inteligente está configurada y lista para usar con XAMPP.
