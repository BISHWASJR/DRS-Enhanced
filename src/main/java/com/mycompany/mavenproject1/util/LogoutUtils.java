package com.mycompany.mavenproject1.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;

/**
 * Utility class for handling logout functionality in the application. Provides
 * a method to log the user out and redirect to the login page.
 *
 * @author Bishwas Bhattarai
 */
public class LogoutUtils {

    /**
     * Logs the user out and redirects them to the login page.
     *
     * @param event the ActionEvent triggered by the logout action.
     */
    public static void handleLogout(ActionEvent event) {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(LogoutUtils.class.getResource("/com/mycompany/mavenproject1/view/LoginPage.fxml"));
            Parent loginPageParent = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene (login page) on the stage
            Scene scene = new Scene(loginPageParent);
            stage.setScene(scene);
            stage.setTitle("Login page");

            // Show the login page
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
