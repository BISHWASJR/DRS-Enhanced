package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.model.DisasterReport;
import com.mycompany.mavenproject1.util.AlertUtils;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.SQLException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller class responsible for handling the task assignment UI logic.
 * Manages the process of assigning tasks to different departments based on
 * disaster reports. Allows users to select a disaster, choose a department, and
 * assign a task. Provides functionality for navigating to the coordinator page
 * and logging out.
 *
 * @author Bishwas Bhattarai
 */
public class AssignTaskController {

    @FXML
    private ComboBox<Integer> disasterIdComboBox;  // ComboBox for disaster IDs
    @FXML
    private TextField disasterTypeField;           // TextField for disaster type (auto-filled)
    @FXML
    private TextField locationField;               // TextField for location (auto-filled)
    @FXML
    private ComboBox<String> departmentComboBox;   // ComboBox for selecting the department
    @FXML
    private TextArea taskDescriptionArea;          // TextArea for task description

    private ObservableList<DisasterReport> disasterReports;

    /**
     * Initializes the controller. Loads disaster reports to populate the
     * disaster ID ComboBox and sets department options.
     */
    @FXML
    public void initialize() {
        // Load disaster reports and populate the disaster ID dropdown
        loadDisasterIds();

        // Populate department ComboBox with options
        departmentComboBox.setItems(FXCollections.observableArrayList(
                "Evacuation Department",
                "Search and Rescue team",
                "Public Health Services",
                "Damage Assessment",
                "Infrastructure Restoration",
                "Debris Removal",
                "Fire Department",
                "Water Supply Department",
                "Hospital"
        ));
    }

    /**
     * Loads disaster reports from the database and populates the
     * disasterIdComboBox with disaster IDs. Fetches disaster reports and
     * handles any SQL exceptions.
     */
    private void loadDisasterIds() {
        try {
            List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
            disasterReports = FXCollections.observableArrayList(reports);

            // Add disaster IDs to the disasterIdComboBox
            for (DisasterReport report : disasterReports) {
                disasterIdComboBox.getItems().add(report.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showAlert(AlertType.ERROR, "Error", "Failed to load disaster reports.");
        }
    }

    /**
     * Event handler for when a disaster ID is selected from the ComboBox.
     * Populates the disaster type and location fields based on the selected
     * disaster.
     */
    @FXML
    private void handleDisasterIdSelection() {
        Integer selectedDisasterId = disasterIdComboBox.getValue();
        if (selectedDisasterId != null) {
            // Find the selected disaster report based on the ID
            DisasterReport selectedReport = disasterReports.stream()
                    .filter(report -> report.getId() == selectedDisasterId)
                    .findFirst()
                    .orElse(null);

            if (selectedReport != null) {
                // Populate the disaster type and location fields
                disasterTypeField.setText(selectedReport.getDisasterType());
                locationField.setText(selectedReport.getLocation());
            }
        }
    }

    /**
     * Handles the task assignment process when the 'Assign Task' button is
     * clicked. Validates the input fields and saves the task assignment to the
     * database. Shows alerts for any errors or success messages.
     */
    @FXML
    public void handleAssignTask() {
        Integer disasterId = disasterIdComboBox.getValue();
        String department = departmentComboBox.getValue();
        String taskDescription = taskDescriptionArea.getText();

        if (disasterId == null || department == null || taskDescription.isEmpty()) {
            AlertUtils.showAlert(AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return;
        }

        // Save the task assignment to the database
        try {
            DatabaseUtils.saveAssignedTask(disasterId, department, taskDescription);
            AlertUtils.showAlert(AlertType.INFORMATION, "Success", "Task successfully assigned and saved to the database.");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showAlert(AlertType.ERROR, "Database Error", "Failed to save task to the database.");
        }
    }

    /**
     * Redirects the user to the Coordinator Page when the relevant button is
     * clicked. Loads the CoordinatorPage.fxml file and sets the new scene.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleDisasterReportRedirect(ActionEvent event) {
        try {
            // Load the CoordinatorPage.fxml (the main page for the coordinator)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/mavenproject1/view/CoordinatorPage.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Coordinator Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the coordinator page.");
        }
    }

    /**
     * Handles the logout process by redirecting the user to the login page.
     *
     * @param event the action event triggered by the logout button click
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }

    /**
     * Clears all input fields in the form. Resets the disasterIdComboBox,
     * disasterTypeField, locationField, departmentComboBox, and
     * taskDescriptionArea.
     */
    private void clearFields() {
        disasterIdComboBox.setValue(null);  // Clear selected disaster ID
        disasterTypeField.clear();          // Clear disaster type field
        locationField.clear();              // Clear location field
        departmentComboBox.setValue(null);  // Clear selected department
        taskDescriptionArea.clear();        // Clear task description area
    }
}
