package com.example.smdproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CartFragment extends Fragment {

    RecyclerView rvCart;
    cartAdapter myAdapter;
    ArrayList<Cart> list;

    Button btnCheckout;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void init() {
        list = new ArrayList<>();
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
        list.add(new Cart("Biryani", "desi", 2, 2.4F));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        // Initialize RecyclerView and adapter
        btnCheckout=rootView.findViewById(R.id.btnCheckout);
        rvCart=rootView.findViewById(R.id.rvCartItem);
        rvCart.setHasFixedSize(true);
        init();
        myAdapter = new cartAdapter(list);
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(myAdapter);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(requireActivity() ,checkoutActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "checkout Button clicked", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }

}