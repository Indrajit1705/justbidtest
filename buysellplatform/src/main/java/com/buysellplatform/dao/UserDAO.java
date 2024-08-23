package com.buysellplatform.dao;

import com.buysellplatform.DatabaseUtil;
import com.buysellplatform.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static final String INSERT_USER_SQL = "INSERT INTO users (name, email, college, whatsapp_number, password) VALUES (?, ?, ?, ?, ?)";
    
    private static final String LOGIN_SQL = "SELECT * FROM users WHERE email = ?";

    public boolean registerUser(User user) {
        boolean isRegistered = false;

        // Define the SQL statements as constants
        final String CHECK_EMAIL_SQL = "SELECT COUNT(*) FROM users WHERE email = ?";
        final String INSERT_USER_SQL = "INSERT INTO users (name, email, college, whatsapp_number, password) VALUES (?, ?, ?, ?, ?)";
        final String VERIFY_INSERTION_SQL = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Check if email already exists
            try (PreparedStatement checkStmt = connection.prepareStatement(CHECK_EMAIL_SQL)) {
                checkStmt.setString(1, user.getEmail());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Email already exists.");
                        return false; // Email already exists
                    }
                }
            }

            // Proceed with registration if email does not exist
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getCollege());
                preparedStatement.setString(4, user.getWhatsappNumber());
                preparedStatement.setString(5, user.getPassword()); // Store plaintext password

                int rowsAffected = preparedStatement.executeUpdate();
                isRegistered = (rowsAffected > 0);
            }

            // Verify insertion
            if (isRegistered) {
                try (PreparedStatement verifyStmt = connection.prepareStatement(VERIFY_INSERTION_SQL)) {
                    verifyStmt.setString(1, user.getEmail());
                    try (ResultSet resultSet = verifyStmt.executeQuery()) {
                        if (resultSet.next()) {
                            System.out.println("User inserted successfully:");
                            System.out.println("ID: " + resultSet.getInt("id"));
                            System.out.println("Name: " + resultSet.getString("name"));
                            System.out.println("Email: " + resultSet.getString("email"));
                            System.out.println("College: " + resultSet.getString("college"));
                            System.out.println("WhatsApp Number: " + resultSet.getString("whatsapp_number"));
                        } else {
                            System.out.println("User insertion verification failed.");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }

        return isRegistered;
    }

    public User loginUser(String email, String password) {
        User user = null;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_SQL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    // Check if the provided password matches the stored password
                    if (password.equals(storedPassword)) {
                        user = new User();
                        user.setId(resultSet.getInt("id"));
                        user.setName(resultSet.getString("name"));
                        user.setEmail(resultSet.getString("email"));
                        user.setCollege(resultSet.getString("college"));
                        user.setWhatsappNumber(resultSet.getString("whatsapp_number"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error message to a file or logging system
        }

        return user;
    }
}
