package com.example.smdproject;

public class Cart {
    private String ItemName;
    private String ItemDescription;
    private int ItemQuantity;
    private float ItemPrice;

    public Cart(String itemName, String itemDescription, int itemQuantity, float itemPrice) {
        ItemName = itemName;
        ItemDescription = itemDescription;
        ItemQuantity = itemQuantity;
        ItemPrice = itemPrice;
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

