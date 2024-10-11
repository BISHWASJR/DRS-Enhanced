package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.util.AlertUtils;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * Controller class responsible for handling the registration page logic. Allows
 * users to register by providing a username, password, email, phone number, and
 * role. Validates user input and saves the new user in the database.
 *
 * @author Bishwas Bhattarai
 */
public class RegisterPageController implements Initializable {

    @FXML
    private TextField usernameField;         // TextField for entering the username
    @FXML
    private PasswordField passwordField;     // PasswordField for entering the password
    @FXML
    private PasswordField reenterPasswordField; // PasswordField for re-entering the password
    @FXML
    private TextField emailField;            // TextField for entering the email
    @FXML
    private TextField phoneNumberField;      // TextField for entering the phone number
    @FXML
    private ComboBox<String> roleComboBox;   // ComboBox for selecting the role (User, Coordinator, Department, Admin)
    @FXML
    private Button registerBtn;              // Button to trigger the registration

    /**
     * Initializes the controller by populating the role ComboBox with default
     * values and setting the default value to "User".
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if not known.
     * @param resourceBundle the resources used to localize the root object, or
     * null if not needed.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleComboBox.getItems().addAll("User", "Coordinator", "Department", "Admin");
        roleComboBox.setValue("User"); // Default value
    }

    /**
     * Handles the registration process when the register button is clicked.
     * Validates user input (password matching, email format, phone number
     * validity) and saves the user in the database. Displays error messages for
     * invalid input or duplicates, and a success message for successful
     * registration.
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String reenteredPassword = reenterPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        String role = roleComboBox.getValue();

        // Check if passwords match
        if (!password.equals(reenteredPassword)) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Error", "Passwords do not match!");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Error", "Invalid email format!");
            return;
        }

        // Validate phone number (should be numeric and have more than 8 digits)
        if (!isValidPhoneNumber(phoneNumber)) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Error", "Invalid phone number");
            return;
        }

        // Check if the username, email, or phone number already exists in the database
        if (DatabaseUtils.userExists(username, email, phoneNumber)) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Error", "Username, email, or phone number already exists!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Failed", "Invalid email format");
            return;
        }

        if (password == null || password.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Failed", "Password cannot be empty");
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Registration Failed", "Username cannot be empty.");
            return;
        }

        // Save the user to the database if no duplicates are found
        DatabaseUtils.saveUser(username, password, email, phoneNumber, role);
        AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User registered successfully!");

        // Load the login page after successful registration
        loadLoginPage();
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param email the email string to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return email.matches(emailRegex);
    }

    /**
     * Validates the phone number to ensure it is numeric and has more than 8
     * digits.
     *
     * @param phoneNumber the phone number string to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{9,}");  // Ensures phone number is numeric and has at least 9 digits
    }

    /**
     * Loads the login page after successful registration. Redirects the user to
     * the login page.
     */
    private void loadLoginPage() {
        try {
            // Load the LoginPage.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/LoginPage.fxml"));

            // Get the current stage and set the new scene (Login page)
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event to go back to the login page. Triggers the logout
     * process to return to the login screen.
     *
     * @param event the action event triggered by the back to login button
     * click.
     */
    public void handleBackToLogin(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }
}
