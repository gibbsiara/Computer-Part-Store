package com.example.pcbuilder.dao;

import com.example.pcbuilder.db.DbConnection;
import com.example.pcbuilder.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> filterByPrice(double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE price >= ? AND price <= ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return products;
    }

    public boolean addProduct(String productName, double productPrice, int manufacturerId, int categoryId) {
        String query = "INSERT INTO Products (name, price, manufacturer_id, category_id) VALUES (?, ?, ?, ?)";
        try(Connection conn = DbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, productName);
            stmt.setDouble(2, productPrice);
            stmt.setInt(3, manufacturerId);
            stmt.setInt(4, categoryId);
            stmt.executeUpdate();
            return true;

        }catch(SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM Products WHERE product_id = ?";
        try(Connection conn = DbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, productId);
            stmt.executeUpdate();
            return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean updateProductPrice(int productId, double newPrice) {
        String query = "UPDATE Products SET price = ? WHERE product_id = ?";
        try(Connection conn = DbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            return true;
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public void printManufacturers() {
        String query = "SELECT manufacturer_id, name FROM Manufacturers";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- DOSTĘPNI PRODUCENCI ---");
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println(rs.getInt("manufacturer_id") + ". " + rs.getString("name"));
            }
            if (!hasResults) System.out.println("(Brak producentów w bazie)");
            System.out.println("---------------------------");

        } catch (SQLException e) {
            System.out.println("Błąd pobierania producentów: " + e.getMessage());
        }
    }

    public void printCategories() {
        String query = "SELECT category_id, name FROM Categories";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- DOSTĘPNE KATEGORIE ---");
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println(rs.getInt("category_id") + ". " + rs.getString("name"));
            }
            if (!hasResults) System.out.println("(Brak kategorii w bazie)");
            System.out.println("--------------------------");

        } catch (SQLException e) {
            System.out.println("Błąd pobierania kategorii: " + e.getMessage());
        }
    }
    public boolean addProductSpec(int productId, int parameterId, String value) {
        String query = "INSERT INTO Product_Specs (product_id, parameter_id, value) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, parameterId);
            stmt.setString(3, value);

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Błąd dodawania specyfikacji: " + e.getMessage());
            return false;
        }
    }

    public void printProductSpecs(int productId) {
        String query = "SELECT p.name AS param_name, ps.value " +
                "FROM Product_Specs ps " +
                "JOIN Parameters p ON ps.parameter_id = p.parameter_id " +
                "WHERE ps.product_id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- SPECYFIKACJA PRODUKTU (ID: " + productId + ") ---");
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String paramName = rs.getString("param_name");
                String value = rs.getString("value");
                System.out.println("- " + paramName + ": " + value);
            }
            if (!hasResults) System.out.println("(Brak specyfikacji dla tego produktu)");
            System.out.println("----------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Błąd pobierania specyfikacji: " + e.getMessage());
        }
    }
    public String getParameterValue(int productId, String parameterName) {
        String query = "SELECT ps.value " +
                "FROM Product_Specs ps " +
                "JOIN Parameters p ON ps.parameter_id = p.parameter_id " +
                "WHERE ps.product_id = ? AND p.name = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            stmt.setString(2, parameterName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            System.out.println("Błąd pobierania parametru: " + e.getMessage());
        }
        return null;
    }

    public boolean addManufacturer(String name) {
        String query = "INSERT INTO Manufacturers (name) VALUES (?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Błąd dodawania producenta: " + e.getMessage());
            return false;
        }
    }

    public boolean addCategory(String name) {
        String query = "INSERT INTO Categories (name) VALUES (?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd dodawania kategorii: " + e.getMessage());
            return false;
        }
    }
    public void printSimpleProductList() {
        String query = "SELECT product_id, name FROM Products";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- LISTA PRODUKTÓW (ID) ---");
            while (rs.next()) {
                System.out.println("[" + rs.getInt("product_id") + "] " + rs.getString("name"));
            }
            System.out.println("----------------------------");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}