# 📚 Biblioteca Inteligente 1.0

Sistema completo de gestión de biblioteca desarrollado en Java con JavaFX y MySQL.

---

## 🎯 Descripción

**Biblioteca Inteligente 1.0** es un sistema de escritorio profesional para la gestión integral de bibliotecas. Permite administrar libros, usuarios y préstamos con una interfaz gráfica moderna e intuitiva.

---

## ✨ Características Principales

### 📚 Gestión de Libros
- CRUD completo (Crear, Leer, Actualizar, Eliminar)
- Búsqueda por título, autor y categoría
- Control de stock e inventario
- Validación de ISBN
- Alertas de stock bajo

### 👥 Gestión de Usuarios
- Sistema de roles (Administrador, Bibliotecario, Lector)
- Registro y gestión de usuarios
- Activación/desactivación de cuentas
- Validación de emails y usernames únicos

### 📋 Gestión de Préstamos
- Registro de préstamos y devoluciones
- Control automático de fechas
- Alertas de préstamos retrasados
- Renovación de préstamos
- Histórico completo

### 📄 Reportes
- Reporte de inventario de libros
- Reporte de usuarios activos
- Reporte de préstamos activos
- Reporte de préstamos retrasados

### 🔐 Seguridad
- Sistema de autenticación
- Control de acceso por roles
- Validación de datos

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 17+ | Lenguaje de programación |
| JavaFX | 21.0.1 | Interfaz gráfica |
| MySQL | 8.0+ | Base de datos |
| JDBC | - | Conectividad con BD |
| Maven | - | Gestión de dependencias |

---

## 📋 Requisitos Previos

### Software Necesario
1. **JDK 17 o superior**
    - Descargar de: https://www.oracle.com/java/technologies/downloads/

2. **IntelliJ IDEA Community** (o cualquier IDE Java)
    - Descargar de: https://www.jetbrains.com/idea/download/

3. **MySQL 8.0 o superior**
    - Descargar de: https://dev.mysql.com/downloads/mysql/

4. **MySQL Workbench** (recomendado)
    - Descargar de: https://dev.mysql.com/downloads/workbench/

---

## 🚀 Instalación y Configuración

### Paso 1: Clonar o Descargar el Proyecto

```bash
# Si tienes el proyecto en un repositorio
git clone [URL_DEL_REPOSITORIO]

# O simplemente descomprime el archivo ZIP
```

### Paso 2: Configurar la Base de Datos

1. **Abrir MySQL Workbench**

2. **Conectarse al servidor MySQL local**

3. **Ejecutar los scripts SQL en orden:**
    - Abrir `database/scripts/01_crear_base_datos.sql`
    - Ejecutar todo el script (esto crea la base de datos)
    - Abrir `database/scripts/02_crear_tablas.sql`
    - Ejecutar todo el script (esto crea las tablas)
    - Abrir `database/scripts/03_datos_ejemplo.sql`
    - Ejecutar todo el script (esto inserta datos de prueba)

### Paso 3: Configurar la Conexión en el Proyecto

1. **Abrir el proyecto en IntelliJ IDEA**

2. **Editar el archivo** `src/main/java/com/biblioteca/util/DatabaseConnection.java`

3. **Actualizar las credenciales de MySQL:**
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db";
   private static final String USUARIO = "root";
   private static final String PASSWORD = "TU_PASSWORD_AQUI"; // ⬅️ Cambiar esto
   ```

### Paso 4: Cargar Dependencias Maven

1. En IntelliJ, click derecho en el proyecto
2. Seleccionar **Maven** → **Reload Project**
3. Esperar a que descargue todas las dependencias

### Paso 5: Compilar y Ejecutar

1. **Compilar el proyecto:**
    - Presionar `Ctrl + F9` (Windows/Linux) o `Cmd + F9` (Mac)

2. **Ejecutar la aplicación:**
    - Click derecho en `Main.java`
    - Seleccionar `Run 'Main.main()'`

---

## 👤 Usuarios de Prueba

| Usuario | Contraseña | Rol | Descripción |
|---------|-----------|-----|-------------|
| admin | admin123 | Administrador | Acceso total al sistema |
| mgonzalez | biblio123 | Bibliotecario | Gestión de libros y préstamos |
| cramirez | biblio123 | Bibliotecario | Gestión de libros y préstamos |
| amartinez | lector123 | Lector | Solo consulta de préstamos |

---

## 📁 Estructura del Proyecto

```
BibliotecaInteligente/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── biblioteca/
│   │   │           ├── modelo/              # Clases de entidad
│   │   │           │   ├── Libro.java
│   │   │           │   ├── Usuario.java
│   │   │           │   └── Prestamo.java
│   │   │           ├── controlador/         # Lógica de negocio (DAO)
│   │   │           │   ├── LibroDAO.java
│   │   │           │   ├── UsuarioDAO.java
│   │   │           │   └── PrestamoDAO.java
│   │   │           ├── vista/               # Controladores de vistas
│   │   │           │   ├── LoginController.java
│   │   │           │   ├── DashboardController.java
│   │   │           │   ├── LibrosController.java
│   │   │           │   ├── UsuariosController.java
│   │   │           │   ├── PrestamosController.java
│   │   │           │   └── ReportesController.java
│   │   │           ├── util/                # Utilidades
│   │   │           │   └── DatabaseConnection.java
│   │   │           └── Main.java            # Clase principal
│   │   └── resources/
│   │       ├── fxml/                        # Vistas JavaFX
│   │       │   ├── Login.fxml
│   │       │   ├── Dashboard.fxml
│   │       │   ├── Libros.fxml
│   │       │   ├── Usuarios.fxml
│   │       │   └── Prestamos.fxml
│   │       └── css/
│   │           └── styles.css               # Estilos
├── database/
│   └── scripts/                             # Scripts SQL
│       ├── 01_crear_base_datos.sql
│       ├── 02_crear_tablas.sql
│       └── 03_datos_ejemplo.sql
├── pom.xml                                  # Dependencias Maven
└── README.md                                # Este archivo
```

---

## 🎨 Capturas de Pantalla

### Pantalla de Login
Sistema de autenticación con roles diferenciados.

### Dashboard Principal
Panel de control con estadísticas en tiempo real.

### Gestión de Libros
CRUD completo con búsqueda y filtros avanzados.

### Gestión de Usuarios
Administración de usuarios con control de roles.

### Gestión de Préstamos
Control de préstamos con alertas de retrasos.

---

## 🔧 Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"
**Solución:** Verifica que la contraseña en `DatabaseConnection.java` sea correcta.

### Error: "Unknown database 'biblioteca_db'"
**Solución:** Ejecuta el script `01_crear_base_datos.sql` en MySQL Workbench.

### Error: "No suitable driver found"
**Solución:**
1. Click derecho en el proyecto
2. Maven → Reload Project
3. Espera a que descargue las dependencias

### La interfaz no se muestra correctamente
**Solución:** Verifica que JavaFX esté correctamente configurado en el `pom.xml`.

---

## 📚 Funcionalidades por Rol

### 🔴 Administrador
- ✅ Acceso completo a todos los módulos
- ✅ Gestión de usuarios (crear, editar, eliminar)
- ✅ Gestión de libros
- ✅ Gestión de préstamos
- ✅ Generación de reportes

### 🔵 Bibliotecario
- ✅ Gestión de libros
- ✅ Gestión de préstamos
- ✅ Consulta de usuarios
- ✅ Generación de reportes
- ❌ No puede gestionar usuarios

### 🟢 Lector
- ✅ Consulta de sus propios préstamos
- ❌ Sin acceso a otras funcionalidades

---

## 🎯 Patrones de Diseño Implementados

### 1. Patrón MVC (Modelo-Vista-Controlador)
- **Modelo:** Clases de entidad (`Libro`, `Usuario`, `Prestamo`)
- **Vista:** Archivos FXML y controladores de vista
- **Controlador:** Clases DAO con lógica de negocio

### 2. Patrón Singleton
- Implementado en `DatabaseConnection` para gestionar una única instancia de conexión

### 3. Patrón DAO (Data Access Object)
- Separación de la lógica de acceso a datos en clases dedicadas

---

## 🔐 Seguridad

### Medidas Implementadas
- ✅ PreparedStatement para prevenir inyección SQL
- ✅ Validación de entrada de datos
- ✅ Control de acceso por roles
- ✅ Validación de credenciales

### Recomendaciones para Producción
- 🔒 Implementar encriptación de contraseñas (BCrypt, SHA-256)
- 🔒 Agregar sistema de sesiones con timeout
- 🔒 Implementar logs de auditoría
- 🔒 Configurar conexiones SSL para MySQL

---

## 📖 Uso del Sistema

### Iniciar Sesión
1. Ejecutar la aplicación
2. Ingresar usuario y contraseña
3. Click en "Iniciar Sesión"

### Agregar un Libro
1. Ir a "Gestión de Libros"
2. Click en "➕ Nuevo Libro"
3. Llenar el formulario
4. Click en "Guardar"

### Registrar un Préstamo
1. Ir a "Gestión de Préstamos"
2. Click en "➕ Nuevo Préstamo"
3. Seleccionar usuario y libro
4. Configurar días del préstamo
5. Click en "Registrar Préstamo"

### Registrar una Devolución
1. Ir a "Gestión de Préstamos"
2. Seleccionar el préstamo
3. Click en "✅ Registrar Devolución"
4. Confirmar

### Generar Reportes
1. Ir a "Reportes"
2. Seleccionar el tipo de reporte
3. Click en "📄 Generar PDF"
4. El reporte se guardará en el escritorio

---

## 🤝 Contribuciones

Este proyecto fue desarrollado como sistema educativo. Si deseas contribuir:

1. Fork el proyecto
2. Crea una rama para tu funcionalidad
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

## 👨‍💻 Autor

**Biblioteca Inteligente Team**
- Proyecto: Sistema de Gestión de Biblioteca
- Versión: 1.0
- Año: 2025

---

## 📧 Soporte

Si tienes preguntas o problemas:
1. Revisa la sección de "Solución de Problemas"
2. Verifica que todos los pasos de instalación se hayan completado
3. Asegúrate de que MySQL esté corriendo

---

## ✅ Checklist de Instalación

- [ ] JDK 17+ instalado
- [ ] MySQL instalado y corriendo
- [ ] MySQL Workbench instalado
- [ ] Scripts SQL ejecutados correctamente
- [ ] Base de datos `biblioteca_db` creada
- [ ] Tablas creadas (usuarios, libros, prestamos)
- [ ] Datos de ejemplo insertados
- [ ] Contraseña configurada en `DatabaseConnection.java`
- [ ] Dependencias Maven descargadas
- [ ] Proyecto compilado sin errores
- [ ] Aplicación ejecuta correctamente
- [ ] Login funciona con usuarios de prueba

---

## 🎉 ¡Listo!

Si completaste todos los pasos, tu sistema **Biblioteca Inteligente 1.0** debería estar funcionando correctamente.

**¡Disfruta gestionando tu biblioteca!** 📚✨