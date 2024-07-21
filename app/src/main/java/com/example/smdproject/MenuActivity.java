package com.example.smdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private menuAdapter adapter;
    private DatabaseReference databaseReference;
    private ArrayList<MenuItem> menuList;
    ImageView payoutBackButton;
    String Restaurantid="";
    FloatingActionButton fabRating;
    private PopupWindow popupWindow;
    private ImageView llFoodImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        recyclerView=findViewById(R.id.RVMENU);
        payoutBackButton=findViewById(R.id.payoutBackButton);
        fabRating = findViewById(R.id.fabRating);
        llFoodImage = findViewById(R.id.llFoodImage);
        recyclerView.setHasFixedSize(true);
        menuList=new ArrayList<>();
        // Assuming your RecyclerView has id 'recyclerView' in activity_menu.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ResId"))
        {
            Restaurantid = intent.getStringExtra("ResId");
       //     Toast.makeText(this, "MenuActi"+Restaurantid, Toast.LENGTH_SHORT).show();
            fetchMenuFromDatabase(Restaurantid);

        }
        fabRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingPopup(Restaurantid);
            }
        });

        payoutBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    private void showRatingPopup(String resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.dialog_rating_bar);
        Button doneButton = dialogView.findViewById(R.id.dialog_done_button);
        Button cancelButon = dialogView.findViewById(R.id.btnDismiss);
      //  TextView TvRating = dialogView.findViewById(R.id.TvRating);

        AlertDialog dialog = builder.create();
        dialog.show();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference restaurantRef = dbRef.child("Restaurants").child(resId.replace('.', '_'));

                restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("RatingPopup", "onDataChange triggered");
                        if (dataSnapshot.exists()) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            if (restaurant != null) {
                                float existingRating = restaurant.getRating();
                               // Toast.makeText(MenuActivity.this, "Existing Rating: " + existingRating, Toast.LENGTH_SHORT).show();
                               // int numberOfRatings = restaurant.getNumberOfRatings();

                                // Calculate the new rating
                                float newRating = ((existingRating ) + rating) / (2);
                               // int newNumberOfRatings = numberOfRatings + 1;

                                // Update the restaurant's rating in the database
                                restaurantRef.child("rating").setValue(newRating);
                               // restaurantRef.child("numberOfRatings").setValue(newNumberOfRatings);

                                Toast.makeText(MenuActivity.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle potential errors here
                    }
                });
                dialog.dismiss(); // Close the AlertDialog

            }
        });
        cancelButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void fetchMenuFromDatabase(String restaurantId ){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference restaurantRef = databaseReference.child("items").child(Restaurantid.replace('.','_')).child("menuItems");

        restaurantRef.addValueEventListener(new ValueEventListener() {
            private DatabaseError error;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuItem menuItem = snapshot.getValue(MenuItem.class);
                    if (menuItem != null) {
                        menuItem.setRestaurantId(Restaurantid);
                        menuList.add(menuItem);
                    }
                }
                // Set up RecyclerView adapter with the received list of restaurants
                adapter = new menuAdapter(menuList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

}