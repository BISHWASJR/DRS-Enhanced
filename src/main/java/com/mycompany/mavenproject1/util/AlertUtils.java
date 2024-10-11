package com.mycompany.mavenproject1.util;

import javafx.scene.control.Alert;

/**
 * Utility class for displaying alert dialogs in the application. Provides a
 * simple method to show alerts of different types, with a title and message.
 *
 * @author Bishwas Bhattarai
 */
public class AlertUtils {

    /**
     * Displays an alert dialog with the specified alert type, title, and
     * message.
     *
     * @param alertType the type of alert (e.g., INFORMATION, WARNING, ERROR)
     * @param title the title of the alert window
     * @param message the message content to display in the alert
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // No header for the alert
        alert.setContentText(message);  // Set the message content
        alert.showAndWait();  // Display the alert and wait for user to close it
    }
}
