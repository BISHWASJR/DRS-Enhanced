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

    DatabaseUtils.setupDatabase();  // Ensure database and tables are created
    connection = DatabaseUtils.getConnection();
    DatabaseUtils.saveUser("ram", "ram@123", "ram@gmail.com", "9841407177", "User");
    Platform.startup(() -> {
    });
}

    @BeforeEach
    void setup() {
        DatabaseUtils.deleteUser("ram");
    DatabaseUtils.saveUser("ram", "ram@123", "ram@gmail.com", "9841407177", "User");  
    }

    @Test
    public void testSuccessfulRegistration() throws SQLException {
        DatabaseUtils.saveUser("thanu", "thanu@123", "thanu@gmail.com", "9876543212", "User");
        boolean isRegistered = DatabaseUtils.isValidLogin("thanu", "thanu@123");
        assertTrue(isRegistered, "User should be registered successfully.");
        DatabaseUtils.deleteUser("thanu");
    }

    @Test
    public void testInvalidEmailRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("pursoth", "pursoth123", "pursoth.com", "9876543214", "User");
        });
        String expectedMessage = "Invalid email format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyPasswordRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("ashim", "", "ashim@gmail.com", "9876543215", "User");
        });
        String expectedMessage = "Password cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected an IllegalArgumentException for an empty password.");
    }

    @Test
    public void testValidLogin() {
        boolean result = DatabaseUtils.isValidLogin("ram", "ram@123");
        assertTrue(result);
    }

    @Test
    public void testEmptyUsernameRegistration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveUser("", "ram123", "ram@gmail.com", "9876543215", "User");
        });
        String expectedMessage = "Username cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected an IllegalArgumentException for an empty username.");
    }

    @Test
    public void testInvalidLogin() {
        boolean result = DatabaseUtils.isValidLogin("ram", "ram");
        assertFalse(result);
    }

    @Test
    public void testEmptyUsername() {
        boolean isLoggedIn = DatabaseUtils.isValidLogin("", "ram@123");
        assertFalse(isLoggedIn, "Login should fail if the username is empty.");
    }

@Test
public void testGetUserRole() {
    DatabaseUtils.saveUser("bhattarai", "bhattarai@123", "bhattarai@gmail.com", "9876543210", "User");
    String role = DatabaseUtils.getUserRole("bhattarai");
    assertEquals("User", role, "The user role should be 'User'.");
    DatabaseUtils.deleteUser("bhattarai");
}

    @Test
    public void testSaveUser() {
        DatabaseUtils.saveUser("pursoth", "pursoth123", "pursoth@gmail.com", "9876543210", "User");
        boolean result = DatabaseUtils.isValidLogin("pursoth", "pursoth123");
        assertTrue(result);
    }

    @Test
    public void testUpdateUserRole() {
        DatabaseUtils.updateUserRole("ram", "Coordinator");
        String role = DatabaseUtils.getUserRole("ram");
        assertEquals("Coordinator", role);
    }

    @Test
    public void testDeleteUser() {
        DatabaseUtils.deleteUser("pursoth");
        boolean result = DatabaseUtils.isValidLogin("pursoth", "pursoth123");
        assertFalse(result);
    }

    @Test
    public void testSaveDisasterReport() throws SQLException {
        DatabaseUtils.saveDisasterReport("ram", "Fire", "granville", 8, "Severe fire");
        List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
        assertFalse(reports.isEmpty());
        assertTrue(reports.stream().anyMatch(r -> r.getUsername().equals("ram") && r.getDisasterType().equals("Fire") && r.getLocation().equals("granville")));
        DatabaseUtils.deleteDisasterReport("ram", "Fire", "granville");
    }
@Test
public void testGetAssignedTasksSortedByPriority() throws SQLException {
    // Step 1: Insert disaster reports without priority
    DatabaseUtils.saveDisasterReport("ram", "Earthquake", "granville", 1, "Severe earthquake");
    int disasterId1 = DatabaseUtils.getDisasterReportId("ram", "Earthquake", "granville");
    DatabaseUtils.saveDisasterReport("ram", "Flood", "auburn", 2, "Severe flood");
    int disasterId2 = DatabaseUtils.getDisasterReportId("ram", "Flood", "auburn");
    DatabaseUtils.saveDisasterReport("ram", "Fire", "central", 3, "Severe fire");
    int disasterId3 = DatabaseUtils.getDisasterReportId("ram", "Fire", "central");
    DatabaseUtils.updateDisasterPriority(disasterId1, "Very High");
    DatabaseUtils.updateDisasterPriority(disasterId2, "High");
    DatabaseUtils.updateDisasterPriority(disasterId3, "Medium");
    DatabaseUtils.saveAssignedTask(disasterId1, "Search and Rescue team", "Handle rescue operations");
    DatabaseUtils.saveAssignedTask(disasterId2, "Medical Team", "Provide medical aid");
    DatabaseUtils.saveAssignedTask(disasterId3, "Fire Department", "Control fire");
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    tasks.forEach(task -> System.out.println("Task: " + task.getTaskDescription() + ", Priority: " + task.getPriority()));
    assertNotNull(tasks, "Assigned tasks list should not be null.");
    assertFalse(tasks.isEmpty(), "Assigned tasks list should not be empty.");
    assertEquals("Very High", tasks.get(0).getPriority(), "The first task should have 'Very High' priority.");
    assertEquals("High", tasks.get(1).getPriority(), "The second task should have 'High' priority.");
    assertEquals("Medium", tasks.get(2).getPriority(), "The third task should have 'Medium' priority.");
    DatabaseUtils.deleteAssignedTask(disasterId1, "Search and Rescue team", "Handle rescue operations");
    DatabaseUtils.deleteAssignedTask(disasterId2, "Medical Team", "Provide medical aid");
    DatabaseUtils.deleteAssignedTask(disasterId3, "Fire Department", "Control fire");
    DatabaseUtils.deleteDisasterReport("ram", "Earthquake", "granville");
    DatabaseUtils.deleteDisasterReport("ram", "Flood", "auburn");
    DatabaseUtils.deleteDisasterReport("ram", "Fire", "central");
}



    @Test
public void testUpdateTaskStatus() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Fire", "central", 7, "Severe fire");
    int disasterId = DatabaseUtils.getDisasterReportId("ram", "Fire", "central");
    DatabaseUtils.saveAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    assertNotNull(tasks, "The task list should not be null.");
    assertFalse(tasks.isEmpty(), "There should be at least one task.");
    AssignedTask task = tasks.stream()
                             .filter(t -> t.getDisasterId() == disasterId)
                             .findFirst()
                             .orElseThrow(() -> new SQLException("Task not found"));
    
    //System.out.println("Initial Task Status: " + task.getStatus());
    DatabaseUtils.updateTaskStatus(disasterId, "Finished");
    List<AssignedTask> updatedTasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    AssignedTask updatedTask = updatedTasks.stream()
                                           .filter(t -> t.getDisasterId() == disasterId)
                                           .findFirst()
                                           .orElseThrow(() -> new SQLException("Updated task not found"));
    //System.out.println("Updated Task Status: " + updatedTask.getStatus());
    assertEquals("Finished", updatedTask.getStatus(), "The task status should be updated to 'Finished'.");
    DatabaseUtils.deleteAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    DatabaseUtils.deleteDisasterReport("ram", "Fire", "central");
}

   @Test
public void testGetAllFinishedTasks() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Flood", "auburn", 5, "Moderate flood");
    int disasterId = DatabaseUtils.getDisasterReportId("ram", "Flood", "auburn");
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
    DatabaseUtils.deleteDisasterReport("ram", "Flood", "auburn");
}

@Test
public void testGetFinishedTasksForUser() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Flood", "auburn", 5, "Minor flooding");
    int disasterId = DatabaseUtils.getDisasterReportId("ram", "Flood", "auburn");
    DatabaseUtils.saveAssignedTask(disasterId, "ram", "Handle flood evacuation");
    DatabaseUtils.updateTaskStatus(disasterId, "Finished");
    List<AssignedTask> tasks = DatabaseUtils.getFinishedTasksForUser("ram");
    assertNotNull(tasks, "The list of finished tasks should not be null.");
    assertFalse(tasks.isEmpty(), "There should be at least one finished task for the user.");
    DatabaseUtils.deleteAssignedTask(disasterId, "ram", "Handle flood evacuation");
    DatabaseUtils.deleteDisasterReport("ram", "Flood", "auburn");
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
        String role = DatabaseUtils.getUserRole("ravi");
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
        DatabaseUtils.saveUser("suraj", "suraj123", "suraj@gmail.com", "9876543212", "User");
        boolean result = DatabaseUtils.isEmailAndPhoneRegistered("suraj@gmail.com", "9876543212");
        assertTrue(result, "The email and phone number should be registered.");
        DatabaseUtils.deleteUser("suraj");
    }

    @Test
    public void testInvalidEmailAndPhoneRegistration() {
        boolean result = DatabaseUtils.isEmailAndPhoneRegistered("ravi@gmail.com", "1234567890");
        assertFalse(result, "The email and phone number should not be registered.");
    }

@Test
public void testUpdateTaskStatusToInProgress() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Earthquake", "granville", 7, "Severe earthquake");
    int disasterId = DatabaseUtils.getDisasterReportId("ram", "Earthquake", "granville");
    DatabaseUtils.saveAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");
    DatabaseUtils.updateTaskStatus(disasterId, "Still in Process");
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    AssignedTask task = tasks.stream()
                             .filter(t -> t.getDisasterId() == disasterId)
                             .findFirst()
                             .orElseThrow(() -> new SQLException("Task not found"));
    assertEquals("Still in Process", task.getStatus(), "The task status should be updated to 'Still in Process'.");
  DatabaseUtils.deleteAssignedTask(disasterId, "Rescue Department", "Handle rescue operations.");
    DatabaseUtils.deleteDisasterReport("ram", "Earthquake", "granville");
}


@Test
public void testGetAllDisasterReports() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Fire", "granville", 8, "Severe fire");
    List<DisasterReport> reports = DatabaseUtils.getAllDisasterReports();
    assertNotNull(reports, "The disaster reports list should not be null.");
    assertFalse(reports.isEmpty(), "There should be at least one disaster report in the database.");
    DatabaseUtils.deleteDisasterReport("ram", "Fire", "granville");
}

@Test
public void testSaveAssignedTask() throws SQLException {
    DatabaseUtils.saveDisasterReport("ram", "Fire", "granville", 8, "Severe fire");
    int disasterId = DatabaseUtils.getDisasterReportId("ram", "Fire", "granville");
    DatabaseUtils.saveAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    List<AssignedTask> tasks = DatabaseUtils.getAssignedTasksSortedByPriority();
    assertTrue(tasks.stream().anyMatch(task -> task.getTaskDescription().equals("Handle evacuation process.")));
    DatabaseUtils.deleteAssignedTask(disasterId, "Evacuation Department", "Handle evacuation process.");
    DatabaseUtils.deleteDisasterReport("ram", "Fire", "granville");
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
            DatabaseUtils.saveDisasterReport("ram", "", "granville", 8, "Severe fire");
        });
        String expectedMessage = "Disaster type cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyLocation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("ram", "Fire", "", 8, "Severe fire");
        });
        String expectedMessage = "Location cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptySeverity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("ram", "Fire", "granville", 0, "Severe fire");
        });
        String expectedMessage = "Severity must be greater than 0";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEmptyDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseUtils.saveDisasterReport("ram", "Fire", "granville", 8, "");
        });
        String expectedMessage = "Description cannot be empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @AfterEach
    void tearDown() {
        //System.out.println("Tearing down after each test.");
        DatabaseUtils.deleteUser("ram");
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        //System.out.println("Tearing down after all tests.");
        DatabaseUtils.deleteUser("newuser2");
        DatabaseUtils.deleteUser("ram");
        if (connection != null && !connection.isClosed()) {
            connection.close();
         //   System.out.println("Database connection closed.");
        }
    }

}
