package com.biblioteca.vista;

import com.biblioteca.controlador.LibroDAO;
import com.biblioteca.controlador.PrestamoDAO;
import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador principal del Dashboard
 *
 * Gestiona la navegación entre las diferentes secciones del sistema
 * y muestra estadísticas generales.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class DashboardController {

    // Elementos de la interfaz
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblFechaHora;
    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contenedorPrincipal;
    @FXML private VBox vistaDashboard;

    // Botones del menú
    @FXML private Button btnDashboard;
    @FXML private Button btnLibros;
    @FXML private Button btnUsuarios;
    @FXML private Button btnPrestamos;
    @FXML private Button btnReportes;

    // Labels de estadísticas
    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosRetrasados;

    // DAOs
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;

    // Usuario actual
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
        prestamoDAO = new PrestamoDAO();

        // Iniciar reloj
        iniciarReloj();

        // Cargar estadísticas iniciales
        cargarEstadisticas();
    }

    /**
     * Establece el usuario actual y configura la interfaz según su rol
     *
     * @param usuario Usuario autenticado
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblUsuario.setText(usuario.getNombreCompleto());
        lblRol.setText(usuario.getTipo().getDescripcion());

        // Configurar permisos según el rol
        configurarPermisos();
    }

    /**
     * Configura los permisos de los botones según el rol del usuario
     */
    private void configurarPermisos() {
        if (usuarioActual.esLector()) {
            // Los lectores solo pueden ver préstamos
            btnUsuarios.setDisable(true);
            btnReportes.setDisable(true);
        }
    }

    /**
     * Inicia el reloj que muestra fecha y hora actual
     */
    private void iniciarReloj() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblFechaHora.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Carga las estadísticas del sistema
     */
    @FXML
    public void cargarEstadisticas() {
        try {
            // Total de libros
            int totalLibros = libroDAO.contarLibros();
            lblTotalLibros.setText(String.valueOf(totalLibros));

            // Total de usuarios activos
            int totalUsuarios = usuarioDAO.contarUsuariosActivos();
            lblTotalUsuarios.setText(String.valueOf(totalUsuarios));

            // Préstamos activos
            int prestamosActivos = prestamoDAO.contarPorEstado(Prestamo.EstadoPrestamo.PENDIENTE) +
                    prestamoDAO.contarPorEstado(Prestamo.EstadoPrestamo.RENOVADO);
            lblPrestamosActivos.setText(String.valueOf(prestamosActivos));

            // Préstamos retrasados
            int prestamosRetrasados = prestamoDAO.contarPorEstado(Prestamo.EstadoPrestamo.RETRASADO);
            lblPrestamosRetrasados.setText(String.valueOf(prestamosRetrasados));

            // Actualizar estados de préstamos
            prestamoDAO.actualizarPrestamosRetrasados();

            System.out.println("✓ Estadísticas cargadas correctamente");

        } catch (Exception e) {
            System.err.println("✗ Error al cargar estadísticas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las estadísticas", Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra la vista del Dashboard
     */
    @FXML
    private void mostrarDashboard() {
        resaltarBotonActivo(btnDashboard);
        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(vistaDashboard);
        cargarEstadisticas();
    }

    /**
     * Muestra la vista de gestión de libros
     */
    @FXML
    private void mostrarLibros() {
        resaltarBotonActivo(btnLibros);
        cargarVista("/fxml/Libros.fxml");
    }

    /**
     * Muestra la vista de gestión de usuarios
     */
    @FXML
    private void mostrarUsuarios() {
        resaltarBotonActivo(btnUsuarios);
        cargarVista("/fxml/Usuarios.fxml");
    }

    /**
     * Muestra la vista de gestión de préstamos
     */
    @FXML
    private void mostrarPrestamos() {
        resaltarBotonActivo(btnPrestamos);
        cargarVista("/fxml/Prestamos.fxml");
    }

    /**
     * Muestra la vista de reportes
     */
    @FXML
    private void mostrarReportes() {
        resaltarBotonActivo(btnReportes);
        cargarVista("/fxml/Reportes.fxml");
    }

    /**
     * Carga una vista FXML en el contenedor principal
     *
     * @param rutaFxml Ruta del archivo FXML
     */
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();

            // Pasar el usuario actual al controlador de la vista
            Object controller = loader.getController();
            if (controller instanceof LibrosController) {
                ((LibrosController) controller).setUsuario(usuarioActual);
            } else if (controller instanceof UsuariosController) {
                ((UsuariosController) controller).setUsuario(usuarioActual);
            } else if (controller instanceof PrestamosController) {
                ((PrestamosController) controller).setUsuario(usuarioActual);
            } else if (controller instanceof ReportesController) {
                ((ReportesController) controller).setUsuario(usuarioActual);
            }

            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(vista);

        } catch (Exception e) {
            System.err.println("✗ Error al cargar vista " + rutaFxml + ": " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista", Alert.AlertType.ERROR);
        }
    }

    /**
     * Resalta el botón del menú activo
     *
     * @param botonActivo Botón a resaltar
     */
    private void resaltarBotonActivo(Button botonActivo) {
        // Restablecer estilo de todos los botones
        btnDashboard.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        btnLibros.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        btnUsuarios.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        btnPrestamos.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        btnReportes.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        // Resaltar botón activo
        botonActivo.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    /**
     * Cierra la sesión y vuelve al login
     */
    @FXML
    private void handleCerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Estás seguro que deseas cerrar sesión?");
        confirmacion.setContentText("Volverás a la pantalla de inicio de sesión");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Cargar ventana de login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setTitle("Biblioteca Inteligente 1.0 - Login");
                    stage.setScene(new Scene(root, 800, 600));
                    stage.setResizable(false);

                    // Cerrar ventana actual
                    Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
                    currentStage.close();

                    // Mostrar login
                    stage.show();

                    System.out.println("✓ Sesión cerrada correctamente");

                } catch (Exception e) {
                    System.err.println("✗ Error al cerrar sesión: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Muestra una alerta
     *
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     * @param tipo Tipo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}