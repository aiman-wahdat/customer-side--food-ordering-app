package com.example.smdproject;
import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class menuAdapter extends RecyclerView.Adapter<menuAdapter.ViewHolder> {

    ArrayList<MenuItem> menu;
    int quantity_temp;
    int myPosition;
    Context context;
    public menuAdapter(ArrayList<MenuItem> list)
    {
        menu = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.single_restaurant_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemName.setText(menu.get(position).getName());
        holder.tvItemDescription.setText(menu.get(position).getDescription());
        holder.tvItemPrice.setText(menu.get(position).getPrice()+"");
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = menu.get(position).getName();
                String desc = menu.get(position).getDescription();
                double price = menu.get(position).getPrice();
                float nPrice = (float)price;
                int quantity = 0;

                Cart cartItem = new Cart(name,desc,quantity,nPrice);
                cartItem.setRestaurantId(menu.get(position).getRestaurantId());
               // Toast.makeText(context, "inMenu "+menu.get(position).getRestaurantId(), Toast.LENGTH_SHORT).show();
               // Log.d("inMenu",menu.get(position).getRestaurantId());

                String quantityText = holder.textQuantity.getText().toString().trim();
                if (!quantityText.isEmpty()) {
                     quantity_temp = Integer.parseInt(quantityText);
                    if (quantity_temp >= 0) {
                        quantity_temp++; // increment the quantity
                        holder.textQuantity.setText(String.valueOf(quantity_temp));
                        notifyDataSetChanged();
                        cartItem.setItemQuantity(quantity_temp);// Update the EditText
                    } else {
                        // Optionally handle the case where quantity is already 0
                    }
                }



// Assuming you have a MenuItem class representing menu items

// Example restaurantId (replace this with your actual restaurantId)
                current_user_singleton currUser = current_user_singleton.getInstance();
                String UserIdTemp = currUser.getUserId();
                String UserId = UserIdTemp.replace('.','_');

// Reference the restaurant's menu items node in the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference restaurantsRef = database.getReference("Carts");
                DatabaseReference menuItemsRef = restaurantsRef.child(UserId).child("cartItems");
                String key = cartItem.getKey();

// Push menu items to generate unique keys for each item
//                menuItemsRef.push().setValue(menuItem1);
            //    Log.d("deletedItem",itemKey);
             //   String key = menuItemsRef.push().getKey();
                menuItemsRef.push().setValue(cartItem)
                        .addOnSuccessListener(aVoid -> {
                            // Menu items saved successfully
                            if (getContext() != null) {
//                                String itemKey = menuItemsRef.getKey();// Get the unique key
//                                cartItem.setKey(itemKey);
                                //Log.d("settedKey",cartItem.getKey());
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (getContext() != null) {
                            }
                        });

//
            }
        });

        String url = menu.get(position).getImageUrl();
        Log.e("Glide", "Print URL " + url);

// Create a RequestOptions object and apply a transformation to make the image round
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .transform(new CircleCrop());


        Glide.with(holder.itemView.getContext())
                .applyDefaultRequestOptions(requestOptions)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error or show a toast to indicate the failure
                        Log.e("Glide", "Image load failed: " + e.getMessage());
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false; // Important to return false so Glide can continue with the default error handling
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        return false; // Continue with Glide's default behavior
                    }
                })
                .into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemName, tvItemDescription, tvItemPrice, tvItemQauntity;
        Button btnAddToCart;
        EditText textQuantity;
        ImageView foodImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.textFoodName);
            tvItemDescription = itemView.findViewById(R.id.textDescription);
            tvItemPrice = itemView.findViewById(R.id.textPrice);
            tvItemQauntity = itemView.findViewById(R.id.textQuantity);
            btnAddToCart=itemView.findViewById(R.id.btnAddToCart);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            foodImage = itemView.findViewById(R.id.foodImage);
            context=itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), tvItemName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}