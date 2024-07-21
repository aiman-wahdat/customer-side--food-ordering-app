package com.example.smdproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Set the initial fragment when the activity starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,
                    new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId;
                    itemId  =  item.getItemId();
                    if(itemId==R.id.cartFragment){
                        selectedFragment = new CartFragment();
                    }
                    else if (itemId==R.id.chatFragment) {
                        selectedFragment = new ChatFragment();
                    }
                    else if (itemId==R.id.homeFragment){
                        selectedFragment = new HomeFragment();
                    }
                    else if (itemId==R.id.profileFragment){
                        selectedFragment = new ProfileFragment();
                    }
                    else if (itemId==R.id.searchFragment){
                        selectedFragment = new SearchFragment();
                    }

                    if (selectedFragment != null) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, selectedFragment);
                        transaction.addToBackStack(null); // Optional, adds the transaction to the back stack
                        transaction.commit();
                        return true;
                    }

                    return false;
                }
            };
}
