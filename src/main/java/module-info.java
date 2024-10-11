module com.mycompany.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 
    requires javafx.graphics;

    opens com.mycompany.mavenproject1 to javafx.fxml;
    exports com.mycompany.mavenproject1;
    exports com.mycompany.mavenproject1.controller;
    exports com.mycompany.mavenproject1.util;
    exports com.mycompany.mavenproject1.model;
    opens com.mycompany.mavenproject1.controller to javafx.fxml;
    opens com.mycompany.mavenproject1.model to javafx.base;
    
}
