package com.example.pcbuilder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    public static Connection getConnection() throws SQLException{
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException("Brak zmiennych środowiskowych DB_URL, DB_USER lub DB_PASSWORD!");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
