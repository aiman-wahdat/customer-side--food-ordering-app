package com.example.smdproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.ViewHolder>{
    ArrayList<Cart> cartItem;
    Context context;
    current_user_singleton currUser = current_user_singleton.getInstance();
    FirebaseDatabase database;
    DatabaseReference restaurantsRef;
    int myPosition;
    public cartAdapter(ArrayList<Cart> list,Context c)
    {
        cartItem= list;
        context=c;
    }

    @NonNull
    @Override
    public cartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.single_cart_item, parent, false);
        return new cartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapter.ViewHolder holder, int position) {
        String emailKey = currUser.getUserId().replace(".", "_");
        DatabaseReference cartItemsRef = restaurantsRef.child(emailKey).child("cartItems")
                .child(cartItem.get(position).getKey());
        holder.ItemName.setText(cartItem.get(position).getItemName());
        holder.ItemDescription.setText(cartItem.get(position).getItemDescription());
        holder.ItemQuantity.setText(cartItem.get(position).getItemQuantity()+"");
        holder.ItemPrice.setText(cartItem.get(position).getItemPrice()+"");
        holder.ivDeleteCartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle("Confirmation");
                deleteDialog.setMessage("Do you really want to Delete it?");
                deleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete code
                        String userID_temp = currUser.getUserId();
                        String userID  =    userID_temp.replace('.','_');
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference cartItemsRef = database.getReference("Carts").child(userID).child("cartItems");
                        String removalKey = cartItem.get(holder.getAdapterPosition()).getKey();
                        // Remove the item from Firebase using its key
                        if(removalKey!=null){
                            cartItemsRef.child(removalKey).removeValue()
                                    .addOnSuccessListener(aVoid -> {

                                        //     Toast.makeText(context,""+removalKey,Toast.LENGTH_SHORT);
                                        // Item removed successfully
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context,"failed to delete",Toast.LENGTH_SHORT);
                                        // Failed to remove item
                                    });
                            cartItem.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());

                            //notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(context, "key not exists", Toast.LENGTH_SHORT).show();
                            Log.d("error ","key not exists");
                        }


//                        Intent intent = new Intent(context,password_items.class);
//                        startActivity(context,intent,null);
//                        ((Activity)context).finish();

                    }
                });
                deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                deleteDialog.show();

            }
        });

        holder.btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityText = holder.ItemQuantity.getText().toString().trim();
                if (!quantityText.isEmpty()) {
                    int quantity = Integer.parseInt(quantityText);
                    if (quantity > 0) {
                        quantity--; // Decrement the quantity
                        holder.ItemQuantity.setText(String.valueOf(quantity));
                        cartItemsRef.child("itemQuantity").setValue(quantity);// Update the EditText
                    } else {
                        // Optionally handle the case where quantity is already 0
                    }
                }
            }
        });

        holder.btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityText = holder.ItemQuantity.getText().toString().trim();
                if (!quantityText.isEmpty()) {
                    int quantity = Integer.parseInt(quantityText);
                    quantity++; // Increment the quantity
                    holder.ItemQuantity.setText(String.valueOf(quantity));
                    cartItemsRef.child("itemQuantity").setValue(quantity);// Update the EditText
                } else {
                    // Optionally handle the case where quantity text is empty
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView ItemName, ItemDescription, ItemQuantity,ItemPrice;
        Button btnDecrement, btnIncrement;
        ImageView ivDeleteCartItem;
       // Button
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.textFoodName);
            ItemDescription = itemView.findViewById(R.id.textDescription);
            ItemQuantity = itemView.findViewById(R.id.textQuantity);
            ItemPrice = itemView.findViewById(R.id.textPrice);
            btnDecrement=itemView.findViewById(R.id.btnDecrement);
            btnIncrement=itemView.findViewById(R.id.btnIncrement);
            ivDeleteCartItem=itemView.findViewById(R.id.ivDeleteCartItem);
            database = FirebaseDatabase.getInstance();
            restaurantsRef = database.getReference("Carts");
            String emailKey = currUser.getUserId().replace(".", "_");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), ItemName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
