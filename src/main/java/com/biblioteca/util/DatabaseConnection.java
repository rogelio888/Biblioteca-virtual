package com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase singleton para gestionar la conexión a la base de datos MySQL
 *
 * Esta clase implementa el patrón Singleton para garantizar una única
 * instancia de conexión a la base de datos en toda la aplicación.
 * Maneja la conexión, desconexión y gestión de errores.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class DatabaseConnection {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "20012005"; // ⬅️ CAMBIAR AQUÍ TU CONTRASEÑA DE MYSQL
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Instancia única (Singleton)
    private static DatabaseConnection instancia;
    private Connection conexion;

    /**
     * Constructor privado para implementar el patrón Singleton
     * Inicializa el driver de MySQL
     */
    private DatabaseConnection() {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);
            System.out.println("✓ Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error al cargar el driver MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la única instancia de DatabaseConnection (Singleton)
     *
     * @return Instancia única de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instancia == null) {
            synchronized (DatabaseConnection.class) {
                if (instancia == null) {
                    instancia = new DatabaseConnection();
                }
            }
        }
        return instancia;
    }

    /**
     * Establece y retorna una conexión a la base de datos
     *
     * @return Objeto Connection activo
     * @throws SQLException Si hay error al conectar
     */
    public Connection conectar() throws SQLException {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("✓ Conexión establecida con la base de datos");
            }
            return conexion;
        } catch (SQLException e) {
            System.err.println("✗ Error al conectar con la base de datos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✓ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica si la conexión está activa
     *
     * @return true si la conexión está activa, false en caso contrario
     */
    public boolean estaConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtiene la conexión actual sin crear una nueva
     *
     * @return Conexión actual o null si no existe
     */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Prueba la conexión a la base de datos
     * Útil para verificar la configuración
     *
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public boolean probarConexion() {
        try {
            Connection conn = conectar();
            boolean valida = conn != null && !conn.isClosed();
            if (valida) {
                System.out.println("✓ Prueba de conexión exitosa");
                System.out.println("  - Base de datos: " + conn.getCatalog());
                System.out.println("  - Usuario: " + USUARIO);
                System.out.println("  - URL: " + URL);
            }
            return valida;
        } catch (SQLException e) {
            System.err.println("✗ Prueba de conexión fallida: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información de la conexión para propósitos de depuración
     *
     * @return String con información de la conexión
     */
    public String getInfoConexion() {
        try {
            if (estaConectado()) {
                return String.format(
                        "Conexión activa - Base de datos: %s, Usuario: %s",
                        conexion.getCatalog(),
                        USUARIO
                );
            } else {
                return "No hay conexión activa";
            }
        } catch (SQLException e) {
            return "Error al obtener información de conexión: " + e.getMessage();
        }
    }
}