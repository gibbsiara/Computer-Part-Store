package com.example.pcbuilder.dao;

import com.example.pcbuilder.db.DbConnection;

import java.sql.*;
import java.util.List;

public class BuildDAO {

    public void createBuild(int userId, String buildName, List<Integer> productIds) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            String insertBuild = "INSERT INTO Builds (user_id, build_name) VALUES (?, ?)";
            PreparedStatement stmtBuild = conn.prepareStatement(insertBuild, Statement.RETURN_GENERATED_KEYS);
            stmtBuild.setInt(1, userId);
            stmtBuild.setString(2, buildName);
            stmtBuild.executeUpdate();
            ResultSet rs = stmtBuild.getGeneratedKeys();
            int buildId = 0;
            if (rs.next()) {
                buildId = rs.getInt(1);
            } else {
                throw new SQLException("Nie udało się utworzyć zestawu, brak ID.");
            }

            String insertItem = "INSERT INTO Build_Items (build_id, product_id, quantity) VALUES (?, ?, 1)";
            PreparedStatement stmtItem = conn.prepareStatement(insertItem);

            for (Integer prodId : productIds) {
                stmtItem.setInt(1, buildId);
                stmtItem.setInt(2, prodId);
                stmtItem.executeUpdate();
            }

            conn.commit();
            System.out.println("Zestaw '" + buildName + "' został zapisany pomyślnie!");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Błąd transakcji!");
                    conn.rollback();
                } catch (SQLException ex) { System.out.println(ex.getMessage()); }
            }
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void printAllBuildsWithComponents() {
        String query = "SELECT b.build_id, b.build_name, u.login, p.name AS part_name, p.price " +
                "FROM Builds b " +
                "JOIN Users u ON b.user_id = u.user_id " +
                "JOIN Build_Items bi ON b.build_id = bi.build_id " +
                "JOIN Products p ON bi.product_id = p.product_id " +
                "ORDER BY b.build_id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- LISTA WSZYSTKICH ZESTAWÓW ---");

            int currentBuildId = -1;
            double currentTotal = 0;

            while (rs.next()) {
                int id = rs.getInt("build_id");

                if (id != currentBuildId) {
                    if (currentBuildId != -1) {
                        System.out.println("   SUMA: " + currentTotal + " PLN");
                        System.out.println("-----------------------------------");
                    }

                    String buildName = rs.getString("build_name");
                    String author = rs.getString("login");
                    System.out.println("🖥️ ZESTAW [" + id + "]: " + buildName + " (Autor: " + author + ")");
                    currentBuildId = id;
                    currentTotal = 0;
                }

                String partName = rs.getString("part_name");
                double price = rs.getDouble("price");
                System.out.println("   - " + partName + " (" + price + " PLN)");
                currentTotal += price;
            }

            if (currentBuildId != -1) {
                System.out.println("   SUMA: " + currentTotal + " PLN");
            }
            System.out.println("===================================");

        } catch (SQLException e) {
            System.out.println("Błąd pobierania zestawów: " + e.getMessage());
        }
    }
}
