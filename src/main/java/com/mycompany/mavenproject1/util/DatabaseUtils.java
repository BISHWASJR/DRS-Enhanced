package com.mycompany.mavenproject1.util;

import com.mycompany.mavenproject1.model.AssignedTask;
import com.mycompany.mavenproject1.model.DisasterReport;
import com.mycompany.mavenproject1.model.UserModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling database operations such as user authentication,
 * disaster report management, and task assignments in the system.
 *
 * This class includes methods for connecting to the database, performing CRUD
 * operations on users, disaster reports, and assigned tasks. It also provides
 * methods for hashing passwords and checking user credentials.
 *
 * The class handles all database transactions such as saving, updating,
 * deleting, and fetching user and task-related data.
 *
 * @author Bishwas Bhattarai
 */
public class DatabaseUtils {

    private static final String URL = "jdbc:mysql://localhost:3306/userdb"; // Database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "pass"; // MySQL password

    /**
     * Establishes a connection to the database.
     *
     * @return a Connection object to interact with the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Hashes a password using the SHA-256 algorithm.
     *
     * @param password the password to hash.
     * @return the hashed password as a hexadecimal string.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = messageDigest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Saves a registered user into the database.
     *
     * @param username the username of the user.
     * @param password the user's password.
     * @param email the user's email address.
     * @param phoneNumber the user's phone number.
     * @param role the user's role (e.g., Coordinator, Department, etc.).
     */
    public static void saveUser(String username, String password, String email, String phoneNumber, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password, email, phone_number, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, role);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the role of a user based on their username.
     *
     * @param username the username of the user.
     * @return the role of the user, or null if not found.
     */
    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if the provided login credentials are valid.
     *
     * @param username the username entered by the user.
     * @param password the password entered by the user.
     * @return true if the login is valid, false otherwise.
     */
    public static boolean isValidLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a user exists in the database by checking their username,
     * email, or phone number.
     *
     * @param username the username of the user.
     * @param email the email address of the user.
     * @param phoneNumber the phone number of the user.
     * @return true if the user exists, false otherwise.
     */
    public static boolean userExists(String username, String email, String phoneNumber) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ? OR phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if an email and phone number combination is registered in the
     * database.
     *
     * @param email the email address of the user.
     * @param phoneNumber the phone number of the user.
     * @return true if the email and phone number are registered, false
     * otherwise.
     */
    public static boolean isEmailAndPhoneRegistered(String email, String phoneNumber) {
        String sql = "SELECT * FROM users WHERE email = ? AND phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the user's password in the database.
     *
     * @param email the email address of the user.
     * @param newPassword the new password to update.
     */
    public static void updateUserPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashPassword(newPassword));
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a disaster report to the database.
     *
     * @param username the username of the user who reported the disaster.
     * @param disasterType the type of the disaster.
     * @param location the location of the disaster.
     * @param severity the severity of the disaster.
     * @param description the description of the disaster.
     */
    public static void saveDisasterReport(String username, String disasterType, String location, int severity, String description) {
        if (disasterType == null || disasterType.trim().isEmpty()) {
            throw new IllegalArgumentException("Disaster type cannot be empty");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (severity <= 0) {
            throw new IllegalArgumentException("Severity must be greater than 0");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        String sql = "INSERT INTO disaster_reports (username, disaster_type, location, severity, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, disasterType);
            stmt.setString(3, location);
            stmt.setInt(4, severity);
            stmt.setString(5, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all disaster reports from the database.
     *
     * @return a list of DisasterReport objects.
     * @throws SQLException if a database access error occurs.
     */
    public static List<DisasterReport> getAllDisasterReports() throws SQLException {
        String query = "SELECT * FROM disaster_reports";
        List<DisasterReport> reports = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DisasterReport report = new DisasterReport(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("disaster_type"),
                        rs.getString("location"),
                        rs.getString("severity"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getTimestamp("report_time").toLocalDateTime()
                );
                reports.add(report);
            }
        }
        return reports;
    }

    /**
     * Updates the priority of a disaster report in the database.
     *
     * @param id the ID of the disaster report.
     * @param priority the new priority level to be updated.
     * @throws SQLException if a database access error occurs.
     */
    public static void updateDisasterPriority(int id, String priority) throws SQLException {
        String query = "UPDATE disaster_reports SET priority = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, priority);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Assigns a task to a department based on a disaster report.
     *
     * @param disasterId the ID of the disaster.
     * @param department the department to assign the task.
     * @param taskDescription the description of the task.
     * @throws SQLException if a database access error occurs.
     */
    public static void assignTaskToDepartment(int disasterId, String department, String taskDescription) throws SQLException {
        String sql = "INSERT INTO assigned_tasks (disaster_id, department, task_description) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, disasterId);
            stmt.setString(2, department);
            stmt.setString(3, taskDescription);
            stmt.executeUpdate();
        }
    }

    /**
     * Saves an assigned task to the database.
     *
     * @param disasterId the ID of the disaster.
     * @param department the department assigned to the task.
     * @param taskDescription the description of the task.
     * @throws SQLException if a database access error occurs.
     */
    public static void saveAssignedTask(int disasterId, String department, String taskDescription) throws SQLException {
        String sql = "INSERT INTO assigned_tasks (disaster_id, department, task_description) VALUES (?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, disasterId);
            preparedStatement.setString(2, department);
            preparedStatement.setString(3, taskDescription);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Retrieves all assigned tasks from the database, sorted by priority.
     *
     * @return a list of AssignedTask objects.
     * @throws SQLException if a database access error occurs.
     */
    public static List<AssignedTask> getAssignedTasksSortedByPriority() throws SQLException {
        List<AssignedTask> assignedTasks = new ArrayList<>();

        String query = "SELECT d.id, d.disaster_id, d.department, d.task_description, d.created_at, d.status, "
                + "a.disaster_type, a.location, a.priority "
                + "FROM assigned_tasks d "
                + "JOIN disaster_reports a ON d.disaster_id = a.id "
                + "ORDER BY CASE "
                + "   WHEN a.priority = 'Very High' THEN 1 "
                + "   WHEN a.priority = 'High' THEN 2 "
                + "   WHEN a.priority = 'Medium' THEN 3 "
                + "   WHEN a.priority = 'Low' THEN 4 "
                + "   WHEN a.priority = 'Very Low' THEN 5 "
                + "   ELSE 6 "
                + "END ASC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AssignedTask task = new AssignedTask(
                        rs.getInt("disaster_id"),
                        rs.getString("disaster_type"),
                        rs.getString("location"),
                        rs.getString("department"),
                        rs.getString("task_description"),
                        rs.getString("priority"),
                        rs.getString("status")
                );
                assignedTasks.add(task);
            }
        }

        return assignedTasks;
    }

    /**
     * Updates the status of an assigned task.
     *
     * @param disasterId the ID of the disaster.
     * @param status the new status of the task.
     * @throws SQLException if a database access error occurs.
     */
    public static void updateTaskStatus(int disasterId, String status) throws SQLException {
        String query = "UPDATE assigned_tasks SET status = ? WHERE disaster_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, disasterId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all finished tasks for a specific user based on their disaster
     * reports.
     *
     * @param username the username of the user.
     * @return a list of finished AssignedTask objects.
     * @throws SQLException if a database access error occurs.
     */
    public static List<AssignedTask> getFinishedTasksForUser(String username) throws SQLException {
        List<AssignedTask> finishedTasks = new ArrayList<>();

        String query = "SELECT a.disaster_id, a.department, a.task_description, a.status, d.disaster_type, d.location, d.priority "
                + "FROM assigned_tasks a "
                + "JOIN disaster_reports d ON a.disaster_id = d.id "
                + "WHERE d.username = ? AND a.status = 'Finished'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username); // This line is supposed to set the username in the query.
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AssignedTask task = new AssignedTask(
                        rs.getInt("disaster_id"),
                        rs.getString("disaster_type"),
                        rs.getString("location"),
                        rs.getString("department"),
                        rs.getString("task_description"),
                        rs.getString("priority"),
                        rs.getString("status")
                );
                finishedTasks.add(task);
            }
        }
        return finishedTasks;
    }

    /**
     * Retrieves all finished tasks from the database.
     *
     * @return a list of finished AssignedTask objects.
     * @throws SQLException if a database access error occurs.
     */
    public static List<AssignedTask> getAllFinishedTasks() throws SQLException {
        List<AssignedTask> finishedTasks = new ArrayList<>();

        String query = "SELECT a.disaster_id, a.department, a.task_description, a.status, d.disaster_type, d.location, d.priority "
                + "FROM assigned_tasks a "
                + "JOIN disaster_reports d ON a.disaster_id = d.id "
                + "WHERE a.status = 'Finished'";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AssignedTask task = new AssignedTask(
                        rs.getInt("disaster_id"),
                        rs.getString("disaster_type"),
                        rs.getString("location"),
                        rs.getString("department"),
                        rs.getString("task_description"),
                        rs.getString("priority"),
                        rs.getString("status")
                );
                finishedTasks.add(task);
            }
        }
        return finishedTasks;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of UserModel objects, each containing a username and role.
     * @throws SQLException if a database access error occurs.
     */
    public static List<UserModel> fetchAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String sql = "SELECT username, role FROM users";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                users.add(new UserModel(username, null, role, null, null));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Updates the role of a user in the database.
     *
     * @param username the username of the user whose role is being updated.
     * @param newRole the new role to assign to the user.
     * @throws SQLException if a database access error occurs.
     */
    public static void updateUserRole(String username, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user role: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param username the username of the user to delete.
     * @throws SQLException if a database access error occurs.
     */
    public static void deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param username the username of the user to delete.
     * @throws SQLException if a database access error occurs.
     */
    public static void deleteDisasterReport(String username, String disasterType, String location) throws SQLException {
        String sql = "DELETE FROM disaster_reports WHERE username = ? AND disaster_type = ? AND location = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, disasterType);
            stmt.setString(3, location);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an assigned task from the database based on the provided disaster
     * ID, department, and task description.
     *
     * @param disasterId the ID of the disaster associated with the task.
     * @param department the department assigned to the task.
     * @param taskDescription the description of the task.
     * @throws SQLException if a database access error occurs.
     */
    public static void deleteAssignedTask(int disasterId, String department, String taskDescription) throws SQLException {
        String sql = "DELETE FROM assigned_tasks WHERE disaster_id = ? AND department = ? AND task_description = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, disasterId);
            stmt.setString(2, department);
            stmt.setString(3, taskDescription);
            stmt.executeUpdate();
        }
    }
}
