package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.util.AlertUtils;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

/**
 * Controller class for managing the login page. This class handles user login,
 * password reset functionality, and redirection to appropriate pages based on
 * user roles (Coordinator, Department, Admin, etc.). It includes methods to
 * validate credentials, reset password, and handle user navigation.
 *
 * @author Bishwas Bhattarai
 */
public class LoginPageController {

    @FXML
    private TextField userNameField;          // TextField for entering the username
    @FXML
    private PasswordField passwordField;      // PasswordField for entering the password
    @FXML
    private Button forgotPasswordBtn;         // Button to trigger the forgot password flow
    @FXML
    private Button resetBtn;                  // Button to reset the username and password fields
    @FXML
    private Button signUpBtn;                 // Button to redirect to the registration page
    @FXML
    private Button loginBtn;                  // Button to trigger the login process

    /**
     * Initializes the controller by setting up event handlers for buttons on
     * the login page. Binds each button to its respective action, such as
     * login, reset, sign-up, and forgot password.
     */
    public void initialize() {
        loginBtn.setOnAction(e -> handleLogin());
        resetBtn.setOnAction(e -> handleReset());
        signUpBtn.setOnAction(e -> openRegisterPage());
        forgotPasswordBtn.setOnAction(e -> handleForgotPassword());
    }

    /**
     * Handles the login process when the login button is clicked. It validates
     * the username and password by querying the database and redirects the user
     * to the appropriate page based on their role.
     */
    void handleLogin() {
        String enteredUsername = userNameField.getText();
        String enteredPassword = passwordField.getText();

        if (DatabaseUtils.isValidLogin(enteredUsername, enteredPassword)) {
            String role = DatabaseUtils.getUserRole(enteredUsername);
            switch (role) {
                case "Coordinator":
                    loadCoordinatorPage();
                    break;
                case "Department":
                    loadOrganizationPage();
                    break;
                case "Admin":
                    loadAdminPage();
                    break;
                default:
                    loadDisasterReportPage(enteredUsername);
                    break;
            }
        } else {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
        }
    }

    /**
     * Loads the admin page for users with the "Admin" role. The method switches
     * the current scene to the Admin page.
     */
    private void loadAdminPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/AdminPage.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the disaster report page for regular users. It passes the logged-in
     * username to the page's controller.
     *
     * @param username the username of the logged-in user.
     */
    private void loadDisasterReportPage(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/mavenproject1/view/DisasterReportPage.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the logged-in username
            DisasterReportController controller = loader.getController();
            controller.setLoggedInUser(username);

            // Set the new scene (Disaster Report page)
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Disaster Report");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the coordinator page for users with the "Coordinator" role. This
     * method changes the current scene to the coordinator's page.
     */
    private void loadCoordinatorPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/CoordinatorPage.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Coordinator Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the organization (department) page for users with the "Department"
     * role. The method switches the current scene to the department's page.
     */
    private void loadOrganizationPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/OrganizationPage.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Organization Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the username and password fields when the reset button is clicked.
     * This method is used to reset the login form to its initial state.
     */
    private void handleReset() {
        userNameField.clear();
        passwordField.clear();
    }

    /**
     * Opens the registration page when the sign-up button is clicked. This
     * method switches the current scene to the registration page.
     */
    private void openRegisterPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/mavenproject1/view/RegisterPage.fxml"));
            Stage stage = (Stage) signUpBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays input dialogs for the user to provide their registered email and
     * phone number. Used in the forgot password flow.
     *
     * @return a string array containing the entered email and phone number, or
     * null if canceled.
     */
    public String[] showEmailPhoneInputDialog() {
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Forgot Password");
        emailDialog.setHeaderText("Enter your registered email address and phone number");

        emailDialog.setContentText("Email:");

        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setContentText("Phone:");

        String email = emailDialog.showAndWait().orElse(null);
        String phone = phoneDialog.showAndWait().orElse(null);

        if (email != null && phone != null) {
            return new String[]{email, phone};
        }

        return null; // Return null if canceled
    }

    /**
     * Handles the forgot password flow by prompting the user to enter their
     * registered email and phone number, validating the input, and allowing
     * them to reset their password.
     */
    @FXML
    private void handleForgotPassword() {
        String[] userInput = showEmailPhoneInputDialog();

        if (userInput == null || userInput[0] == null || userInput[1] == null) {
            AlertUtils.showAlert(AlertType.WARNING, "Forgot Password", "Email and phone number are required!");
            return;
        }

        String email = userInput[0];
        String phone = userInput[1];

        if (!DatabaseUtils.isEmailAndPhoneRegistered(email, phone)) {
            AlertUtils.showAlert(AlertType.WARNING, "Forgot Password", "Invalid email or phone number!");
            return;
        }

        String newPassword = showPasswordResetDialog();
        if (newPassword == null || newPassword.isEmpty()) {
            AlertUtils.showAlert(AlertType.WARNING, "Forgot Password", "Password reset canceled.");
            return;
        }

        DatabaseUtils.updateUserPassword(email, newPassword);
        AlertUtils.showAlert(AlertType.INFORMATION, "Forgot Password", "Password reset successful!");
    }

    /**
     * Displays a dialog for the user to input a new password during the
     * password reset process.
     *
     * @return the entered new password, or null if canceled.
     */
    public String showPasswordResetDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your new password:");
        dialog.setContentText("New Password:");

        return dialog.showAndWait().orElse(null);
    }
}
