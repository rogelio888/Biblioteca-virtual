package com.biblioteca.vista;

import com.biblioteca.controlador.LibroDAO;
import com.biblioteca.modelo.Libro;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.Year;

/**
 * Controlador para el formulario de libro (crear/editar)
 *
 * Maneja la validación y guardado de libros.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class LibroFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtISBN;
    @FXML private Spinner<Integer> spAnio;
    @FXML private Spinner<Integer> spStock;
    @FXML private Label lblMensaje;

    private LibroDAO libroDAO;
    private Libro libroActual;
    private boolean modoEdicion = false;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        // Configurar spinner de año
        int anioActual = Year.now().getValue();
        SpinnerValueFactory<Integer> valueFactoryAnio =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, anioActual, anioActual);
        spAnio.setValueFactory(valueFactoryAnio);

        // Configurar spinner de stock
        SpinnerValueFactory<Integer> valueFactoryStock =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1);
        spStock.setValueFactory(valueFactoryStock);

        // Cargar categorías predefinidas
        cbCategoria.setItems(FXCollections.observableArrayList(
                "Ficción", "Ciencia", "Historia", "Tecnología",
                "Clásicos", "Autoayuda", "Infantil/Juvenil",
                "Poesía", "Psicología", "Filosofía"
        ));
    }

    /**
     * Establece el DAO de libros
     *
     * @param libroDAO DAO de libros
     */
    public void setLibroDAO(LibroDAO libroDAO) {
        this.libroDAO = libroDAO;
    }

    /**
     * Establece el libro a editar (modo edición)
     *
     * @param libro Libro a editar
     */
    public void setLibro(Libro libro) {
        this.libroActual = libro;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Libro");

        // Cargar datos en el formulario
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        cbCategoria.setValue(libro.getCategoria());
        txtEditorial.setText(libro.getEditorial());
        txtISBN.setText(libro.getIsbn());
        spAnio.getValueFactory().setValue(libro.getAnioPublicacion());
        spStock.getValueFactory().setValue(libro.getStock());
    }

    /**
     * Guarda el libro (crear o actualizar)
     */
    @FXML
    private void handleGuardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        try {
            // Crear o actualizar libro
            if (modoEdicion) {
                actualizarLibro();
            } else {
                crearLibro();
            }

        } catch (Exception e) {
            System.err.println("✗ Error al guardar libro: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al guardar: " + e.getMessage(), false);
        }
    }

    /**
     * Crea un nuevo libro
     */
    private void crearLibro() {
        // Verificar si el ISBN ya existe
        if (libroDAO.existeISBN(txtISBN.getText().trim(), 0)) {
            mostrarMensaje("El ISBN ya existe en el sistema", false);
            return;
        }

        Libro nuevoLibro = new Libro(
                txtTitulo.getText().trim(),
                txtAutor.getText().trim(),
                cbCategoria.getValue(),
                spStock.getValue(),
                spAnio.getValue(),
                txtISBN.getText().trim(),
                txtEditorial.getText().trim()
        );

        boolean guardado = libroDAO.insertar(nuevoLibro);

        if (guardado) {
            mostrarMensaje("✓ Libro creado exitosamente", true);
            cerrarVentana();
        } else {
            mostrarMensaje("Error al crear el libro", false);
        }
    }

    /**
     * Actualiza un libro existente
     */
    private void actualizarLibro() {
        // Verificar si el ISBN ya existe (excluyendo el libro actual)
        if (libroDAO.existeISBN(txtISBN.getText().trim(), libroActual.getId())) {
            mostrarMensaje("El ISBN ya existe en otro libro", false);
            return;
        }

        libroActual.setTitulo(txtTitulo.getText().trim());
        libroActual.setAutor(txtAutor.getText().trim());
        libroActual.setCategoria(cbCategoria.getValue());
        libroActual.setEditorial(txtEditorial.getText().trim());
        libroActual.setIsbn(txtISBN.getText().trim());
        libroActual.setAnioPublicacion(spAnio.getValue());
        libroActual.setStock(spStock.getValue());

        boolean actualizado = libroDAO.actualizar(libroActual);

        if (actualizado) {
            mostrarMensaje("✓ Libro actualizado exitosamente", true);
            cerrarVentana();
        } else {
            mostrarMensaje("Error al actualizar el libro", false);
        }
    }

    /**
     * Valida los campos del formulario
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        // Validar título
        if (txtTitulo.getText().trim().isEmpty()) {
            mostrarMensaje("El título es obligatorio", false);
            txtTitulo.requestFocus();
            return false;
        }

        // Validar autor
        if (txtAutor.getText().trim().isEmpty()) {
            mostrarMensaje("El autor es obligatorio", false);
            txtAutor.requestFocus();
            return false;
        }

        // Validar categoría
        if (cbCategoria.getValue() == null || cbCategoria.getValue().trim().isEmpty()) {
            mostrarMensaje("La categoría es obligatoria", false);
            cbCategoria.requestFocus();
            return false;
        }

        // Validar ISBN
        if (txtISBN.getText().trim().isEmpty()) {
            mostrarMensaje("El ISBN es obligatorio", false);
            txtISBN.requestFocus();
            return false;
        }

        // Validar formato básico de ISBN
        String isbn = txtISBN.getText().trim().replaceAll("-", "");
        if (isbn.length() != 10 && isbn.length() != 13) {
            mostrarMensaje("El ISBN debe tener 10 o 13 dígitos", false);
            txtISBN.requestFocus();
            return false;
        }

        // Validar año
        int anioActual = Year.now().getValue();
        if (spAnio.getValue() < 1000 || spAnio.getValue() > anioActual) {
            mostrarMensaje("El año debe estar entre 1000 y " + anioActual, false);
            return false;
        }

        // Validar stock
        if (spStock.getValue() < 0) {
            mostrarMensaje("El stock no puede ser negativo", false);
            return false;
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
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}