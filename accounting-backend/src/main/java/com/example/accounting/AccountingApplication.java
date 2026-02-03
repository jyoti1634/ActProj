// This defines the package (folder) this class belongs to.
package com.example.accounting;

// imports Spring Boot application annotations and classes
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main class to bootstrap the Accounting application, means to "start here" 
@SpringBootApplication
public class AccountingApplication {
    // Main method to launch the application
    public static void main(String[] args) {
        // Run the Spring Boot application
        SpringApplication.run(AccountingApplication.class, args);
    }
}