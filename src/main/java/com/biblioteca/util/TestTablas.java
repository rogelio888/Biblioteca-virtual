package com.biblioteca.util;

import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.controlador.PrestamoDAO;
import com.biblioteca.modelo.Usuario;
import com.biblioteca.modelo.Prestamo;

import java.util.List;

/**
 * Clase de prueba para verificar que los datos se obtienen correctamente
 * Ejecutar este archivo para ver si hay datos en la base de datos
 */
public class TestTablas {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  PRUEBA DE OBTENCIÓN DE DATOS");
        System.out.println("===========================================\n");
        
        // Probar usuarios
        System.out.println("1. Probando UsuarioDAO...");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        
        System.out.println("   Total usuarios: " + usuarios.size());
        if (!usuarios.isEmpty()) {
            System.out.println("   Primer usuario: " + usuarios.get(0).getNombreCompleto());
            System.out.println("   Detalles completos:");
            Usuario u = usuarios.get(0);
            System.out.println("   - ID: " + u.getId());
            System.out.println("   - Nombre: " + u.getNombre());
            System.out.println("   - Apellido: " + u.getApellido());
            System.out.println("   - Tipo: " + u.getTipo());
            System.out.println("   - Email: " + u.getEmail());
            System.out.println("   - Username: " + u.getUsername());
            System.out.println("   - Activo: " + u.isActivo());
        } else {
            System.out.println("   ⚠ No hay usuarios en la base de datos");
        }
        
        System.out.println("\n2. Probando PrestamoDAO...");
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        List<Prestamo> prestamos = prestamoDAO.obtenerTodos();
        
        System.out.println("   Total préstamos: " + prestamos.size());
        if (!prestamos.isEmpty()) {
            System.out.println("   Primer préstamo:");
            Prestamo p = prestamos.get(0);
            System.out.println("   - ID: " + p.getId());
            System.out.println("   - ID Usuario: " + p.getIdUsuario());
            System.out.println("   - ID Libro: " + p.getIdLibro());
            System.out.println("   - Nombre Usuario: " + p.getNombreUsuario());
            System.out.println("   - Título Libro: " + p.getTituloLibro());
            System.out.println("   - Fecha Préstamo: " + p.getFechaPrestamo());
            System.out.println("   - Estado: " + p.getEstado());
            System.out.println("   - Días Restantes: " + p.getDiasRestantes());
        } else {
            System.out.println("   ⚠ No hay préstamos en la base de datos");
        }
        
        System.out.println("\n===========================================");
        System.out.println("  FIN DE LA PRUEBA");
        System.out.println("===========================================");
    }
}