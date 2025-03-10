
package com.example.smdproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView rvRestaurants;
    restaurantAdapter myAdapter;
    private DatabaseReference restaurantsRef;
    // In a fragment or activity where you want to access or update the shared ArrayList
    DataRepository dataRepository = DataRepository.getInstance();
    current_user_singleton currUser = current_user_singleton.getInstance();
    TextView tvWelcomeUser , TvHelloInitials;

    // Get the shared ArrayList
    ArrayList<Restaurant> arrayList = dataRepository.getRestaurantsList();

    private DatabaseReference favoritesRef;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcomeUser = rootView.findViewById(R.id.TVWelcomeHome);
        TvHelloInitials = rootView.findViewById(R.id.TvHelloInitials);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantsRef = database.getReference("Restaurants");

        // Configure FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Restaurant> options =
                new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(restaurantsRef, Restaurant.class)
                        .build();

        fetchDataFromFirebase();
        myAdapter=new restaurantAdapter(arrayList);
        // Initialize RecyclerView and adapter
        rvRestaurants = rootView.findViewById(R.id.rvRestaurants);

        retrieveUserData(currUser.getUserId(),"users");

//        myAdapter = new restaurantAdapter(list);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRestaurants.setAdapter(myAdapter);

        // Find views within the inflated layout
        ImageSlider imageSlider = rootView.findViewById(R.id.image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image5, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        return rootView;
    }
    private void fetchDataFromFirebase() {
        restaurantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        arrayList.add(restaurant);
                    }
                }
                dataRepository.setRestaurantsList(arrayList);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });

    }

    private void checkFavoriteStatus(final Restaurant restaurant) {
        favoritesRef.child(restaurant.getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFavorite = dataSnapshot.exists();
                restaurant.setFavorite(isFavorite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

    private void  retrieveUserData(String email, String userType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(userType);

        // Use user's email as the key in the database (replace '.' with '_' for the key)
        String emailKey = email.replace(".", "_");

        DatabaseReference userNodeRef = userRef.child(emailKey);

        userNodeRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    // User data found, retrieve it
                    User user = dataSnapshot.getValue(User.class);
                    tvWelcomeUser.setText("Hello "+user.getName());
                    String toSet = user.getName().toUpperCase();
                    String a = toSet.charAt(0) + "";
                    char b =  toSet.charAt(1);
                    String set = toSet.charAt(0)+"" + toSet.charAt(1)+"";
                    TvHelloInitials.setText(set);




                } else
                {
                    // User data not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while retrieving data
            }
        });
    }
}