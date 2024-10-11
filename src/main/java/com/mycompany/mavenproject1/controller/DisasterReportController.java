package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.model.AssignedTask;
import com.mycompany.mavenproject1.util.AlertUtils;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

/**
 * Controller class that manages disaster report submissions and related
 * notifications. Handles user inputs for disaster details such as type,
 * location, severity, and description. Also manages notifications related to
 * the completion of tasks assigned to the user's reported disasters. This class
 * interacts with the database to save disaster reports and fetch completed task
 * notifications.
 *
 * @author Bishwas Bhattarai
 */
public class DisasterReportController implements Initializable {

    @FXML
    private ComboBox<String> disasterTypeComboBox;  // ComboBox for selecting disaster type
    @FXML
    public TextField locationField;                // TextField for inputting disaster location
    @FXML
    public TextField severityField;                // TextField for inputting disaster severity
    @FXML
    public TextArea descriptionArea;               // TextArea for inputting disaster description
    @FXML
    private Button submitBtn;                      // Button to submit the disaster report
    @FXML
    private Button clearBtn;                       // Button to clear the form inputs
    @FXML
    private TextArea notificationArea;             // TextArea to display notifications about task completions

    private String loggedInUser;                   // Stores the username of the logged-in user

    /**
     * Initializes the controller. Populates the disasterTypeComboBox with
     * predefined disaster types. Sets up the button event handlers for
     * submitting and clearing disaster reports.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if not known
     * @param rb the resources used to localize the root object, or null if not
     * needed
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        disasterTypeComboBox.getItems().addAll("Hurricane", "Fire", "Earthquake", "Landslide", "Flood");

        submitBtn.setOnAction(e -> handleSubmit());
        clearBtn.setOnAction(e -> handleClear());
    }

    /**
     * Handles actions to load and display notifications for completed tasks
     * related to the user's disaster reports. Fetches the notifications from
     * the database and updates the notificationArea with relevant messages.
     */
    @FXML
    private void handleViewNotifications() {
        loadNotifications();
    }

    /**
     * Loads notifications from the database about tasks that have been marked
     * as completed. If tasks are completed, the notifications are displayed in
     * the notificationArea. If no tasks are completed, a message stating that
     * is displayed instead.
     */
    private void loadNotifications() {
        try {
            List<AssignedTask> completedTasks = DatabaseUtils.getFinishedTasksForUser(loggedInUser);
            if (completedTasks.isEmpty()) {
                notificationArea.setText("No completed tasks at the moment.");
            } else {
                StringBuilder notifications = new StringBuilder();
                for (AssignedTask task : completedTasks) {
                    notifications.append("Task for Disaster ID: ")
                            .append(task.getDisasterId())
                            .append(" (")
                            .append(task.getTaskDescription())
                            .append(") has been completed.\n");
                }
                notificationArea.setText(notifications.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notificationArea.setText("Error loading notifications.");
        }
    }

    /**
     * Sets the username of the logged-in user, which will be used to associate
     * disaster reports with the correct user. This method is typically called
     * after user authentication.
     *
     * @param username the username of the logged-in user
     */
    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }

    /**
     * Handles the submission of a new disaster report. Validates that all input
     * fields are filled out and that severity is a valid integer between 1 and
     * 10. Submits the report to the database if validation passes. Displays
     * alerts for any validation errors or success messages upon successful
     * submission.
     */
    private void handleSubmit() {
        String disasterType = disasterTypeComboBox.getValue();
        String location = locationField.getText();
        String severity = severityField.getText();
        String description = descriptionArea.getText();

        if (disasterType == null || location.isEmpty() || severity.isEmpty() || description.isEmpty()) {
            AlertUtils.showAlert(AlertType.WARNING, "Submission Error", "Please fill in all fields.");
            return;
        }

        try {
            int severityInt = Integer.parseInt(severity);
            if (severityInt < 1 || severityInt > 10) {
                AlertUtils.showAlert(AlertType.WARNING, "Submission Error", "Severity must be a number between 1 and 10.");
                return;
            }

            DatabaseUtils.saveDisasterReport(loggedInUser, disasterType, location, severityInt, description);
            AlertUtils.showAlert(AlertType.INFORMATION, "Submission Success", "Disaster report submitted successfully!");
            handleClear();
        } catch (NumberFormatException e) {
            AlertUtils.showAlert(AlertType.WARNING, "Submission Error", "Severity must be a valid number.");
        }
    }

    /**
     * Clears all input fields in the form, resetting the ComboBox, TextFields,
     * and TextArea. This method is called after a successful submission or when
     * the user clicks the clear button.
     */
    private void handleClear() {
        disasterTypeComboBox.setValue(null);
        locationField.clear();
        severityField.clear();
        descriptionArea.clear();
    }

    /**
     * Handles the logout process, redirecting the user back to the login page.
     * This method uses the LogoutUtils utility to manage the logout flow and
     * switch scenes.
     *
     * @param event the action event triggered by the logout button click
     */
    public void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }
}
