package com.example.pcbuilder.dao;

import com.example.pcbuilder.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParametersDAO {
    public boolean addParameterDefinitions(String parameterDescription) {
        String query = "INSERT INTO Parameters (name) VALUES (?)";
        try(Connection conn = DbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, parameterDescription);
            stmt.executeUpdate();
            return true;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public void printAllParameters() {
        String query = "SELECT parameter_id, name FROM Parameters ORDER BY parameter_id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- LISTA PARAMETRÓW ---");
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println(rs.getInt("parameter_id") + ". " + rs.getString("name"));
            }

            if (!hasResults) {
                System.out.println("(Brak zdefiniowanych parametrów)");
            }
            System.out.println("------------------------");

        } catch (SQLException e) {
            System.out.println("Błąd pobierania parametrów: " + e.getMessage());
        }
    }
    public boolean deleteParameterDefinitions(int parameterId) {
        String query = "DELETE FROM Parameters WHERE parameter_id = ?";
        try(Connection conn = DbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, parameterId);
            stmt.executeUpdate();
            return true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
