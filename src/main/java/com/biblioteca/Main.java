package com.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Clase principal de la aplicación Biblioteca Inteligente 1.0
 *
 * Esta clase inicia la aplicación JavaFX y carga la ventana principal.
 * Implementa el patrón MVC (Modelo-Vista-Controlador) para una arquitectura limpia.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class Main extends Application {

    /**
     * Método principal de inicio de JavaFX
     *
     * @param primaryStage Escenario principal de la aplicación
     * @throws Exception Si hay error al cargar la vista FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Cargar la vista principal desde archivo FXML
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/fxml/Login.fxml")
                    )
            );

            // Configurar la escena
            Scene scene = new Scene(root, 800, 600);

            // Opcional: Cargar hoja de estilos CSS
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/css/styles.css")
                    ).toExternalForm()
            );

            // Configurar el escenario principal
            primaryStage.setTitle("Biblioteca Inteligente 1.0");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            // Mostrar la ventana
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Método de entrada principal de la aplicación
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Iniciar la aplicación JavaFX
        launch(args);
    }
}