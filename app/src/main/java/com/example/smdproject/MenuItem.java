package com.example.smdproject;


import java.io.Serializable;

public class MenuItem  implements Serializable {
    private String name;
    private double price;
    private String description;
    private String restaurantId;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MenuItem() {
        // Default constructor required for Firebase serialization
    }

    public MenuItem(String name, double price, String description, String restaurantId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.restaurantId = restaurantId;
    }

    // Getters and Setters for Menu properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
