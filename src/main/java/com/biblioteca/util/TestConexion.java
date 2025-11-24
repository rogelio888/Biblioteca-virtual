package com.biblioteca.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Clase de prueba para verificar la conexión a la base de datos
 * Esta clase es solo para pruebas y puede eliminarse después
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 */
public class TestConexion {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  PRUEBA DE CONEXIÓN A BASE DE DATOS");
        System.out.println("===========================================\n");

        DatabaseConnection db = DatabaseConnection.getInstance();

        try {
            // Probar conexión
            System.out.println("1. Probando conexión...");
            boolean conectado = db.probarConexion();

            if (conectado) {
                System.out.println("\n2. Obteniendo datos de prueba...\n");

                Connection conn = db.conectar();
                Statement stmt = conn.createStatement();

                // Contar usuarios
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM usuarios");
                if (rs.next()) {
                    System.out.println("   ✓ Total de usuarios: " + rs.getInt("total"));
                }
                rs.close();

                // Contar libros
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM libros");
                if (rs.next()) {
                    System.out.println("   ✓ Total de libros: " + rs.getInt("total"));
                }
                rs.close();

                // Contar préstamos
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM prestamos");
                if (rs.next()) {
                    System.out.println("   ✓ Total de préstamos: " + rs.getInt("total"));
                }
                rs.close();

                // Mostrar un usuario de ejemplo
                System.out.println("\n3. Usuario de ejemplo:");
                rs = stmt.executeQuery(
                        "SELECT nombre, apellido, tipo, email FROM usuarios WHERE tipo = 'ADMINISTRADOR' LIMIT 1"
                );
                if (rs.next()) {
                    System.out.println("   - Nombre: " + rs.getString("nombre") + " " + rs.getString("apellido"));
                    System.out.println("   - Tipo: " + rs.getString("tipo"));
                    System.out.println("   - Email: " + rs.getString("email"));
                }
                rs.close();

                // Mostrar un libro de ejemplo
                System.out.println("\n4. Libro de ejemplo:");
                rs = stmt.executeQuery(
                        "SELECT titulo, autor, categoria, stock FROM libros LIMIT 1"
                );
                if (rs.next()) {
                    System.out.println("   - Título: " + rs.getString("titulo"));
                    System.out.println("   - Autor: " + rs.getString("autor"));
                    System.out.println("   - Categoría: " + rs.getString("categoria"));
                    System.out.println("   - Stock: " + rs.getInt("stock"));
                }
                rs.close();

                stmt.close();

                System.out.println("\n===========================================");
                System.out.println("  ✓ CONEXIÓN EXITOSA - TODO FUNCIONA BIEN");
                System.out.println("===========================================");

            } else {
                System.err.println("\n✗ Error: No se pudo establecer conexión");
                System.err.println("Verifica:");
                System.err.println("  1. MySQL está corriendo");
                System.err.println("  2. La contraseña en DatabaseConnection.java es correcta");
                System.err.println("  3. La base de datos 'biblioteca_db' existe");
            }

        } catch (Exception e) {
            System.err.println("\n✗ Error durante la prueba:");
            e.printStackTrace();
        } finally {
            db.desconectar();
        }
    }
}