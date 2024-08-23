package com.buysellplatform.controller;

import com.buysellplatform.model.User;
import com.buysellplatform.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/profile")
public class UserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");

        User user = (User) session.getAttribute("user");

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Fetch the current password from the database
            String query = "SELECT password FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, user.getId());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    // Compare the stored password with the entered current password
                    if (storedPassword.equals(currentPassword)) {
                        // Proceed to update the password
                        String updatePasswordSQL = "UPDATE users SET password = ? WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updatePasswordSQL)) {
                            updateStatement.setString(1, newPassword);
                            updateStatement.setInt(2, user.getId());
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                user.setPassword(newPassword);
                                session.setAttribute("user", user);
                                response.sendRedirect("profile?passwordChanged=true");
                                return; // Ensure to return after redirect
                            } else {
                                request.setAttribute("errorMessage", "Failed to update password. Please try again.");
                            }
                        }
                    } else {
                        request.setAttribute("errorMessage", "Current password is incorrect.");
                    }
                } else {
                    request.setAttribute("errorMessage", "User not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while updating the password.");
        }

        // Forward to profile.jsp if there's an error
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}
