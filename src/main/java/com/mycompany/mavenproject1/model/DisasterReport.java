package com.mycompany.mavenproject1.model;

import javafx.scene.control.ComboBox;
import java.time.LocalDateTime;

/**
 * Model class representing a disaster report. Stores details such as report ID,
 * username of the reporter, disaster type, location, severity, description,
 * priority, and the time the report was created.
 *
 * @author Bishwas Bhattarai
 */
public class DisasterReport {

    private int id;                     // Unique ID of the disaster report
    private String username;             // Username of the person who reported the disaster
    private String disasterType;         // Type of disaster (e.g., Hurricane, Fire)
    private String location;             // Location of the disaster
    private String severity;             // Severity level of the disaster
    private String description;          // Description of the disaster
    private ComboBox<String> priorityDropdown; // ComboBox to select the priority of the disaster
    private LocalDateTime reportTime;    // Time when the report was created

    /**
     * Constructs a DisasterReport object with the given details.
     *
     * @param id the unique ID of the disaster report
     * @param username the username of the person who reported the disaster
     * @param disasterType the type of disaster
     * @param location the location of the disaster
     * @param severity the severity level of the disaster
     * @param description a description of the disaster
     * @param priority the priority level of the disaster (e.g., Very High,
     * High, Medium, Low, Very Low)
     * @param reportTime the time the report was created
     */
    public DisasterReport(int id, String username, String disasterType, String location, String severity, String description, String priority, LocalDateTime reportTime) {
        this.id = id;
        this.username = username;
        this.disasterType = disasterType;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.priorityDropdown = new ComboBox<>();
        this.priorityDropdown.getItems().addAll("Very High", "High", "Medium", "Low", "Very Low");
        this.priorityDropdown.setValue(priority);
        this.reportTime = reportTime;
    }

    /**
     * Returns the unique ID of the disaster report.
     *
     * @return the report ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the username of the person who reported the disaster.
     *
     * @return the username of the reporter
     */
    public String getUsername() {
        return username;
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
     * @return the location of the disaster
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the severity level of the disaster.
     *
     * @return the severity level
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Returns the description of the disaster.
     *
     * @return the description of the disaster
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the ComboBox used to select the priority level of the disaster.
     *
     * @return the priority ComboBox
     */
    public ComboBox<String> getPriorityDropdown() {
        return priorityDropdown;
    }

    /**
     * Returns the time when the disaster report was created.
     *
     * @return the report creation time
     */
    public LocalDateTime getReportTime() {
        return reportTime;
    }
}
