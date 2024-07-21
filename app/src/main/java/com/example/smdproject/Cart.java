package com.example.smdproject;

import java.io.Serializable;

public class Cart implements Serializable {
    private String ItemName;
    private String ItemDescription;
    private int ItemQuantity;
    private float ItemPrice;
    private String key;
    private String restaurantId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Cart(){

    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Cart(String itemName, String itemDescription, int itemQuantity, float itemPrice) {
        ItemName = itemName;
        ItemDescription = itemDescription;
        ItemQuantity = itemQuantity;
        ItemPrice = itemPrice;
        this.restaurantId=restaurantId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public int getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        ItemQuantity = itemQuantity;
    }

    public float getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(float itemPrice) {
        ItemPrice = itemPrice;
    }
}

