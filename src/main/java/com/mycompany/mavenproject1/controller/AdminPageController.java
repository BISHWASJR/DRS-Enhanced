package com.mycompany.mavenproject1.controller;

import com.mycompany.mavenproject1.model.UserModel;
import javafx.collections.FXCollections;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import com.mycompany.mavenproject1.util.LogoutUtils;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * Controller class for handling the admin page functionality. Provides
 * functionality to view, edit, and delete users, and handles user logout.
 *
 * @author Bishwas Bhattarai
 */
public class AdminPageController {

    @FXML
    private TableView<UserModel> userTable;         // TableView for displaying users
    @FXML
    private TableColumn<UserModel, String> usernameColumn; // Column for displaying usernames
    @FXML
    private TableColumn<UserModel, String> roleColumn;     // Column for displaying user roles
    @FXML
    private TableColumn<UserModel, Void> actionsColumn;    // Column for edit/delete actions

    /**
     * Initializes the controller. Configures the TableView columns and loads
     * users from the database.
     */
    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Set up the actions column with edit and delete buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    handleEdit(user);
                });
                deleteBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editBtn, deleteBtn);
                    hbox.setSpacing(10);
                    setGraphic(hbox);
                }
            }
        });

        loadUsers(); // Load the users into the table when the page is initialized
    }

    /**
     * Loads the list of users from the database and populates the TableView.
     */
    public void loadUsers() {
        List<UserModel> users = DatabaseUtils.fetchAllUsers();
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Handles the editing of a user's role. Displays a dialog to input the new
     * role, updates the user's role in the database, and reloads the user list.
     *
     * @param user The user whose role is to be edited.
     */
    private void handleEdit(UserModel user) {
        TextInputDialog dialog = new TextInputDialog(user.getRole());
        dialog.setTitle("Edit User Role");
        dialog.setHeaderText("Change role for: " + user.getUsername());
        dialog.setContentText("Enter new role:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(role -> {
            DatabaseUtils.updateUserRole(user.getUsername(), role);
            loadUsers(); // Refresh the table after updating the role
        });
    }

    /**
     * Handles the deletion of a user. Displays a confirmation dialog before
     * deleting the user from the database.
     *
     * @param user The user to be deleted.
     */
    private void handleDelete(UserModel user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + user.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            DatabaseUtils.deleteUser(user.getUsername());
            loadUsers(); // Refresh the table after deleting the user
        }
    }

    /**
     * Handles the logout action for the admin. Invokes the logout utility
     * method to return to the login page.
     *
     * @param event The action event triggered by the logout button.
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event);
    }
}
