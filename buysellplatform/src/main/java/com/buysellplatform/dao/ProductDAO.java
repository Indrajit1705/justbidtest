package com.buysellplatform.dao;

import com.buysellplatform.model.Product;
import com.buysellplatform.DatabaseUtil;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final Logger logger = Logger.getLogger(ProductDAO.class.getName());

    // Insert a product into the database
    public boolean listProduct(Product product) {
        String INSERT_PRODUCT_SQL = "INSERT INTO products (title, image_url, description, min_bid_price, current_bid_price, auction_end_date, seller_id, product_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("Attempting to insert product into the database...");
        System.out.println("Product Details:");
        System.out.println("Title: " + product.getTitle());
        System.out.println("Image URL: " + product.getImage());  // Ensure this is the S3 URL
        System.out.println("Description: " + product.getDescription());
        System.out.println("Min Bid Price: " + product.getMinBidPrice());
        System.out.println("Current Bid Price: " + product.getCurrentBidPrice());
        System.out.println("Auction End Date: " + product.getAuctionEndDate());
        System.out.println("Seller ID: " + product.getSellerId());
        System.out.println("Product Type: " + product.getProductType()); // Add this line

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_SQL)) {

            preparedStatement.setString(1, product.getTitle());
            preparedStatement.setString(2, product.getImage()); // S3 URL
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setDouble(4, product.getMinBidPrice());
            preparedStatement.setDouble(5, product.getCurrentBidPrice());
            preparedStatement.setTimestamp(6, product.getAuctionEndDate());
            preparedStatement.setInt(7, product.getSellerId());
            preparedStatement.setString(8, product.getProductType()); // Set the product type

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("ProductDAO: Rows affected - " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("ProductDAO: SQL exception - " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    
 // Method to update the current bid price
    public boolean updateBid(int productId, int userId, double bidAmount) {
        boolean isUpdated = false;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String updateBidQuery = "UPDATE products SET current_bid_price = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateBidQuery);
            preparedStatement.setDouble(1, bidAmount);
            preparedStatement.setInt(2, productId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Insert bid into bids table
                String insertBidQuery = "INSERT INTO bids (product_id, user_id, bid_amount, created_at) VALUES (?, ?, ?, NOW())";
                PreparedStatement insertBidStatement = connection.prepareStatement(insertBidQuery);
                insertBidStatement.setInt(1, productId);
                insertBidStatement.setInt(2, userId);
                insertBidStatement.setDouble(3, bidAmount);
                insertBidStatement.executeUpdate();

                isUpdated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Test connection
            if (connection != null) {
                logger.info("Database connection successful.");
            } else {
                logger.severe("Database connection is null.");
            }

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                logger.info("Executing query: " + query);

                while (resultSet.next()) {
                    Product product = new Product();
                    product.setId(resultSet.getInt("id"));
                    product.setTitle(resultSet.getString("title"));
                    product.setImage(resultSet.getString("image_url"));
                    product.setDescription(resultSet.getString("description"));
                    product.setMinBidPrice(resultSet.getDouble("min_bid_price"));
                    product.setCurrentBidPrice(resultSet.getDouble("current_bid_price"));
                    product.setAuctionEndDate(resultSet.getTimestamp("auction_end_date"));
                    product.setSellerId(resultSet.getInt("seller_id"));
                    product.setProductType(resultSet.getString("product_type")); // Get the product type

                    logger.info("Product retrieved: " + product);
                    products.add(product);
                }

                logger.info("Total number of products retrieved: " + products.size());

            } catch (SQLException e) {
                logger.severe("SQL exception while executing query: " + e.getMessage());
            }

        } catch (SQLException e) {
            logger.severe("SQL exception while getting connection: " + e.getMessage());
        }

        return products;
    }
    //retrive all products from seller listed
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE seller_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, sellerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setTitle(resultSet.getString("title"));
                product.setImage(resultSet.getString("image_url"));
                product.setDescription(resultSet.getString("description"));
                product.setMinBidPrice(resultSet.getDouble("min_bid_price"));
                product.setCurrentBidPrice(resultSet.getDouble("current_bid_price"));
                product.setAuctionEndDate(resultSet.getTimestamp("auction_end_date"));
                product.setSellerId(resultSet.getInt("seller_id"));
                products.add(product);
            }

            // Log the number of products retrieved
            System.out.println("Products retrieved from seller: " + products.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
    
    //retrive product details from database
    public Product getProductDetails(int productId) {
        Product product = null;
        String query = "SELECT * FROM products WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, productId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setTitle(rs.getString("title"));
                product.setImage(rs.getString("image"));
                product.setDescription(rs.getString("description"));
                product.setMinBidPrice(rs.getDouble("min_bid_price"));
                product.setCurrentBidPrice(rs.getDouble("current_bid_price"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    public Product getProductById(int productId) {
        Product product = null;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM products WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, productId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        product = new Product();
                        product.setId(resultSet.getInt("id"));
                        product.setTitle(resultSet.getString("title"));
                        product.setDescription(resultSet.getString("description"));
                        product.setCurrentBidPrice(resultSet.getDouble("current_bid_price"));
                        product.setMinBidPrice(resultSet.getDouble("min_bid_price"));
                        product.setImage(resultSet.getString("image_url"));
                        product.setProductType(resultSet.getString("product_type")); // Add this line to handle product_type
                        // Set other fields as necessary
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }


}
