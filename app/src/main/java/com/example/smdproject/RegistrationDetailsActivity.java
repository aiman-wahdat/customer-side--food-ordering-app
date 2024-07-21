package com.example.smdproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

public class RegistrationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        if (savedInstanceState == null) {
            // Default to showing customer registration fragment
            showCustomerRegistrationFragment();
        }
        showCustomerRegistrationFragment();

    }
    private void showCustomerRegistrationFragment() {
        Fragment fragment = new CustomerFrag();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

}