package com.biblioteca.vista;

import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controlador para el formulario de usuario (crear/editar)
 *
 * Maneja la validación y guardado de usuarios.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class UsuarioFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private ComboBox<Usuario.TipoUsuario> cbTipo;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblMensaje;

    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;
    private boolean modoEdicion = false;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        // Cargar tipos de usuario en el ComboBox
        cbTipo.setItems(FXCollections.observableArrayList(Usuario.TipoUsuario.values()));
    }

    /**
     * Establece el DAO de usuarios
     *
     * @param usuarioDAO DAO de usuarios
     */
    public void setUsuarioDAO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Establece el usuario a editar (modo edición)
     *
     * @param usuario Usuario a editar
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Usuario");

        // Cargar datos en el formulario
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        cbTipo.setValue(usuario.getTipo());
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(usuario.getPassword());
        txtConfirmarPassword.setText(usuario.getPassword());
        chkActivo.setSelected(usuario.isActivo());

        // En modo edición, hacer el password opcional
        txtPassword.setPromptText("Dejar vacío para mantener la actual");
        txtConfirmarPassword.setPromptText("Dejar vacío para mantener la actual");
    }

    /**
     * Guarda el usuario (crear o actualizar)
     */
    @FXML
    private void handleGuardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        try {
            // Crear o actualizar usuario
            if (modoEdicion) {
                actualizarUsuario();
            } else {
                crearUsuario();
            }

        } catch (Exception e) {
            System.err.println("✗ Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al guardar: " + e.getMessage(), false);
        }
    }

    /**
     * Crea un nuevo usuario
     */
    private void crearUsuario() {
        // Verificar si el username ya existe
        if (usuarioDAO.existeUsername(txtUsername.getText().trim(), 0)) {
            mostrarMensaje("El nombre de usuario ya existe", false);
            return;
        }

        // Verificar si el email ya existe
        if (usuarioDAO.existeEmail(txtEmail.getText().trim(), 0)) {
            mostrarMensaje("El email ya está registrado", false);
            return;
        }

        Usuario nuevoUsuario = new Usuario(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                cbTipo.getValue(),
                txtEmail.getText().trim(),
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                txtUsername.getText().trim(),
                txtPassword.getText() // En producción debería estar encriptada
        );

        nuevoUsuario.setActivo(chkActivo.isSelected());

        boolean guardado = usuarioDAO.insertar(nuevoUsuario);

        if (guardado) {
            mostrarMensaje("✓ Usuario creado exitosamente", true);
            cerrarVentana();
        } else {
            mostrarMensaje("Error al crear el usuario", false);
        }
    }

    /**
     * Actualiza un usuario existente
     */
    private void actualizarUsuario() {
        // Verificar si el username ya existe (excluyendo el usuario actual)
        if (usuarioDAO.existeUsername(txtUsername.getText().trim(), usuarioActual.getId())) {
            mostrarMensaje("El nombre de usuario ya existe en otro usuario", false);
            return;
        }

        // Verificar si el email ya existe (excluyendo el usuario actual)
        if (usuarioDAO.existeEmail(txtEmail.getText().trim(), usuarioActual.getId())) {
            mostrarMensaje("El email ya está registrado en otro usuario", false);
            return;
        }

        usuarioActual.setNombre(txtNombre.getText().trim());
        usuarioActual.setApellido(txtApellido.getText().trim());
        usuarioActual.setTipo(cbTipo.getValue());
        usuarioActual.setEmail(txtEmail.getText().trim());
        usuarioActual.setTelefono(txtTelefono.getText().trim());
        usuarioActual.setDireccion(txtDireccion.getText().trim());
        usuarioActual.setUsername(txtUsername.getText().trim());

        // Solo actualizar password si se ingresó uno nuevo
        if (!txtPassword.getText().isEmpty()) {
            usuarioActual.setPassword(txtPassword.getText());
        }

        usuarioActual.setActivo(chkActivo.isSelected());

        boolean actualizado = usuarioDAO.actualizar(usuarioActual);

        if (actualizado) {
            mostrarMensaje("✓ Usuario actualizado exitosamente", true);
            cerrarVentana();
        } else {
            mostrarMensaje("Error al actualizar el usuario", false);
        }
    }

    /**
     * Valida los campos del formulario
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("El nombre es obligatorio", false);
            txtNombre.requestFocus();
            return false;
        }

        // Validar apellido
        if (txtApellido.getText().trim().isEmpty()) {
            mostrarMensaje("El apellido es obligatorio", false);
            txtApellido.requestFocus();
            return false;
        }

        // Validar tipo
        if (cbTipo.getValue() == null) {
            mostrarMensaje("Debes seleccionar un tipo de usuario", false);
            cbTipo.requestFocus();
            return false;
        }

        // Validar email
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarMensaje("El email es obligatorio", false);
            txtEmail.requestFocus();
            return false;
        }

        // Validar formato de email
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarMensaje("El formato del email no es válido", false);
            txtEmail.requestFocus();
            return false;
        }

        // Validar username
        if (txtUsername.getText().trim().isEmpty()) {
            mostrarMensaje("El nombre de usuario es obligatorio", false);
            txtUsername.requestFocus();
            return false;
        }

        // Validar username (solo letras, números y guión bajo)
        if (!txtUsername.getText().matches("^[a-zA-Z0-9_]+$")) {
            mostrarMensaje("El usuario solo puede contener letras, números y guión bajo", false);
            txtUsername.requestFocus();
            return false;
        }

        // Validar password (solo en modo creación o si se ingresó uno nuevo en edición)
        if (!modoEdicion || !txtPassword.getText().isEmpty()) {
            if (txtPassword.getText().isEmpty()) {
                mostrarMensaje("La contraseña es obligatoria", false);
                txtPassword.requestFocus();
                return false;
            }

            if (txtPassword.getText().length() < 6) {
                mostrarMensaje("La contraseña debe tener al menos 6 caracteres", false);
                txtPassword.requestFocus();
                return false;
            }

            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarMensaje("Las contraseñas no coinciden", false);
                txtConfirmarPassword.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Muestra un mensaje en la interfaz
     *
     * @param mensaje Mensaje a mostrar
     * @param esExito true si es mensaje de éxito, false si es error
     */
    private void mostrarMensaje(String mensaje, boolean esExito) {
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);

        if (esExito) {
            lblMensaje.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }

    /**
     * Cancela la operación y cierra la ventana
     */
    @FXML
    private void handleCancelar() {
        // Confirmar si hay cambios sin guardar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Deseas cancelar?");
        confirmacion.setContentText("Los cambios no guardados se perderán");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cerrarVentana();
            }
        });
    }

    /**
     * Cierra la ventana del formulario
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}