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
    DatabaseUtils.setupDatabase();  // Ensure database and tables are created
    connection = DatabaseUtils.getConnection();
    System.out.println("Database connection established.");
    DatabaseUtils.saveUser("testuser", "password123", "testuser@example.com", "9876543210", "User");
    Platform.startup(() -> {
    });
}

    @BeforeEach
    void setup() {
        DatabaseUtils.deleteUser("testuser");
    DatabaseUtils.saveUser("testuser", "password123", "testuser@example.com", "9876543210", "User");  
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
    DatabaseUtils.saveUser("bishwas", "password123", "bishwas@example.com", "9876543210", "User");
    String role = DatabaseUtils.getUserRole("bishwas");
    assertEquals("User", role, "The user role should be 'User'.");
    DatabaseUtils.deleteUser("bishwas");
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
    // Step 1: Insert disaster reports without priority
    DatabaseUtils.saveDisasterReport("testuser", "Earthquake", "Location A", 1, "Severe earthquake");
    int disasterId1 = DatabaseUtils.getDisasterReportId("testuser", "Earthquake", "Location A");

    DatabaseUtils.saveDisasterReport("testuser", "Flood", "Location B", 2, "Severe flood");
    int disasterId2 = DatabaseUtils.getDisasterReportId("testuser", "Flood", "Location B");

    DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location C", 3, "Severe fire");
    int disasterId3 = DatabaseUtils.getDisasterReportId("testuser", "Fire", "Location C");

    // Step 2: Update priority for each disaster report
    DatabaseUtils.updateDisasterPriority(disasterId1, "Very High");
    DatabaseUtils.updateDisasterPriority(disasterId2, "High");
    DatabaseUtils.updateDisasterPriority(disasterId3, "Medium");

    // Step 3: Insert assigned tasks for these disaster reports
    DatabaseUtils.saveAssignedTask(disasterId1, "Rescue Team", "Handle rescue operations");
    DatabaseUtils.saveAssignedTask(disasterId2, "Medical Team", "Provide medical aid");
    DatabaseUtils.saveAssignedTask(disasterId3, "Firefighters", "Control fire");

    // Step 4: Fetch tasks sorted by priority
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();

    // Step 5: Debugging output
    tasks.forEach(task -> System.out.println("Task: " + task.getTaskDescription() + ", Priority: " + task.getPriority()));

    // Step 6: Verify tasks are sorted by priority
    assertNotNull(tasks, "Assigned tasks list should not be null.");
    assertFalse(tasks.isEmpty(), "Assigned tasks list should not be empty.");

    // Check that tasks are sorted in expected order: Very High -> High -> Medium
    assertEquals("Very High", tasks.get(0).getPriority(), "The first task should have 'Very High' priority.");
    assertEquals("High", tasks.get(1).getPriority(), "The second task should have 'High' priority.");
    assertEquals("Medium", tasks.get(2).getPriority(), "The third task should have 'Medium' priority.");

    // Step 7: Cleanup
    DatabaseUtils.deleteAssignedTask(disasterId1, "Rescue Team", "Handle rescue operations");
    DatabaseUtils.deleteAssignedTask(disasterId2, "Medical Team", "Provide medical aid");
    DatabaseUtils.deleteAssignedTask(disasterId3, "Firefighters", "Control fire");

    DatabaseUtils.deleteDisasterReport("testuser", "Earthquake", "Location A");
    DatabaseUtils.deleteDisasterReport("testuser", "Flood", "Location B");
    DatabaseUtils.deleteDisasterReport("testuser", "Fire", "Location C");
}



    @Test
public void testUpdateTaskStatus() throws SQLException {
    DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location C", 7, "Severe fire");
    int disasterId = DatabaseUtils.getDisasterReportId("testuser", "Fire", "Location C");
    DatabaseUtils.saveAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    assertNotNull(tasks, "The task list should not be null.");
    assertFalse(tasks.isEmpty(), "There should be at least one task.");
    AssignedTask task = tasks.stream()
                             .filter(t -> t.getDisasterId() == disasterId)
                             .findFirst()
                             .orElseThrow(() -> new SQLException("Task not found"));
    
    System.out.println("Initial Task Status: " + task.getStatus());
    DatabaseUtils.updateTaskStatus(disasterId, "Finished");
    List<AssignedTask> updatedTasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    AssignedTask updatedTask = updatedTasks.stream()
                                           .filter(t -> t.getDisasterId() == disasterId)
                                           .findFirst()
                                           .orElseThrow(() -> new SQLException("Updated task not found"));
    System.out.println("Updated Task Status: " + updatedTask.getStatus());
    assertEquals("Finished", updatedTask.getStatus(), "The task status should be updated to 'Finished'.");
    DatabaseUtils.deleteAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    DatabaseUtils.deleteDisasterReport("testuser", "Fire", "Location C");
}

   @Test
public void testGetAllFinishedTasks() throws SQLException {
    DatabaseUtils.saveDisasterReport("testuser", "Flood", "Location B", 5, "Moderate flood");
    int disasterId = DatabaseUtils.getDisasterReportId("testuser", "Flood", "Location B");
    DatabaseUtils.saveAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");
    DatabaseUtils.updateTaskStatus(disasterId, "Finished");
    List<AssignedTask> finishedTasks = DatabaseUtils.getAllFinishedTasks();
    assertNotNull(finishedTasks, "Finished tasks list should not be null.");
    assertFalse(finishedTasks.isEmpty(), "There should be at least one finished task.");
    boolean taskFound = finishedTasks.stream()
                                     .anyMatch(task -> task.getDisasterId() == disasterId && 
                                                       task.getTaskDescription().equals("Handle rescue operations.") &&
                                                       task.getStatus().equals("Finished"));
    assertTrue(taskFound, "The finished task should be present in the result.");
    DatabaseUtils.deleteAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");
    DatabaseUtils.deleteDisasterReport("testuser", "Flood", "Location B");
}

@Test
public void testGetFinishedTasksForUser() throws SQLException {
    DatabaseUtils.saveDisasterReport("testuser", "Flood", "Location B", 5, "Minor flooding");
    int disasterId = DatabaseUtils.getDisasterReportId("testuser", "Flood", "Location B");
    DatabaseUtils.saveAssignedTask(disasterId, "testuser", "Handle flood evacuation");
    DatabaseUtils.updateTaskStatus(disasterId, "Finished");
    List<AssignedTask> tasks = DatabaseUtils.getFinishedTasksForUser("testuser");
    assertNotNull(tasks, "The list of finished tasks should not be null.");
    assertFalse(tasks.isEmpty(), "There should be at least one finished task for the user.");
    DatabaseUtils.deleteAssignedTask(disasterId, "testuser", "Handle flood evacuation");
    DatabaseUtils.deleteDisasterReport("testuser", "Flood", "Location B");
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
    // Step 1: Ensure that a disaster report exists for the task
    DatabaseUtils.saveDisasterReport("testuser", "Earthquake", "Location A", 7, "Severe earthquake");
    
    // Step 2: Retrieve the disaster report ID
    int disasterId = DatabaseUtils.getDisasterReportId("testuser", "Earthquake", "Location A");

    // Step 3: Save the assigned task
    DatabaseUtils.saveAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");

    // Step 4: Update the task status to "Still in Process"
    DatabaseUtils.updateTaskStatus(disasterId, "Still in Process");

    // Step 5: Retrieve the task and assert that the status has been updated
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    AssignedTask task = tasks.stream()
                             .filter(t -> t.getDisasterId() == disasterId)
                             .findFirst()
                             .orElseThrow(() -> new SQLException("Task not found"));
    
    // Step 6: Check if the task status is updated to "Still in Process"
    assertEquals("Still in Process", task.getStatus(), "The task status should be updated to 'Still in Process'.");

    // Cleanup: Delete the task and the disaster report after the test
  DatabaseUtils.deleteAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");

    // Step 5: Delete the disaster report
    DatabaseUtils.deleteDisasterReport("testuser", "Earthquake", "Location A");
}


@Test
public void testGetAllDisasterReports() throws SQLException {
    // Insert a disaster report to ensure there's at least one in the database
    DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location A", 8, "Severe fire");

    // Retrieve all disaster reports
    List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();

    // Assert that the list is not null
    assertNotNull(reports, "The disaster reports list should not be null.");

    // Assert that the list is not empty
    assertFalse(reports.isEmpty(), "There should be at least one disaster report in the database.");


    // Cleanup: Delete the disaster report after the test to maintain test isolation
    DatabaseUtils.deleteDisasterReport("testuser", "Fire", "Location A");
}

@Test
public void testSaveAssignedTask() throws SQLException {
    // Step 1: Save a disaster report and get the disaster report ID
    DatabaseUtils.saveDisasterReport("testuser", "Fire", "Location A", 8, "Severe fire");
    
    // Retrieve the ID of the disaster report you just created
    int disasterId = DatabaseUtils.getDisasterReportId("testuser", "Fire", "Location A");
    
    // Step 2: Use the disaster ID to save the assigned task
    DatabaseUtils.saveAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    
    // Step 3: Verify that the task was saved correctly
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    assertTrue(tasks.stream().anyMatch(task -> task.getTaskDescription().equals("Handle evacuation process.")));
 
    // Step 4: Clean up - delete the assigned task and disaster report
    DatabaseUtils.deleteAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    DatabaseUtils.deleteDisasterReport("testuser", "Fire", "Location A");
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
        DatabaseUtils.deleteUser("testuser");
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
