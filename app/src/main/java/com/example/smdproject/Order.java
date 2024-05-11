package com.example.smdproject;

public class Order {
    private String orderId;
    private String restaurantId;
    private String orderStatus;
    private String orderPrice;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Order(){

    }

    public Order(String restaurantId, String orderStatus, String orderPrice,String orderId) {
        this.restaurantId = restaurantId;
        this.orderStatus = orderStatus;
        this.orderPrice = orderPrice;
        this.orderId=orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }
}
