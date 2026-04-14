package com.example.pcbuilder.dao;

import com.example.pcbuilder.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean saveUser(String login, String passwordHash, String salt, int roleId) {
        String query = "INSERT INTO Users (login, password_hash, salt, role_id) VALUES(?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            stmt.setString(2, passwordHash);
            stmt.setString(3, salt);
            stmt.setInt(4, roleId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Błąd rejestracji: " + e.getMessage());
            return false;
        }
    }

    public boolean promoteToAdmin(String login) {
        String query = "UPDATE Users SET role_id = 1 WHERE login = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Błąd awansu: " + e.getMessage());
            return false;
        }
    }

    public String[] getUserCredentials(String login) {
        String query = "SELECT password_hash, salt, user_id FROM Users WHERE login = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        String.valueOf(rs.getInt("user_id"))
                };
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int getUserRole(int userId) {
        String query = "SELECT role_id FROM Users WHERE user_id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("role_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 2;
    }
}