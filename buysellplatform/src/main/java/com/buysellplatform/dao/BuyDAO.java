package com.buysellplatform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.buysellplatform.DatabaseUtil;

public class BuyDAO {

    public boolean savePurchaseDetails(int productId, int userId) {
        String query = "INSERT INTO purchases (product_id, user_id) VALUES (?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
