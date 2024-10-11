package com.mycompany.mavenproject1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for launching the JavaFX application. This class initializes the
 * application and loads the login page as the starting point.
 *
 * @author Bishwas Bhattarai
 */
public class App extends Application {

    /**
     * The main entry point for the JavaFX application. This method is
     * automatically called when the application is launched.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception if there is an issue loading the FXML resource.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login page from the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/LoginPage.fxml"));

        // Set the title of the application window
        primaryStage.setTitle("login page");

        // Set the scene for the primary stage
        primaryStage.setScene(new Scene(root, 400, 400));

        // Show the primary stage
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
