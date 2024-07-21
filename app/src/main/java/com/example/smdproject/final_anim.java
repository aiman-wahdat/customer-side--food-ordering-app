package com.example.smdproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class final_anim extends AppCompatActivity {
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_final_anim);
        // Adding the gif here using glide library
        logo = findViewById(R.id.ivTick);
        Glide.with(this).load(R.drawable.gif_anim_done).into(logo);

        // Set a click listener on the root view
        findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the main activity when clicked
                startActivity(new Intent(final_anim.this, MainActivity.class));
                // Finish this activity
                finish();
            }
        });


    }
}