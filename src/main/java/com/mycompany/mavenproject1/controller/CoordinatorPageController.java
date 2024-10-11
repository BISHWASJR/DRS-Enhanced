package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.model.AssignedTask;
import com.mycompany.mavenproject1.model.DisasterReport;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller class for managing the coordinator page. Handles the display of
 * disaster reports, assignment of tasks, saving priorities, and viewing
 * notifications. This controller interacts with the database to fetch disaster
 * reports and update their priorities. It also manages navigation to other
 * pages, such as the Assign Task page, and allows the user to logout.
 *
 * @author Bishwas Bhattarai
 */
public class CoordinatorPageController {

    @FXML
    private TableView<DisasterReport> disasterTable;  // TableView to display disaster reports
    @FXML
    private TableColumn<DisasterReport, Integer> idColumn;  // Column for disaster report IDs
    @FXML
    private TableColumn<DisasterReport, String> usernameColumn;  // Column for the username of the reporter
    @FXML
    private TableColumn<DisasterReport, String> disasterTypeColumn;  // Column for the disaster type
    @FXML
    private TableColumn<DisasterReport, String> locationColumn;  // Column for the location of the disaster
    @FXML
    private TableColumn<DisasterReport, String> severityColumn;  // Column for the severity of the disaster
    @FXML
    private TableColumn<DisasterReport, String> descriptionColumn;  // Column for the description of the disaster
    @FXML
    private TableColumn<DisasterReport, String> priorityColumn;  // Column for the priority of the disaster
    @FXML
    private TableColumn<DisasterReport, LocalDateTime> reportTimeColumn;  // Column for the time the disaster was reported

    private ObservableList<DisasterReport> disasterReports;  // List of disaster reports to be displayed in the table
    @FXML
    private TextArea notificationArea;  // TextArea to display notifications

    /**
     * Initializes the controller by setting up the columns in the disaster
     * reports table. Loads disaster reports from the database and binds them to
     * the table for display.
     */
    @FXML
    public void initialize() {
        // Set up the columns in the table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        disasterTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityDropdown"));
        reportTimeColumn.setCellValueFactory(new PropertyValueFactory<>("reportTime"));  // Bind report time column

        // Load the data from the database
        loadDisasterReports();
    }

    /**
     * Loads disaster reports from the database and populates the disaster table
     * with them. Handles any SQL exceptions that occur during the process.
     */
    private void loadDisasterReports() {
        try {
            List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
            disasterReports = FXCollections.observableArrayList(reports);
            disasterTable.setItems(disasterReports);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the selected priority values for each disaster report to the
     * database. Iterates over the disaster reports in the table and updates
     * their priorities in the database.
     */
    @FXML
    private void handleSavePriorities() {
        for (DisasterReport report : disasterReports) {
            String selectedPriority = report.getPriorityDropdown().getValue();
            try {
                DatabaseUtils.updateDisasterPriority(report.getId(), selectedPriority);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the logout process by redirecting the user to the login page.
     *
     * @param event the action event triggered by the logout button click
     */
    public void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }

    /**
     * Redirects the user to the AssignTask page when the assign task button is
     * clicked. Loads the AssignTaskPage.fxml file and switches the scene to
     * display the assign task page.
     *
     * @param event the action event triggered by the assign task button click
     */
    @FXML
    public void handleAssignTask(ActionEvent event) {
        try {
            // Load the AssignTask page when the button is clicked
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/mavenproject1/view/AssignTaskPage.fxml"));
            Parent root = loader.load();

            // Switch the scene to the Assign Task page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Assign Task");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a notification message to the notification area. Appends a new
     * notification message to the notification area TextArea.
     *
     * @param message the notification message to be appended
     */
    public void addNotification(String message) {
        notificationArea.appendText(message + "\n");
    }

    /**
     * Loads and displays notifications about finished tasks. Fetches finished
     * tasks from the database and displays them in the notification area. If no
     * tasks are finished, a message indicating this is displayed.
     */
    @FXML
    private void handleViewNotifications() {
        try {
            List<AssignedTask> finishedTasks = DatabaseUtils.getAllFinishedTasks();

            // Display finished tasks in the notificationArea
            if (finishedTasks.isEmpty()) {
                notificationArea.setText("No finished tasks at the moment.");
            } else {
                StringBuilder notifications = new StringBuilder();
                for (AssignedTask task : finishedTasks) {
                    notifications.append("Disaster ID: ").append(task.getDisasterId())
                            .append(", Task: ").append(task.getTaskDescription())
                            .append(", Department: ").append(task.getDepartment())
                            .append(" is completed ").append("\n");
                }
                notificationArea.setText(notifications.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notificationArea.setText("Error loading notifications.");
        }
    }
}
