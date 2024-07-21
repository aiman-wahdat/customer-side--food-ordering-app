package com.example.smdproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.ViewHolder>{
    ArrayList<Cart> cartItem;
    public cartAdapter(ArrayList<Cart> list)
    {
        cartItem= list;
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
        holder.ItemName.setText(cartItem.get(position).getItemName());
        holder.ItemDescription.setText(cartItem.get(position).getItemDescription());
        holder.ItemQuantity.setText(cartItem.get(position).getItemQuantity()+"");
        holder.ItemPrice.setText(cartItem.get(position).getItemPrice()+"");
    }

    @Override
    public int getItemCount() {
        return cartItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView ItemName, ItemDescription, ItemQuantity,ItemPrice;
        Button btnDecrement, btnIncrement;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.textFoodName);
            ItemDescription = itemView.findViewById(R.id.textDescription);
            ItemQuantity = itemView.findViewById(R.id.textQuantity);
            ItemPrice = itemView.findViewById(R.id.textPrice);
            btnDecrement=itemView.findViewById(R.id.btnDecrement);
            btnIncrement=itemView.findViewById(R.id.btnIncrement);


            btnDecrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityText = ItemQuantity.getText().toString().trim();
                    if (!quantityText.isEmpty()) {
                        int quantity = Integer.parseInt(quantityText);
                        if (quantity > 0) {
                            quantity--; // Decrement the quantity
                            ItemQuantity.setText(String.valueOf(quantity)); // Update the EditText
                        } else {
                            // Optionally handle the case where quantity is already 0
                        }
                    }
                }
            });

            btnIncrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityText = ItemQuantity.getText().toString().trim();
                    if (!quantityText.isEmpty()) {
                        int quantity = Integer.parseInt(quantityText);
                        quantity++; // Increment the quantity
                        ItemQuantity.setText(String.valueOf(quantity)); // Update the EditText
                    } else {
                        // Optionally handle the case where quantity text is empty
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(itemView.getContext(), ItemName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
