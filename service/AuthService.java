package com.example.pcbuilder.service;

import com.example.pcbuilder.dao.UserDAO;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String hashPassword(String passwdToHash, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltHex.getBytes());
            byte[] bytes = md.digest(passwdToHash.getBytes());
            return bytesToHex(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return bytesToHex(salt);
    }

    public boolean register(String login, String passwordRaw) {
        try {
            String salt = getSalt();
            String hashedPassword = hashPassword(passwordRaw, salt);

            boolean success = userDAO.saveUser(login, hashedPassword, salt, 2);

            if (success) {
                System.out.println("Zarejestrowano pomyślnie!");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int login(String login, String passwordRaw) {
        String[] creds = userDAO.getUserCredentials(login);

        if (creds == null) {
            System.out.println("Użytkownik nie istnieje.");
            return -1;
        }

        String dbHash = creds[0];
        String dbSalt = creds[1];
        int userId = Integer.parseInt(creds[2]);

        String calculatedHash = hashPassword(passwordRaw, dbSalt);

        if (calculatedHash.equals(dbHash)) {
            return userId;
        } else {
            System.out.println("Błędne hasło.");
            return -1;
        }
    }
}