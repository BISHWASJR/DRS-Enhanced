package com.mycompany.mavenproject1.model;

/**
 * Model class representing a user in the system. Stores user details such as
 * username, password, role, email, and phone number. Provides methods for
 * validating the user's credentials.
 *
 * @author Bishwas Bhattarai
 */
public class UserModel {

    private String username;    // Username of the user
    private String password;    // Password of the user
    private String role;        // Role of the user (e.g., User, Coordinator, Department)
    private String email;       // Email address of the user
    private String phoneNumber; // Phone number of the user

    /**
     * Constructs a UserModel object with the given user details.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param role the role of the user (e.g., User, Coordinator, Department)
     * @param email the email address of the user
     * @param phoneNumber the phone number of the user
     */
    public UserModel(String username, String password, String role, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the role of the user.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the new role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number of the user.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Validates the entered username and password against the stored user
     * credentials.
     *
     * @param enteredUsername the username entered by the user
     * @param enteredPassword the password entered by the user
     * @return true if the entered username and password match the stored
     * credentials, false otherwise
     */
    public boolean validate(String enteredUsername, String enteredPassword) {
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }
}
