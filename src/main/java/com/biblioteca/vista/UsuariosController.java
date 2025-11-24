package com.biblioteca.vista;

import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de usuarios
 * 
 * Maneja todas las operaciones CRUD de usuarios y la interfaz de usuario
 * correspondiente.
 * 
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class UsuariosController {
    
    // Elementos de la interfaz
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, Usuario.TipoUsuario> colTipo;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, LocalDate> colFechaRegistro;
    @FXML private TableColumn<Usuario, Boolean> colActivo;
    @FXML private Label lblTotal;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnEliminar;
    @FXML private Button btnDetalles;
    
    // DAO y datos
    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaUsuarios;
    private Usuario usuarioActual;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        listaUsuarios = FXCollections.observableArrayList();
        
        // Configurar columnas de la tabla
        configurarTabla();
        
        // Cargar filtros
        cargarFiltros();
        
        // Cargar todos los usuarios
        cargarUsuarios();
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                btnEditar.setDisable(!haySeleccion);
                btnCambiarEstado.setDisable(!haySeleccion);
                btnEliminar.setDisable(!haySeleccion);
                btnDetalles.setDisable(!haySeleccion);
            }
        );
        
        // Listener para búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty() && cbTipo.getValue() == null && cbEstado.getValue() == null) {
                cargarUsuarios();
            }
        });
    }
    
    /**
     * Establece el usuario actual
     * 
     * @param usuario Usuario actual del sistema
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colApellido.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellido()));
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTipo()));
        colEmail.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colTelefono.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
        colUsername.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        colFechaRegistro.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaRegistro()));
        colActivo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isActivo()).asObject());
        
        // Estilo personalizado para estado
        colActivo.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("✓ Activo");
                        setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    } else {
                        setText("✗ Inactivo");
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Estilo para tipo de usuario
        colTipo.setCellFactory(column -> new TableCell<Usuario, Usuario.TipoUsuario>() {
            @Override
            protected void updateItem(Usuario.TipoUsuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDescripcion());
                    if (item == Usuario.TipoUsuario.ADMINISTRADOR) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    } else if (item == Usuario.TipoUsuario.BIBLIOTECARIO) {
                        setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #388e3c;");
                    }
                }
            }
        });
        
        tablaUsuarios.setItems(listaUsuarios);
    }
    
    /**
     * Carga los filtros en los ComboBox
     */
    private void cargarFiltros() {
        // Filtro de tipo
        ObservableList<String> tipos = FXCollections.observableArrayList(
            "Todos", "ADMINISTRADOR", "BIBLIOTECARIO", "LECTOR"
        );
        cbTipo.setItems(tipos);
        
        // Filtro de estado
        ObservableList<String> estados = FXCollections.observableArrayList(
            "Todos", "Activos", "Inactivos"
        );
        cbEstado.setItems(estados);
    }
    
    /**
     * Carga todos los usuarios en la tabla
     */
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            listaUsuarios.clear();
            listaUsuarios.addAll(usuarios);
            actualizarTotal();
            System.out.println("✓ Usuarios cargados: " + usuarios.size());
        } catch (Exception e) {
            System.err.println("✗ Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los usuarios", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Actualiza el label con el total de usuarios
     */
    private void actualizarTotal() {
        lblTotal.setText("Total: " + listaUsuarios.size() + " usuarios");
    }
    
    /**
     * Maneja la búsqueda de usuarios
     */
    @FXML
    private void handleBuscar() {
        String textoBusqueda = txtBuscar.getText().trim();
        String tipo = cbTipo.getValue();
        String estado = cbEstado.getValue();
        
        try {
            List<Usuario> resultados;
            
            // Obtener todos primero
            resultados = usuarioDAO.obtenerTodos();
            
            // Filtrar por texto
            if (!textoBusqueda.isEmpty()) {
                resultados = resultados.stream()
                    .filter(u -> u.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                                u.getApellido().toLowerCase().contains(textoBusqueda.toLowerCase()))
                    .toList();
            }
            
            // Filtrar por tipo
            if (tipo != null && !tipo.equals("Todos")) {
                Usuario.TipoUsuario tipoEnum = Usuario.TipoUsuario.valueOf(tipo);
                resultados = resultados.stream()
                    .filter(u -> u.getTipo() == tipoEnum)
                    .toList();
            }
            
            // Filtrar por estado
            if (estado != null && !estado.equals("Todos")) {
                boolean activo = estado.equals("Activos");
                resultados = resultados.stream()
                    .filter(u -> u.isActivo() == activo)
                    .toList();
            }
            
            listaUsuarios.clear();
            listaUsuarios.addAll(resultados);
            actualizarTotal();
            
            System.out.println("✓ Búsqueda completada: " + resultados.size() + " resultados");
            
        } catch (Exception e) {
            System.err.println("✗ Error en búsqueda: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al realizar la búsqueda", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Limpia los filtros y recarga todos los usuarios
     */
    @FXML
    private void handleLimpiar() {
        txtBuscar.clear();
        cbTipo.setValue(null);
        cbEstado.setValue(null);
        cargarUsuarios();
    }
    
    /**
     * Abre el formulario para crear un nuevo usuario
     */
    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioForm.fxml"));
            Scene scene = new Scene(loader.load());
            
            UsuarioFormController controller = loader.getController();
            controller.setUsuarioDAO(usuarioDAO);
            
            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            
            // Recargar usuarios después de cerrar el formulario
            cargarUsuarios();
            
        } catch (Exception e) {
            System.err.println("✗ Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Abre el formulario para editar el usuario seleccionado
     */
    @FXML
    private void handleEditar() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un usuario", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioForm.fxml"));
            Scene scene = new Scene(loader.load());
            
            UsuarioFormController controller = loader.getController();
            controller.setUsuarioDAO(usuarioDAO);
            controller.setUsuario(usuarioSeleccionado);
            
            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            
            // Recargar usuarios
            cargarUsuarios();
            
        } catch (Exception e) {
            System.err.println("✗ Error al abrir formulario de edición: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Cambia el estado activo/inactivo del usuario seleccionado
     */
    @FXML
    private void handleCambiarEstado() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un usuario", Alert.AlertType.WARNING);
            return;
        }
        
        String nuevoEstado = usuarioSeleccionado.isActivo() ? "desactivar" : "activar";
        
        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cambio de Estado");
        confirmacion.setHeaderText("¿Deseas " + nuevoEstado + " este usuario?");
        confirmacion.setContentText(usuarioSeleccionado.getNombreCompleto());
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean nuevoEstadoBool = !usuarioSeleccionado.isActivo();
                boolean cambiado = usuarioDAO.cambiarEstado(usuarioSeleccionado.getId(), nuevoEstadoBool);
                
                if (cambiado) {
                    mostrarAlerta("Éxito", "Estado cambiado correctamente", Alert.AlertType.INFORMATION);
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo cambiar el estado", Alert.AlertType.ERROR);
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error al cambiar estado: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Error", "Error al cambiar estado: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    /**
     * Elimina el usuario seleccionado
     */
    @FXML
    private void handleEliminar() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un usuario", Alert.AlertType.WARNING);
            return;
        }
        
        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar este usuario?");
        confirmacion.setContentText(usuarioSeleccionado.getNombreCompleto() + "\n\n" +
                                   "ADVERTENCIA: Esta acción no se puede deshacer.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = usuarioDAO.eliminar(usuarioSeleccionado.getId());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Usuario eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el usuario", Alert.AlertType.ERROR);
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    /**
     * Muestra los detalles del usuario seleccionado
     */
    @FXML
    private void handleVerDetalles() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un usuario", Alert.AlertType.WARNING);
            return;
        }
        
        String detalles = String.format(
            "ID: %d\n" +
            "Nombre Completo: %s\n" +
            "Tipo: %s\n" +
            "Email: %s\n" +
            "Teléfono: %s\n" +
            "Dirección: %s\n" +
            "Usuario: %s\n" +
            "Fecha de Registro: %s\n" +
            "Estado: %s",
            usuarioSeleccionado.getId(),
            usuarioSeleccionado.getNombreCompleto(),
            usuarioSeleccionado.getTipo().getDescripcion(),
            usuarioSeleccionado.getEmail(),
            usuarioSeleccionado.getTelefono(),
            usuarioSeleccionado.getDireccion(),
            usuarioSeleccionado.getUsername(),
            usuarioSeleccionado.getFechaRegistro(),
            usuarioSeleccionado.isActivo() ? "Activo" : "Inactivo"
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Usuario");
        alert.setHeaderText(usuarioSeleccionado.getNombreCompleto());
        alert.setContentText(detalles);
        alert.showAndWait();
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