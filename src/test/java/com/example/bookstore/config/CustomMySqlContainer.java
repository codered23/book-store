package com.example.bookstore.config;
import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
 private static final String DB_IMAGE = "mysql:8.0.33";
 private static CustomMySqlContainer mySqlContainer;

 private CustomMySqlContainer() {
    super(DB_IMAGE);
 }

 public static synchronized CustomMySqlContainer getInstance() {
     if (mySqlContainer == null) {
         mySqlContainer = new CustomMySqlContainer();
     }
     return mySqlContainer;
 }

 @Override
 public void start() {
    super.start();
    System.setProperty("TEST_DB_URL", mySqlContainer.getJdbcUrl());
    System.setProperty("TEST_DB_USERNAME", mySqlContainer.getUsername());
    System.setProperty("TEST_DB_PASSWORD", mySqlContainer.getPassword());


     // Додаємо логування:
     System.out.println("=== Testcontainers MySQL connection info ===");
     System.out.println("JDBC URL:    " + mySqlContainer.getJdbcUrl());
     System.out.println("Username:    " + mySqlContainer.getUsername());
     System.out.println("Password:    " + mySqlContainer.getPassword());
     System.out.println("Mapped Port: " + mySqlContainer.getMappedPort(3306));
     System.out.println("===========================================");
 }

 @Override
 public void stop() {
 }
}