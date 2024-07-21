package com.example.smdproject;

import android.widget.ImageView;
public class Restaurant {

    private String RestaurantName;
    private float DeliveryFee;
    private int DeliveryTime;
    private float Rating;
    private String password;
    private String email;
    private String address;

    public Restaurant() {
    }


    public Restaurant(String restaurantName, float deliveryFee, int deliveryTime, float rating) {
        RestaurantName = restaurantName;
        DeliveryFee = deliveryFee;
        DeliveryTime = deliveryTime;
        Rating = rating;
    }

    public Restaurant(String restaurantName, float deliveryFee, int deliveryTime, float rating, String password, String email, String address) {
        RestaurantName = restaurantName;
        DeliveryFee = deliveryFee;
        DeliveryTime = deliveryTime;
        Rating = rating;
        this.password = password;
        this.email = email;
        this.address = address;
    }

    public String getRestaurantName() {
        return RestaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        RestaurantName = restaurantName;
    }

    public float getDeliveryFee() {
        return DeliveryFee;
    }

    public void setDeliveryFee(float deliveryFee) {
        DeliveryFee = deliveryFee;
    }

    public int getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        DeliveryTime = deliveryTime;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(float rating) {
        Rating = rating;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
