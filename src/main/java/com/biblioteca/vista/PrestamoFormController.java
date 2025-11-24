package com.biblioteca.vista;

import com.biblioteca.controlador.LibroDAO;
import com.biblioteca.controlador.PrestamoDAO;
import com.biblioteca.controlador.UsuarioDAO;
import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para el formulario de préstamo
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class PrestamoFormController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private ComboBox<Libro> cbLibro;
    @FXML private Label lblStock;
    @FXML private Spinner<Integer> spDias;
    @FXML private DatePicker dpFechaPrestamo;
    @FXML private Label lblFechaDevolucion;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMensaje;

    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();

        // Configurar spinner de días
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 14);
        spDias.setValueFactory(valueFactory);

        // Configurar fecha de préstamo
        dpFechaPrestamo.setValue(LocalDate.now());

        // Cargar datos
        cargarUsuarios();
        cargarLibros();

        // Listeners
        cbLibro.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblStock.setText("Disponibles: " + newVal.getStock());
                if (newVal.getStock() == 0) {
                    lblStock.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                } else if (newVal.getStock() <= 2) {
                    lblStock.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                } else {
                    lblStock.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                }
            }
        });

        spDias.valueProperty().addListener((obs, oldVal, newVal) -> calcularFechaDevolucion());
        dpFechaPrestamo.valueProperty().addListener((obs, oldVal, newVal) -> calcularFechaDevolucion());

        calcularFechaDevolucion();
    }

    public void setPrestamoDAO(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            // Filtrar solo usuarios activos
            List<Usuario> activos = usuarios.stream()
                    .filter(Usuario::isActivo)
                    .toList();

            cbUsuario.setItems(FXCollections.observableArrayList(activos));

            // Personalizar cómo se muestra cada usuario
            cbUsuario.setCellFactory(param -> new ListCell<Usuario>() {
                @Override
                protected void updateItem(Usuario item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombreCompleto() + " (" + item.getTipo() + ")");
                    }
                }
            });

            cbUsuario.setButtonCell(new ListCell<Usuario>() {
                @Override
                protected void updateItem(Usuario item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombreCompleto());
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("✗ Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.obtenerTodos();
            // Filtrar solo libros con stock
            List<Libro> disponibles = libros.stream()
                    .filter(Libro::estaDisponible)
                    .toList();

            cbLibro.setItems(FXCollections.observableArrayList(disponibles));

            // Personalizar cómo se muestra cada libro
            cbLibro.setCellFactory(param -> new ListCell<Libro>() {
                @Override
                protected void updateItem(Libro item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitulo() + " - " + item.getAutor() + " (Stock: " + item.getStock() + ")");
                    }
                }
            });

            cbLibro.setButtonCell(new ListCell<Libro>() {
                @Override
                protected void updateItem(Libro item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitulo());
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("✗ Error al cargar libros: " + e.getMessage());
        }
    }

    private void calcularFechaDevolucion() {
        if (dpFechaPrestamo.getValue() != null) {
            LocalDate fechaDevolucion = dpFechaPrestamo.getValue().plusDays(spDias.getValue());
            lblFechaDevolucion.setText(fechaDevolucion.toString());
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Usuario usuario = cbUsuario.getValue();
            Libro libro = cbLibro.getValue();

            // Verificar stock nuevamente
            if (!libro.estaDisponible()) {
                mostrarMensaje("El libro no tiene stock disponible", false);
                return;
            }

            Prestamo nuevoPrestamo = new Prestamo(
                    usuario.getId(),
                    libro.getId(),
                    spDias.getValue()
            );

            nuevoPrestamo.setFechaPrestamo(dpFechaPrestamo.getValue());
            nuevoPrestamo.setObservaciones(txtObservaciones.getText());

            boolean guardado = prestamoDAO.insertar(nuevoPrestamo);

            if (guardado) {
                mostrarMensaje("✓ Préstamo registrado exitosamente", true);
                cerrarVentana();
            } else {
                mostrarMensaje("Error al registrar el préstamo", false);
            }

        } catch (Exception e) {
            System.err.println("✗ Error al guardar préstamo: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error: " + e.getMessage(), false);
        }
    }

    private boolean validarCampos() {
        if (cbUsuario.getValue() == null) {
            mostrarMensaje("Debes seleccionar un usuario", false);
            cbUsuario.requestFocus();
            return false;
        }

        if (cbLibro.getValue() == null) {
            mostrarMensaje("Debes seleccionar un libro", false);
            cbLibro.requestFocus();
            return false;
        }

        if (dpFechaPrestamo.getValue() == null) {
            mostrarMensaje("La fecha de préstamo es obligatoria", false);
            dpFechaPrestamo.requestFocus();
            return false;
        }

        if (spDias.getValue() < 1 || spDias.getValue() > 90) {
            mostrarMensaje("Los días deben estar entre 1 y 90", false);
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String mensaje, boolean esExito) {
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);

        if (esExito) {
            lblMensaje.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleCancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Deseas cancelar?");
        confirmacion.setContentText("El préstamo no se registrará");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cerrarVentana();
            }
        });
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cbUsuario.getScene().getWindow();
        stage.close();
    }
}