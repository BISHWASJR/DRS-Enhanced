package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.model.AssignedTask;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import javafx.scene.control.TableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.ComboBox;

/**
 * Controller class for managing the organization page. This class handles the
 * display of assigned tasks, updating the task status, and logout
 * functionality. It interacts with the database to load and update task
 * details, and allows users to notify the coordinator upon task completion.
 *
 * @author Bishwas Bhattarai
 */
public class OrganizationPageController {

    @FXML
    private TableView<AssignedTask> taskTable;                // Table to display assigned tasks
    @FXML
    private TableColumn<AssignedTask, Integer> disasterIdColumn; // Column for disaster ID
    @FXML
    private TableColumn<AssignedTask, String> disasterTypeColumn; // Column for disaster type
    @FXML
    private TableColumn<AssignedTask, String> locationColumn;   // Column for disaster location
    @FXML
    private TableColumn<AssignedTask, String> departmentColumn; // Column for department
    @FXML
    private TableColumn<AssignedTask, String> taskDescriptionColumn; // Column for task description
    @FXML
    private TableColumn<AssignedTask, String> priorityColumn;   // Column for task priority
    @FXML
    private TableColumn<AssignedTask, String> statusColumn;     // Column for task status (editable with ComboBox)

    private ObservableList<AssignedTask> taskList;              // ObservableList to hold assigned tasks

    /**
     * Initializes the controller and sets up the task table columns. Loads
     * assigned tasks from the database and enables task status updates. It also
     * allows users to mark tasks as finished, notifying the coordinator.
     */
    @FXML
    public void initialize() {
        // Set up the columns in the table
        disasterIdColumn.setCellValueFactory(new PropertyValueFactory<>("disasterId"));
        disasterTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        // Set up the status column with a ComboBox for updating task status
        statusColumn.setCellFactory(col -> {
            return new TableCell<AssignedTask, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList("Still in Process", "Finished"));

                {
                    comboBox.setOnAction(event -> {
                        AssignedTask task = getTableView().getItems().get(getIndex());
                        task.setStatus(comboBox.getValue());

                        // Update the status in the database
                        try {
                            DatabaseUtils.updateTaskStatus(task.getDisasterId(), comboBox.getValue());

                            // Send notification to the coordinator if the status is changed to "Finished"
                            if ("Finished".equals(comboBox.getValue())) {
                                notifyCoordinator(task);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        AssignedTask task = getTableView().getItems().get(getIndex());
                        comboBox.setValue(task.getStatus());  // Set the value from the database
                        setGraphic(comboBox);
                    }
                }
            };
        });

        // Load the tasks from the database
        loadAssignedTasks();
    }

    /**
     * Sends a notification to the coordinator when a task is marked as
     * "Finished". The notification informs the coordinator about the completion
     * of the task.
     *
     * @param task the task that has been marked as finished.
     */
    private void notifyCoordinator(AssignedTask task) {
        // Logic to send notification to coordinator
        System.out.println("Task ID: " + task.getDisasterId() + " is marked as Finished.");
    }

    /**
     * Loads the assigned tasks from the database, sorted by priority. Populates
     * the task table with the retrieved tasks and handles any potential SQL
     * exceptions.
     */
    private void loadAssignedTasks() {
        try {
            // Retrieve the assigned tasks from the database, sorted by priority
            List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
            taskList = FXCollections.observableArrayList(tasks);
            taskTable.setItems(taskList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the logout process by redirecting the user to the login page.
     * This method is triggered when the user clicks the logout button.
     *
     * @param event the action event triggered by the logout button click.
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }
}
