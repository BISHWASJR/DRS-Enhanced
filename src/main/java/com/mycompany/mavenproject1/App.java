package com.mycompany.mavenproject1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.mavenproject1.util.DatabaseUtils;
/**
 * Main class for launching the JavaFX application. This class initializes the
 * application and loads the login page as the starting point.
 *
 * @author Bishwas Bhattarai
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup the database and tables before loading the login page
        DatabaseUtils.setupDatabase();

        // Load the login page from the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/LoginPage.fxml"));

        primaryStage.setTitle("Login Page");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}








