package com.biblioteca.vista;

import com.biblioteca.controlador.PrestamoDAO;
import com.biblioteca.modelo.Prestamo;
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
 * Controlador para la gestión de préstamos
 * 
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class PrestamosController {
    
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colUsuario;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, LocalDate> colFechaPrestamo;
    @FXML private TableColumn<Prestamo, LocalDate> colFechaDevolucion;
    @FXML private TableColumn<Prestamo, Prestamo.EstadoPrestamo> colEstado;
    @FXML private TableColumn<Prestamo, Long> colDiasRestantes;
    @FXML private Label lblTotal;
    @FXML private Button btnNuevo;
    @FXML private Button btnDevolver;
    @FXML private Button btnRenovar;
    @FXML private Button btnEliminar;
    @FXML private Button btnDetalles;
    
    private PrestamoDAO prestamoDAO;
    private ObservableList<Prestamo> listaPrestamos;
    private Usuario usuarioActual;
    
    @FXML
    public void initialize() {
        prestamoDAO = new PrestamoDAO();
        listaPrestamos = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarFiltros();
        cargarPrestamos();
        
        tablaPrestamos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                boolean esPendiente = newSelection != null && 
                                     newSelection.getEstado() != Prestamo.EstadoPrestamo.DEVUELTO;
                
                btnDevolver.setDisable(!esPendiente);
                btnRenovar.setDisable(!esPendiente);
                btnEliminar.setDisable(!haySeleccion);
                btnDetalles.setDisable(!haySeleccion);
            }
        );
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsuario.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreUsuario()));
        colLibro.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTituloLibro()));
        colFechaPrestamo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaPrestamo()));
        colFechaDevolucion.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaDevolucionEsperada()));
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEstado()));
        colDiasRestantes.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getDiasRestantes()).asObject());
        
        // Estilo para estado
        colEstado.setCellFactory(column -> new TableCell<Prestamo, Prestamo.EstadoPrestamo>() {
            @Override
            protected void updateItem(Prestamo.EstadoPrestamo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDescripcion());
                    switch (item) {
                        case PENDIENTE:
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100;");
                            break;
                        case RETRASADO:
                            setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            break;
                        case DEVUELTO:
                            setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;");
                            break;
                        case RENOVADO:
                            setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0;");
                            break;
                    }
                }
            }
        });
        
        // Estilo para días restantes
        colDiasRestantes.setCellFactory(column -> new TableCell<Prestamo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    if (prestamo.getEstado() == Prestamo.EstadoPrestamo.DEVUELTO) {
                        setText("—");
                        setStyle("");
                    } else {
                        setText(item.toString());
                        if (item < 0) {
                            setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                        } else if (item <= 3) {
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            }
        });
        
        tablaPrestamos.setItems(listaPrestamos);
    }
    
    private void cargarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList(
            "Todos", "PENDIENTE", "RETRASADO", "DEVUELTO", "RENOVADO"
        ));
    }
    
    private void cargarPrestamos() {
        try {
            // Actualizar estados retrasados primero
            prestamoDAO.actualizarPrestamosRetrasados();
            
            List<Prestamo> prestamos = prestamoDAO.obtenerTodos();
            listaPrestamos.clear();
            listaPrestamos.addAll(prestamos);
            actualizarTotal();
            System.out.println("✓ Préstamos cargados: " + prestamos.size());
        } catch (Exception e) {
            System.err.println("✗ Error al cargar préstamos: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los préstamos", Alert.AlertType.ERROR);
        }
    }
    
    private void actualizarTotal() {
        lblTotal.setText("Total: " + listaPrestamos.size() + " préstamos");
    }
    
    @FXML
    private void mostrarTodos() {
        cbEstado.setValue("Todos");
        cargarPrestamos();
    }
    
    @FXML
    private void mostrarRetrasados() {
        try {
            List<Prestamo> retrasados = prestamoDAO.obtenerPrestamosRetrasados();
            listaPrestamos.clear();
            listaPrestamos.addAll(retrasados);
            actualizarTotal();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar préstamos retrasados", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void mostrarActivos() {
        try {
            List<Prestamo> activos = prestamoDAO.obtenerPrestamosActivos();
            listaPrestamos.clear();
            listaPrestamos.addAll(activos);
            actualizarTotal();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar préstamos activos", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrestamoForm.fxml"));
            Scene scene = new Scene(loader.load());
            
            PrestamoFormController controller = loader.getController();
            controller.setPrestamoDAO(prestamoDAO);
            
            Stage stage = new Stage();
            stage.setTitle("Nuevo Préstamo");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            cargarPrestamos();
            
        } catch (Exception e) {
            System.err.println("✗ Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleDevolver() {
        Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
        
        if (prestamoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un préstamo", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Devolución");
        confirmacion.setHeaderText("¿Registrar devolución de este préstamo?");
        confirmacion.setContentText("Libro: " + prestamoSeleccionado.getTituloLibro() + "\n" +
                                   "Usuario: " + prestamoSeleccionado.getNombreUsuario());
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean devuelto = prestamoDAO.registrarDevolucion(prestamoSeleccionado.getId());
                
                if (devuelto) {
                    mostrarAlerta("Éxito", "Devolución registrada correctamente", Alert.AlertType.INFORMATION);
                    cargarPrestamos();
                } else {
                    mostrarAlerta("Error", "No se pudo registrar la devolución", Alert.AlertType.ERROR);
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error al registrar devolución: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleRenovar() {
        Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
        
        if (prestamoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un préstamo", Alert.AlertType.WARNING);
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("14");
        dialog.setTitle("Renovar Préstamo");
        dialog.setHeaderText("¿Cuántos días deseas extender el préstamo?");
        dialog.setContentText("Días adicionales:");
        
        Optional<String> resultado = dialog.showAndWait();
        
        resultado.ifPresent(dias -> {
            try {
                int diasInt = Integer.parseInt(dias);
                if (diasInt <= 0 || diasInt > 30) {
                    mostrarAlerta("Error", "Ingresa un número entre 1 y 30", Alert.AlertType.ERROR);
                    return;
                }
                
                prestamoSeleccionado.renovarPrestamo(diasInt);
                boolean renovado = prestamoDAO.actualizar(prestamoSeleccionado);
                
                if (renovado) {
                    mostrarAlerta("Éxito", "Préstamo renovado por " + dias + " días", Alert.AlertType.INFORMATION);
                    cargarPrestamos();
                } else {
                    mostrarAlerta("Error", "No se pudo renovar el préstamo", Alert.AlertType.ERROR);
                }
                
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingresa un número válido", Alert.AlertType.ERROR);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    
    @FXML
    private void handleEliminar() {
        Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
        
        if (prestamoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un préstamo", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar este préstamo?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = prestamoDAO.eliminar(prestamoSeleccionado.getId());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Préstamo eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarPrestamos();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el préstamo", Alert.AlertType.ERROR);
                }
                
            } catch (Exception e) {
                mostrarAlerta("Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleVerDetalles() {
        Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
        
        if (prestamoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un préstamo", Alert.AlertType.WARNING);
            return;
        }
        
        String detalles = String.format(
            "ID: %d\n" +
            "Usuario: %s\n" +
            "Libro: %s\n" +
            "Fecha Préstamo: %s\n" +
            "Fecha Devolución Esperada: %s\n" +
            "Fecha Devolución Real: %s\n" +
            "Estado: %s\n" +
            "Días Restantes: %d\n" +
            "Observaciones: %s",
            prestamoSeleccionado.getId(),
            prestamoSeleccionado.getNombreUsuario(),
            prestamoSeleccionado.getTituloLibro(),
            prestamoSeleccionado.getFechaPrestamo(),
            prestamoSeleccionado.getFechaDevolucionEsperada(),
            prestamoSeleccionado.getFechaDevolucionReal() != null ? 
                prestamoSeleccionado.getFechaDevolucionReal() : "No devuelto",
            prestamoSeleccionado.getEstado(),
            prestamoSeleccionado.getDiasRestantes(),
            prestamoSeleccionado.getObservaciones() != null ? 
                prestamoSeleccionado.getObservaciones() : "—"
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Préstamo");
        alert.setHeaderText("Préstamo #" + prestamoSeleccionado.getId());
        alert.setContentText(detalles);
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}