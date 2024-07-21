package com.example.smdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class checkoutActivity extends AppCompatActivity {

    Button placeMyOrderButton;
    ImageView payoutBackButton;
    EditText payoutName, payoutAddress, payoutPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        placeMyOrderButton=findViewById(R.id.placeMyOrderButton);
        payoutName=findViewById(R.id.payoutName);
        payoutAddress=findViewById(R.id.payoutAddress);
        payoutPhoneNumber=findViewById(R.id.payoutPhoneNumber);
        payoutBackButton=findViewById(R.id.payoutBackButton);

        placeMyOrderButton.setOnClickListener(new View.OnClickListener() {
            String name=payoutName.getText().toString().trim();
            String add=payoutAddress.getText().toString().trim();
            String phone=payoutPhoneNumber.getText().toString().trim();

            @Override
            public void onClick(View v) {
                if(name.isEmpty() || add.isEmpty() || phone.isEmpty())
                {
                    Toast.makeText(checkoutActivity.this, "PLEASE ENTER DETAILS", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(checkoutActivity.this, "ORDER PLACED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                }
            }
        });

        payoutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}