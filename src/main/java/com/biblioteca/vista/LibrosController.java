package com.biblioteca.vista;

import com.biblioteca.controlador.LibroDAO;
import com.biblioteca.modelo.Libro;
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

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de libros
 *
 * Maneja todas las operaciones CRUD de libros y la interfaz de usuario
 * correspondiente.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class LibrosController {

    // Elementos de la interfaz
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, Integer> colAnio;
    @FXML private TableColumn<Libro, String> colISBN;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private Label lblTotal;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnDetalles;

    // DAO y datos
    private LibroDAO libroDAO;
    private ObservableList<Libro> listaLibros;
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        listaLibros = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        configurarTabla();

        // Cargar categorías
        cargarCategorias();

        // Cargar todos los libros
        cargarLibros();

        // Listener para habilitar/deshabilitar botones según selección
        tablaLibros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                    btnDetalles.setDisable(!haySeleccion);
                }
        );

        // Listener para búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty() && cbCategoria.getValue() == null) {
                cargarLibros();
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
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Estilo personalizado para stock bajo
        colStock.setCellFactory(column -> new TableCell<Libro, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item <= 2) {
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else if (item <= 5) {
                        setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tablaLibros.setItems(listaLibros);
    }

    /**
     * Carga las categorías en el ComboBox
     */
    private void cargarCategorias() {
        List<String> categorias = libroDAO.obtenerCategorias();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Todas");
        items.addAll(categorias);
        cbCategoria.setItems(items);
    }

    /**
     * Carga todos los libros en la tabla
     */
    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.obtenerTodos();
            listaLibros.clear();
            listaLibros.addAll(libros);
            actualizarTotal();
            System.out.println("✓ Libros cargados: " + libros.size());
        } catch (Exception e) {
            System.err.println("✗ Error al cargar libros: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los libros", Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza el label con el total de libros
     */
    private void actualizarTotal() {
        lblTotal.setText("Total: " + listaLibros.size() + " libros");
    }

    /**
     * Maneja la búsqueda de libros
     */
    @FXML
    private void handleBuscar() {
        String textoBusqueda = txtBuscar.getText().trim();
        String categoria = cbCategoria.getValue();

        try {
            List<Libro> resultados;

            if (!textoBusqueda.isEmpty()) {
                // Buscar por título o autor
                List<Libro> porTitulo = libroDAO.buscarPorTitulo(textoBusqueda);
                List<Libro> porAutor = libroDAO.buscarPorAutor(textoBusqueda);

                // Combinar resultados sin duplicados
                resultados = porTitulo;
                for (Libro libro : porAutor) {
                    if (!resultados.contains(libro)) {
                        resultados.add(libro);
                    }
                }
            } else if (categoria != null && !categoria.equals("Todas")) {
                // Buscar por categoría
                resultados = libroDAO.buscarPorCategoria(categoria);
            } else {
                // Mostrar todos
                resultados = libroDAO.obtenerTodos();
            }

            listaLibros.clear();
            listaLibros.addAll(resultados);
            actualizarTotal();

            System.out.println("✓ Búsqueda completada: " + resultados.size() + " resultados");

        } catch (Exception e) {
            System.err.println("✗ Error en búsqueda: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al realizar la búsqueda", Alert.AlertType.ERROR);
        }
    }

    /**
     * Limpia los filtros y recarga todos los libros
     */
    @FXML
    private void handleLimpiar() {
        txtBuscar.clear();
        cbCategoria.setValue(null);
        cargarLibros();
    }

    /**
     * Abre el formulario para crear un nuevo libro
     */
    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LibroForm.fxml"));
            Scene scene = new Scene(loader.load());

            LibroFormController controller = loader.getController();
            controller.setLibroDAO(libroDAO);

            Stage stage = new Stage();
            stage.setTitle("Nuevo Libro");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

            // Recargar libros después de cerrar el formulario
            cargarLibros();
            cargarCategorias();

        } catch (Exception e) {
            System.err.println("✗ Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario", Alert.AlertType.ERROR);
        }
    }

    /**
     * Abre el formulario para editar el libro seleccionado
     */
    @FXML
    private void handleEditar() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un libro", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LibroForm.fxml"));
            Scene scene = new Scene(loader.load());

            LibroFormController controller = loader.getController();
            controller.setLibroDAO(libroDAO);
            controller.setLibro(libroSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Libro");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

            // Recargar libros
            cargarLibros();
            cargarCategorias();

        } catch (Exception e) {
            System.err.println("✗ Error al abrir formulario de edición: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario", Alert.AlertType.ERROR);
        }
    }

    /**
     * Elimina el libro seleccionado
     */
    @FXML
    private void handleEliminar() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un libro", Alert.AlertType.WARNING);
            return;
        }

        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar este libro?");
        confirmacion.setContentText(libroSeleccionado.getTitulo() + " - " + libroSeleccionado.getAutor());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = libroDAO.eliminar(libroSeleccionado.getId());

                if (eliminado) {
                    mostrarAlerta("Éxito", "Libro eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarLibros();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el libro", Alert.AlertType.ERROR);
                }

            } catch (Exception e) {
                System.err.println("✗ Error al eliminar libro: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Muestra los detalles del libro seleccionado
     */
    @FXML
    private void handleVerDetalles() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un libro", Alert.AlertType.WARNING);
            return;
        }

        String detalles = String.format(
                "ID: %d\n" +
                        "Título: %s\n" +
                        "Autor: %s\n" +
                        "Categoría: %s\n" +
                        "Editorial: %s\n" +
                        "Año: %d\n" +
                        "ISBN: %s\n" +
                        "Stock: %d\n" +
                        "Estado: %s",
                libroSeleccionado.getId(),
                libroSeleccionado.getTitulo(),
                libroSeleccionado.getAutor(),
                libroSeleccionado.getCategoria(),
                libroSeleccionado.getEditorial(),
                libroSeleccionado.getAnioPublicacion(),
                libroSeleccionado.getIsbn(),
                libroSeleccionado.getStock(),
                libroSeleccionado.estaDisponible() ? "Disponible" : "No disponible"
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Libro");
        alert.setHeaderText(libroSeleccionado.getTitulo());
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