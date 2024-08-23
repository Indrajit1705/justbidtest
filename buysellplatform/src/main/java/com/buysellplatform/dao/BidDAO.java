package com.buysellplatform.dao;

import com.buysellplatform.DatabaseUtil;
import com.buysellplatform.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BidDAO {

    // Method to save a bid and update the current bid price
    public boolean updateBid(int productId, int buyerId, double bidPrice) {
        String INSERT_BID_SQL = "INSERT INTO bids (product_id, buyer_id, bid_price, bid_time) VALUES (?, ?, ?, NOW())";
        String UPDATE_PRODUCT_BID_SQL = "UPDATE products SET current_bid_price = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement insertBidStmt = connection.prepareStatement(INSERT_BID_SQL);
             PreparedStatement updateProductStmt = connection.prepareStatement(UPDATE_PRODUCT_BID_SQL)) {

            // Insert bid into bids table
            insertBidStmt.setInt(1, productId);
            insertBidStmt.setInt(2, buyerId);
            insertBidStmt.setDouble(3, bidPrice);
            int rowsAffected = insertBidStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update the current bid price in the products table
                updateProductStmt.setDouble(1, bidPrice);
                updateProductStmt.setInt(2, productId);
                int updateRowsAffected = updateProductStmt.executeUpdate();
                return updateRowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
