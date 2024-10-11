package com.mycompany.mavenproject1.model;

/**
 * Model class representing an assigned task related to a disaster. Stores
 * details such as disaster ID, disaster type, location, department, task
 * description, priority, and status.
 *
 * @author Bishwas Bhattarai
 */
public class AssignedTask {

    private int disasterId;          // ID of the disaster associated with the task
    private String disasterType;     // Type of disaster (e.g., Hurricane, Fire)
    private String location;         // Location of the disaster
    private String department;       // Department responsible for handling the task
    private String taskDescription;  // Description of the assigned task
    private String priority;         // Priority of the task (e.g., High, Medium, Low)
    private String status;           // Status of the task (e.g., Still in Process, Finished)

    /**
     * Constructs an AssignedTask object with the given details.
     *
     * @param disasterId the ID of the disaster
     * @param disasterType the type of disaster (e.g., Hurricane, Fire)
     * @param location the location of the disaster
     * @param department the department responsible for the task
     * @param taskDescription a description of the assigned task
     * @param priority the priority level of the task (e.g., High, Medium, Low)
     * @param status the status of the task (e.g., Still in Process, Finished)
     */
    public AssignedTask(int disasterId, String disasterType, String location, String department, String taskDescription, String priority, String status) {
        this.disasterId = disasterId;
        this.disasterType = disasterType;
        this.location = location;
        this.department = department;
        this.taskDescription = taskDescription;
        this.priority = priority;
        this.status = status;  // Initialize the status
    }

    /**
     * Returns the ID of the disaster associated with the task.
     *
     * @return the disaster ID
     */
    public int getDisasterId() {
        return disasterId;
    }

    /**
     * Returns the type of disaster.
     *
     * @return the disaster type
     */
    public String getDisasterType() {
        return disasterType;
    }

    /**
     * Returns the location of the disaster.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the department responsible for handling the task.
     *
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Returns the description of the assigned task.
     *
     * @return the task description
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * Returns the priority level of the task.
     *
     * @return the priority level
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Returns the status of the task (e.g., Still in Process, Finished).
     *
     * @return the task status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status the new status of the task
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
