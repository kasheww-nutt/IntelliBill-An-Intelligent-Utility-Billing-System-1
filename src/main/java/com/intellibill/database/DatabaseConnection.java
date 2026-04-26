package com.intellibill.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("MySQL JDBC Driver not found in classpath.", ex);
        }
        this.url = System.getenv().getOrDefault("INTELLIBILL_DB_URL",
                "jdbc:mysql://localhost:3306/intellibill_db?useSSL=false&serverTimezone=UTC");
        this.user = System.getenv().getOrDefault("INTELLIBILL_DB_USER", "root");
        this.password = System.getenv().getOrDefault("INTELLIBILL_DB_PASSWORD", "");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
