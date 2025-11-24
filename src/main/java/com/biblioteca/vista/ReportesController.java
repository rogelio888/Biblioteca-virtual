package com.biblioteca.vista;

import com.biblioteca.controlador.LibroDAO;
import com.biblioteca.controlador.PrestamoDAO;
import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para la generación de reportes
 *
 * Genera reportes en formato texto plano que pueden ser visualizados
 * o impresos fácilmente.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class ReportesController {

    @FXML private Label lblMensaje;

    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
        prestamoDAO = new PrestamoDAO();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    private void generarReporteLibros() {
        try {
            List<Libro> libros = libroDAO.obtenerTodos();

            if (libros.isEmpty()) {
                mostrarAlerta("Información", "No hay libros para generar el reporte", Alert.AlertType.INFORMATION);
                return;
            }

            String nombreArchivo = generarNombreArchivo("Reporte_Libros");
            File archivo = new File(System.getProperty("user.home") + "/Desktop/" + nombreArchivo);

            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                // Encabezado
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("           BIBLIOTECA INTELIGENTE 1.0 - REPORTE DE LIBROS");
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fecha de generación: " + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                writer.println("Total de libros: " + libros.size());
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println();

                // Tabla de libros
                writer.printf("%-5s %-35s %-25s %-15s %-6s %-6s%n",
                        "ID", "TÍTULO", "AUTOR", "CATEGORÍA", "AÑO", "STOCK");
                writer.println("───────────────────────────────────────────────────────────────");

                for (Libro libro : libros) {
                    String titulo = truncar(libro.getTitulo(), 35);
                    String autor = truncar(libro.getAutor(), 25);
                    String categoria = truncar(libro.getCategoria(), 15);

                    writer.printf("%-5d %-35s %-25s %-15s %-6d %-6d%n",
                            libro.getId(),
                            titulo,
                            autor,
                            categoria,
                            libro.getAnioPublicacion(),
                            libro.getStock()
                    );
                }

                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fin del reporte");
            }

            mostrarMensajeExito("Reporte generado exitosamente en: " + archivo.getAbsolutePath());
            abrirArchivo(archivo);

        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void generarReporteUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();

            if (usuarios.isEmpty()) {
                mostrarAlerta("Información", "No hay usuarios para generar el reporte", Alert.AlertType.INFORMATION);
                return;
            }

            String nombreArchivo = generarNombreArchivo("Reporte_Usuarios");
            File archivo = new File(System.getProperty("user.home") + "/Desktop/" + nombreArchivo);

            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("         BIBLIOTECA INTELIGENTE 1.0 - REPORTE DE USUARIOS");
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fecha de generación: " + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                writer.println("Total de usuarios: " + usuarios.size());
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println();

                writer.printf("%-5s %-30s %-20s %-30s %-10s%n",
                        "ID", "NOMBRE COMPLETO", "TIPO", "EMAIL", "ESTADO");
                writer.println("───────────────────────────────────────────────────────────────");

                for (Usuario usuario : usuarios) {
                    String nombre = truncar(usuario.getNombreCompleto(), 30);
                    String tipo = usuario.getTipo().toString();
                    String email = truncar(usuario.getEmail(), 30);
                    String estado = usuario.isActivo() ? "ACTIVO" : "INACTIVO";

                    writer.printf("%-5d %-30s %-20s %-30s %-10s%n",
                            usuario.getId(),
                            nombre,
                            tipo,
                            email,
                            estado
                    );
                }

                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fin del reporte");
            }

            mostrarMensajeExito("Reporte generado exitosamente en: " + archivo.getAbsolutePath());
            abrirArchivo(archivo);

        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void generarReportePrestamosActivos() {
        try {
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();

            if (prestamos.isEmpty()) {
                mostrarAlerta("Información", "No hay préstamos activos", Alert.AlertType.INFORMATION);
                return;
            }

            String nombreArchivo = generarNombreArchivo("Reporte_Prestamos_Activos");
            File archivo = new File(System.getProperty("user.home") + "/Desktop/" + nombreArchivo);

            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("      BIBLIOTECA INTELIGENTE 1.0 - PRÉSTAMOS ACTIVOS");
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fecha de generación: " + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                writer.println("Total de préstamos activos: " + prestamos.size());
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println();

                writer.printf("%-5s %-25s %-25s %-12s %-12s %-10s%n",
                        "ID", "USUARIO", "LIBRO", "PRÉSTAMO", "DEVOLUCIÓN", "ESTADO");
                writer.println("───────────────────────────────────────────────────────────────");

                for (Prestamo prestamo : prestamos) {
                    String usuario = truncar(prestamo.getNombreUsuario(), 25);
                    String libro = truncar(prestamo.getTituloLibro(), 25);

                    writer.printf("%-5d %-25s %-25s %-12s %-12s %-10s%n",
                            prestamo.getId(),
                            usuario,
                            libro,
                            prestamo.getFechaPrestamo().toString(),
                            prestamo.getFechaDevolucionEsperada().toString(),
                            prestamo.getEstado().toString()
                    );
                }

                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fin del reporte");
            }

            mostrarMensajeExito("Reporte generado exitosamente en: " + archivo.getAbsolutePath());
            abrirArchivo(archivo);

        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void generarReportePrestamosRetrasados() {
        try {
            prestamoDAO.actualizarPrestamosRetrasados();
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosRetrasados();

            if (prestamos.isEmpty()) {
                mostrarAlerta("Información", "¡Excelente! No hay préstamos retrasados", Alert.AlertType.INFORMATION);
                return;
            }

            String nombreArchivo = generarNombreArchivo("Reporte_Prestamos_Retrasados");
            File archivo = new File(System.getProperty("user.home") + "/Desktop/" + nombreArchivo);

            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("     BIBLIOTECA INTELIGENTE 1.0 - PRÉSTAMOS RETRASADOS");
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fecha de generación: " + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                writer.println("Total de préstamos retrasados: " + prestamos.size());
                writer.println("¡ATENCIÓN! Estos préstamos requieren seguimiento urgente");
                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println();

                writer.printf("%-5s %-25s %-25s %-12s %-12s %-10s%n",
                        "ID", "USUARIO", "LIBRO", "PRÉSTAMO", "DEBIÓ DEV.", "RETRASO");
                writer.println("───────────────────────────────────────────────────────────────");

                for (Prestamo prestamo : prestamos) {
                    String usuario = truncar(prestamo.getNombreUsuario(), 25);
                    String libro = truncar(prestamo.getTituloLibro(), 25);
                    long retraso = Math.abs(prestamo.getDiasRestantes());

                    writer.printf("%-5d %-25s %-25s %-12s %-12s %d días%n",
                            prestamo.getId(),
                            usuario,
                            libro,
                            prestamo.getFechaPrestamo().toString(),
                            prestamo.getFechaDevolucionEsperada().toString(),
                            retraso
                    );
                }

                writer.println("═══════════════════════════════════════════════════════════════");
                writer.println("Fin del reporte");
            }

            mostrarMensajeExito("Reporte generado exitosamente en: " + archivo.getAbsolutePath());
            abrirArchivo(archivo);

        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String generarNombreArchivo(String prefijo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return prefijo + "_" + LocalDateTime.now().format(formatter) + ".txt";
    }

    private String truncar(String texto, int maxLength) {
        if (texto == null) return "";
        return texto.length() > maxLength ? texto.substring(0, maxLength - 3) + "..." : texto;
    }

    private void abrirArchivo(File archivo) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el archivo automáticamente: " + e.getMessage());
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        lblMensaje.setText("✓ " + mensaje);
        lblMensaje.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        lblMensaje.setVisible(true);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}