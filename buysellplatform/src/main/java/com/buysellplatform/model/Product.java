package com.buysellplatform.model;

import java.sql.Timestamp;

public class Product {
    private int id;
    private String title;
    private String image;
    private String description;
    private double minBidPrice;
    private double currentBidPrice;
    private Timestamp auctionEndDate; // Changed to Timestamp
    private int sellerId;
    private String productType; // Added field for product type
    
    private static final String S3_BASE_URL = "https://justbid.s3.amazonaws.com/";


    // Getters and Setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMinBidPrice() {
        return minBidPrice;
    }

    public void setMinBidPrice(double minBidPrice) {
        this.minBidPrice = minBidPrice;
    }

    public double getCurrentBidPrice() {
        return currentBidPrice;
    }

    public void setCurrentBidPrice(double currentBidPrice) {
        this.currentBidPrice = currentBidPrice;
    }

    public Timestamp getAuctionEndDate() {
        return auctionEndDate;
    }

    public void setAuctionEndDate(Timestamp auctionEndDate) {
        this.auctionEndDate = auctionEndDate;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    // Method to get the full URL for the image
    public String getImageUrl() {
        return S3_BASE_URL + this.image; // Constructs the full URL
    }
    
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}