package com.example.smdproject;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class checkoutActivity extends AppCompatActivity {

    Button placeMyOrderButton,appCompatButton2;
    ImageView payoutBackButton;
    EditText payoutName, payoutAddress, payoutPhoneNumber;
    TextView payoutTotalAmount;
    float payoutTotal=0;
    String restaurantId="";
    current_user_singleton currUser = current_user_singleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Intent intent = getIntent();

        // Check if the intent has extra data with the key "payoutTotalAmount"
        if (intent != null && intent.hasExtra("payoutTotalAmount") && intent.hasExtra("restId")) {
            // Retrieve the float value associated with the key "payoutTotalAmount"
            payoutTotal = intent.getFloatExtra("payoutTotalAmount", 0.0f);
           restaurantId=intent.getStringExtra("restId");
            Log.d("gettedRestId",restaurantId);
            // Now you have the payoutTotalAmount, you can use it as needed
         //   Toast.makeText(this, "Received payoutTotalAmount: " + payoutTotal, Toast.LENGTH_SHORT).show();
        }

        placeMyOrderButton=findViewById(R.id.placeMyOrderButton);
        payoutName=findViewById(R.id.payoutName);
        payoutAddress=findViewById(R.id.payoutAddress);
        payoutPhoneNumber=findViewById(R.id.payoutPhone);
        payoutTotalAmount = findViewById(R.id.payoutTotalAmount);
        payoutTotalAmount.setText("RS."+payoutTotal+"");
        appCompatButton2=findViewById(R.id.appCompatButton2);
        appCompatButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(checkoutActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setInputFields();
        placeMyOrderButton.setOnClickListener(new View.OnClickListener() {


           // @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String name=payoutName.getText().toString().trim();
                String add=payoutAddress.getText().toString().trim();
                String phone=payoutPhoneNumber.getText().toString().trim();

                if(name.isEmpty() || add.isEmpty() || phone.isEmpty())
                {
                    Toast.makeText(checkoutActivity.this, "PLEASE ENTER DETAILS", Toast.LENGTH_SHORT).show();
                }
                else {


                    String rest = currUser.getUserId();

                    if (!restaurantId.isEmpty()) {
                        Log.d("notNullCheck", restaurantId);

                        // Ensure payoutTotalAmount is not null or empty
                        String payoutAmountText = payoutTotalAmount.getText().toString().trim();
                        if (!TextUtils.isEmpty(payoutAmountText)) {
                          //  double orderPrice = Double.parseDouble(payoutAmountText);
                            String rest_ = restaurantId.replace('.','_');
                            // Create the Order object




                            // Reference the database and save the order
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ordersRef = database.getReference("Orders");
                            DatabaseReference orderItemRef = ordersRef.child(rest_).push();

                            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = userDatabase.getReference("users");
                            String temp = currUser.getUserId();
                            String id = temp.replace('.','_');
                            // Reference the user's node in the database
                            DatabaseReference ref = usersRef.child(id);

                            // Retrieve the user data from the database
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // User data found, dataSnapshot contains the user object
                                        User user = dataSnapshot.getValue(User.class);
                                        Order orderItem = new Order(restaurantId, "pending", payoutAmountText,user.getName());// Use restaurantId as child key
                                        orderItemRef.setValue(orderItem)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Order saved successfully
                                                    //  Toast.makeText(checkoutActivity.this, "ORDER PLACED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                                    deleteCart();
                                                    Intent intent = new Intent(checkoutActivity.this, final_anim.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to save order
                                                    Log.e("FirebaseError", "Failed to save order: " + e.getMessage());
                                                    Toast.makeText(checkoutActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                                });

                                    } else {
                                        // User data doesn't exist
                                        if (getContext() != null) {
                                            Toast.makeText(checkoutActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Failed to read value
                                    if (getContext() != null) {
                                        Toast.makeText(checkoutActivity.this, "Failed to retrieve user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        } else {
                            Log.d("nullCheck", "payoutTotalAmount is null or empty");
                            Toast.makeText(checkoutActivity.this, "Invalid order amount", Toast.LENGTH_SHORT).show();
                        }



                    } else {
                        Log.d("nullCheck", "userId is null");
                        Toast.makeText(checkoutActivity.this, "User ID is null", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

//        payoutBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

    }
    private void setInputFields(){
        String temp = currUser.getUserId();
        String id = temp.replace('.','_');
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users").child(id);
        // Retrieve the user data from the database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found, dataSnapshot contains the user object
                    User user = dataSnapshot.getValue(User.class);
                    payoutName.setText(user.getName());
                    payoutPhoneNumber.setText(user.getPhone());
                    payoutAddress.setText(user.getAddress());

                } else {
                    // User data doesn't exist
                    if (getContext() != null) {
                        Toast.makeText(checkoutActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                if (getContext() != null) {
                    Toast.makeText(checkoutActivity.this, "Failed to retrieve user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deleteCart(){

        // delete code
        String userID_temp = currUser.getUserId();
        String userID  =    userID_temp.replace('.','_');
         FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cartItemsRef = database.getReference("Carts");
        String removalKey = userID;
        // Remove the item from Firebase using its key
        if(removalKey!=null){
            cartItemsRef.child(removalKey).removeValue()
                    .addOnSuccessListener(aVoid -> {

                     //   Toast.makeText(checkoutActivity.this,"itemRemove "+removalKey,Toast.LENGTH_SHORT);
                        // Item removed successfully
                    })
                    .addOnFailureListener(e -> {
                        //    Toast.makeText(getContext(),"failed to delete",Toast.LENGTH_SHORT);
                        // Failed to remove item
                    });
//                                            cartItem.remove(holder.getAdapterPosition());
//                                            notifyItemRemoved(holder.getAdapterPosition());

            //notifyDataSetChanged();
        }
        else {
            Toast.makeText(checkoutActivity.this, "key not exists", Toast.LENGTH_SHORT).show();
            Log.d("error ","key not exists");
        }

    }
}