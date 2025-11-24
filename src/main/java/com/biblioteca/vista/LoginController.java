package com.biblioteca.vista;

import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador para la ventana de Login
 *
 * Gestiona la autenticación de usuarios y la navegación
 * al dashboard principal según el rol del usuario.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;
    @FXML private Button btnLogin;

    private UsuarioDAO usuarioDAO;

    /**
     * Inicializa el controlador
     * Se ejecuta automáticamente después de cargar el FXML
     */
    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();

        // Listener para limpiar mensaje de error al escribir
        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> ocultarMensaje());
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> ocultarMensaje());
    }

    /**
     * Maneja el evento de clic en el botón de login
     * Valida las credenciales y navega al dashboard
     */
    @FXML
    private void handleLogin() {
        String username = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingresa usuario y contraseña");
            return;
        }

        // Deshabilitar botón mientras se autentica
        btnLogin.setDisable(true);

        try {
            // Intentar autenticar
            Usuario usuario = usuarioDAO.autenticar(username, password);

            if (usuario != null) {
                // Login exitoso
                System.out.println("✓ Login exitoso: " + usuario.getNombreCompleto());
                abrirDashboard(usuario);
            } else {
                // Credenciales inválidas
                mostrarError("Usuario o contraseña incorrectos");
                btnLogin.setDisable(false);
            }

        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos");
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            btnLogin.setDisable(false);
        }
    }

    /**
     * Abre el dashboard principal según el rol del usuario
     *
     * @param usuario Usuario autenticado
     */
    private void abrirDashboard(Usuario usuario) {
        try {
            // Cargar el dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            // Pasar el usuario al controlador del dashboard
            DashboardController controller = loader.getController();
            controller.setUsuario(usuario);

            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Biblioteca Inteligente 1.0 - " + usuario.getTipo().getDescripcion());
            stage.setScene(new Scene(root, 1200, 700));
            stage.setResizable(true);
            stage.setMaximized(true);

            // Cerrar ventana de login
            Stage loginStage = (Stage) btnLogin.getScene().getWindow();
            loginStage.close();

            // Mostrar dashboard
            stage.show();

        } catch (Exception e) {
            mostrarError("Error al abrir el sistema");
            System.err.println("Error al cargar dashboard: " + e.getMessage());
            e.printStackTrace();
            btnLogin.setDisable(false);
        }
    }

    /**
     * Muestra un mensaje de error
     *
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarError(String mensaje) {
        lblMensaje.setText("⚠ " + mensaje);
        lblMensaje.setVisible(true);
        lblMensaje.setStyle("-fx-text-fill: #f44336;");
    }

    /**
     * Oculta el mensaje de error
     */
    private void ocultarMensaje() {
        if (lblMensaje.isVisible()) {
            lblMensaje.setVisible(false);
        }
    }
}