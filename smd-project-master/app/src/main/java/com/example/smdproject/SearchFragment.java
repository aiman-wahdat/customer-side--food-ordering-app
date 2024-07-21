package com.example.smdproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private restaurantAdapter adapter;
    private ArrayList<Restaurant> list;
    private ArrayList<Restaurant> filteredList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchEditText = root.findViewById(R.id.etSearch);
        recyclerView = root.findViewById(R.id.menuRecyclerView);

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
