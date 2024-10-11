/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.mycompany.mavenproject1.model.AssignedTask;
import com.mycompany.mavenproject1.model.DisasterReport;
import com.mycompany.mavenproject1.util.DatabaseUtils;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Platform;

public class AppTest {

    private static Connection connection;

    @BeforeAll
    static void setupAll() throws SQLException {
        System.out.println("Setting up before all tests.");
        connection = DatabaseUtils.getConnection();
        System.out.println("Database connection established.");
        DatabaseUtils.saveUser("testuser", "password123", "testuser@example.com", "9876543210", "User");
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void setup() {
        System.out.println("Setting up before each test.");
    }

    @Test
    public void testSuccessfulRegistration() throws SQLException {
        DatabaseUtils.saveUser("registerUser", "password123", "registeruser@example.com", "9876543212", "User");
        boolean isRegistered = DatabaseUtils.isValidLogin("registerUser", "password123");
        assertTrue(isRegistered, "User should be registered successfully.");
        DatabaseUtils.deleteUser("registerUser");
    }

    @Test
    public void testInvalidEmailRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("invalidEmailUser", "password123", "invalidEmail", "9876543214", "User");
        });
        String expectedMessage = "Invalid email format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyPasswordRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("emptyPasswordUser", "", "emptypassworduser@test.com", "9876543215", "User");
        });
        String expectedMessage = "Password cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected an IllegalArgumentException for an empty password.");
    }

    @Test
    public void testValidLogin() {
        boolean result = DatabaseUtils.isValidLogin("testuser", "password123");
        assertTrue(result);
    }

    @Test
    public void testEmptyUsernameRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("", "password123", "emptyusername@example.com", "9876543215", "User");
        });
        String expectedMessage = "Username cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected an IllegalArgumentException for an empty username.");
    }

    @Test
    public void testInvalidLogin() {
        boolean result = DatabaseUtils.isValidLogin("testuser", "wrongpassword");
        assertFalse(result);
    }

    @Test
    public void testEmptyUsername() {
        boolean isLoggedIn = DatabaseUtils.isValidLogin("", "password123");
        assertFalse(isLoggedIn, "Login should fail if the username is empty.");
    }

    @Test
    public void testGetUserRole() {
        String role = DatabaseUtils.getUserRole("bishwas");
        assertEquals("User", role);
    }

    @Test
    public void testSaveUser() {
        DatabaseUtils.saveUser("newuser", "password123", "newuser@example.com", "9876543210", "User");
        boolean result = DatabaseUtils.isValidLogin("newuser", "password123");
        assertTrue(result);
    }

    @Test
    public void testUpdateUserRole() {
        DatabaseUtils.updateUserRole("testuser", "Coordinator");
        String role = DatabaseUtils.getUserRole("testuser");
        assertEquals("Coordinator", role);
    }

    @Test
    public void testDeleteUser() {
        DatabaseUtils.deleteUser("newuser");
        boolean result = DatabaseUtils.isValidLogin("newuser", "password123");
        assertFalse(result);
    }

    @Test
    public void testSaveDisasterReport() throws SQLException {
        DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location A", 8, "Severe fire");
        List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
        assertFalse(reports.isEmpty());
        assertTrue(reports.stream().anyMatch(r -> r.getUsername().equals("testuser") && r.getDisasterType().equals("Fire") && r.getLocation().equals("Location A")));
        DatabaseUtils.deleteDisasterReport("testuser", "Fire", "Location A");
    }

    @Test
    public void testGetAssignedTasksSortedByPriority() throws SQLException {
        List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void testUpdateTaskStatus() throws SQLException {
        DatabaseUtils.updateTaskStatus(1, "Finished");
        List<AssignedTask> finishedTasks = DatabaseUtils.getAllFinishedTasks();
        assertTrue(finishedTasks.stream().anyMatch(task -> task.getDisasterId() == 1 && task.getStatus().equals("Finished")));
    }

    @Test
    public void testGetAllFinishedTasks() throws SQLException {
        List<AssignedTask> tasks = DatabaseUtils.getAllFinishedTasks();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void testGetFinishedTasksForUser() throws SQLException {
        List<AssignedTask> tasks = DatabaseUtils.getFinishedTasksForUser("bishwas");
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void testDuplicateUsername() {
        DatabaseUtils.saveUser("duplicateuser", "password123", "duplicate@test.com", "9876543211", "User");
        boolean userExists = DatabaseUtils.userExists("duplicateuser", "duplicate@test.com", "9876543211");
        assertTrue(userExists);
        DatabaseUtils.deleteUser("duplicateuser");
    }

    @Test
    public void testNonExistentUserRole() {
        String role = DatabaseUtils.getUserRole("nonexistentuser");
        assertNull(role, "Expected null as the user does not exist.");
    }

    @Test
    public void testPasswordEncryption() {
        String password = "bishwas123";
        String hashedPassword = DatabaseUtils.hashPassword(password);
        assertNotEquals(password, hashedPassword, "Hashed password should not be equal to the plain text password.");
    }

    @Test
    public void testIsEmailAndPhoneRegistered() {
        DatabaseUtils.saveUser("emailuser", "password123", "emailuser@test.com", "9876543212", "User");
        boolean result = DatabaseUtils.isEmailAndPhoneRegistered("emailuser@test.com", "9876543212");
        assertTrue(result, "The email and phone number should be registered.");
        DatabaseUtils.deleteUser("emailuser");
    }

    @Test
    public void testInvalidEmailAndPhoneRegistration() {
        boolean result = DatabaseUtils.isEmailAndPhoneRegistered("nonexistent@test.com", "1234567890");
        assertFalse(result, "The email and phone number should not be registered.");
    }

    @Test
    public void testUpdateTaskStatusToInProgress() throws SQLException {
        DatabaseUtils.updateTaskStatus(1, "Still in Process");
        List<AssignedTask> inProgressTasks = DatabaseUtils.getAssignedTasksSortedByPriority();
        assertTrue(inProgressTasks.stream().anyMatch(task -> task.getDisasterId() == 1 && task.getStatus().equals("Still in Process")));
    }

    @Test
    public void testGetAllDisasterReports() throws SQLException {
        List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
        assertNotNull(reports);
        assertFalse(reports.isEmpty(), "There should be some disaster reports in the database.");
    }

    @Test
    public void testSaveAssignedTask() throws SQLException {
        DatabaseUtils.saveAssignedTask(1, "Evacuation Department", "Handle evacuation process.");
        List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
        assertTrue(tasks.stream().anyMatch(task -> task.getTaskDescription().equals("Handle evacuation process.")));
        DatabaseUtils.deleteAssignedTask(1, "Evacuation Department", "Handle evacuation process.");
    }

    @Test
    public void testFetchAllUsers() {
        List<com.mycompany.mavenproject1.model.UserModel> users = DatabaseUtils.fetchAllUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty(), "There should be at least one user in the database.");
    }

    @Test
    public void testEmptyDisasterType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("testuser", "", "Location A", 8, "Severe fire");
        });
        String expectedMessage = "Disaster type cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyLocation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("testuser", "Fire", "", 8, "Severe fire");
        });
        String expectedMessage = "Location cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptySeverity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location A", 0, "Severe fire");
        });
        String expectedMessage = "Severity must be greater than 0";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location A", 8, "");
        });
        String expectedMessage = "Description cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Tearing down after each test.");
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        System.out.println("Tearing down after all tests.");
        DatabaseUtils.deleteUser("newuser2");
        DatabaseUtils.deleteUser("testuser");
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }

}
