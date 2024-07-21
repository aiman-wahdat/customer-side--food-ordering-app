package com.example.smdproject;

import java.io.StringReader;
import java.util.ArrayList;

public class DataRepository {
    private static DataRepository instance;
    private ArrayList<Restaurant> RestaurantsList;

    private DataRepository() {
        // Initialize the sharedArrayList
        RestaurantsList = new ArrayList<>();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public ArrayList<Restaurant> getRestaurantsList() {
        return RestaurantsList;
    }

    public void setRestaurantsList(ArrayList<Restaurant> sharedArrayList) {
        RestaurantsList = sharedArrayList;
    }

    public void addItemToSharedArrayList(Restaurant item) {
        RestaurantsList.add(item);
    }

    public void removeItemFromSharedArrayList(Restaurant item) {
        RestaurantsList.remove(item);
    }
}
