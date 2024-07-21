package com.example.smdproject;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CartFragment extends Fragment {

    RecyclerView rvCart;
    cartAdapter myAdapter;
    ArrayList<Cart> list = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference restaurantsRef;
    current_user_singleton currentUser = current_user_singleton.getInstance();
    FirebaseDatabase cartDatabase;
    DatabaseReference cartRef;



    Button btnCheckout;
    private DatabaseReference databaseReference;

    public CartFragment() {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        // Get a reference to the database
        database = FirebaseDatabase.getInstance();
        restaurantsRef = database.getReference("Carts");


        // Configure FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(restaurantsRef, Cart.class)
                        .build();

        // Initialize RecyclerView and adapter
        btnCheckout = rootView.findViewById(R.id.btnCheckout);
        rvCart = rootView.findViewById(R.id.rvCartItem);
        rvCart.setHasFixedSize(true);
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));

        String email=currentUser.getUserId();
        String emailKey = email.replace(".", "_");
        fetchMenuFromDatabase( currentUser.getUserId());

        myAdapter = new cartAdapter(list,getContext());
        rvCart.setAdapter(myAdapter);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatabaseReference cartItemsRef = restaurantsRef.child(emailKey).child("cartItems");
                float payoutTotalAmount = 0;
                for(Cart item : list){
                    int itemQuantity = item.getItemQuantity();
                    float itemPrice = item.getItemPrice();
                    float price = itemPrice*itemQuantity;
                    payoutTotalAmount = payoutTotalAmount+ price;
                }
               String ResId = list.get(0).getRestaurantId();
                Toast.makeText(getContext(),"CF "+ResId,Toast.LENGTH_SHORT).show();
            //    Toast.makeText(getContext(), "checkout Button clicked", Toast.LENGTH_SHORT).show();
             //   Log.d("cartInRest",ResId);
                Intent intent;
                intent = new Intent(requireActivity(), checkoutActivity.class);
                intent.putExtra("payoutTotalAmount",payoutTotalAmount);
               intent.putExtra("restId",ResId);
                startActivity(intent);
               // requireActivity().finish();

            }
        });


        return rootView;
    }

    private void fetchMenuFromDatabase(String userId) {
        String emailKey = userId.replace(".", "_");
        // Assuming you have already initialized Firebase in your app



// Example user ID (replace this with the actual user ID)
// Reference the cart items node for the specific user
        DatabaseReference cartItemsRef = restaurantsRef.child(emailKey).child("cartItems");

// Attach a listener to retrieve the data
        cartItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called when the data changes
                if (dataSnapshot.exists())
                {
                  if(!list.isEmpty())
                  {
                      list.clear();
                  }
                    // Iterate through the cart items
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Cart cartItem = snapshot.getValue(Cart.class);
                       String key = snapshot.getKey();
                        if (cartItem != null) {
                            cartItem.setKey(key);
                         //   cartItem.setKey(key);
                            list.add(cartItem);
                        }
                    }
                   myAdapter.notifyDataSetChanged();

                }
                else
                {
                    // No data available
                    // Handle this case as needed
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error (if any)
                // For example, log the error message
                Log.e("Database error:", databaseError.getMessage());
            }
        });


    }
}