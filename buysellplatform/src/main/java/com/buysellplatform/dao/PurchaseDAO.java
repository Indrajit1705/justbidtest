package com.buysellplatform.dao;

import com.buysellplatform.DatabaseUtil;
import com.buysellplatform.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    public List<Product> getPurchasedProducts(int userId) {
        List<Product> purchasedProducts = new ArrayList<>();
        String query = "SELECT p.id, p.title, p.description, p.image_url, pur.price " +
                       "FROM purchases pur " +
                       "JOIN products p ON pur.product_id = p.id " +
                       "WHERE pur.user_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setTitle(rs.getString("title"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image_url"));
                product.setMinBidPrice(rs.getDouble("price"));  // Assuming this field represents the purchase price

                purchasedProducts.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchasedProducts;
    }
}
