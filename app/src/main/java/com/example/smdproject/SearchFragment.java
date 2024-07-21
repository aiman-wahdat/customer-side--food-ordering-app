package com.example.smdproject;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private restaurantAdapter adapter;
    private ArrayList<Restaurant> list;
    private ArrayList<Restaurant> filteredList;
    AdView adView;
    InterstitialAd MyinterstitialAd;
    current_user_singleton currUser = current_user_singleton.getInstance();
    TextView TvcustName;
    FloatingActionButton loadAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchEditText = root.findViewById(R.id.etSearch);
        recyclerView = root.findViewById(R.id.menuRecyclerView);
        TvcustName=root.findViewById(R.id.TvcustName);
        loadAdd=root.findViewById(R.id.loadAdd);
        adView=root.findViewById(R.id.adView);


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        InterstitialAd.load(getContext(),"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MyinterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        MyinterstitialAd = null;
                    }
                });


        loadAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyinterstitialAd != null) {
                    MyinterstitialAd.show(requireActivity());
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });




        // Initialize restaurant list and filtered list
        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize adapter
        adapter = new restaurantAdapter(filteredList);

        // Set RecyclerView layout manager and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        // In a fragment or activity where you want to access or update the shared ArrayList
        DataRepository dataRepository = DataRepository.getInstance();

        list=new ArrayList<>();
// Get the shared ArrayList
        list = dataRepository.getRestaurantsList();
        retrieveUserData(currUser.getUserId(),"users");

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list based on user input
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return root;
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
                    TvcustName.setText(user.getName());
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
    private void filter(String searchText) {
        filteredList.clear();
        for (Restaurant restaurant : list) {
            if (restaurant.getRestaurantName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
