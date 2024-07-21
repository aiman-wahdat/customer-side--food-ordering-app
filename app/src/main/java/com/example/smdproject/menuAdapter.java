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
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

//        String email= menu.get(position).getRestaurantId();
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference resRef = db.getReference("Restaurants");
//        // Use user's email as the key in the database (replace '.' with '_' for the key)
//        String emailKey = email.replace(".", "_");
//
//        DatabaseReference resNodeRef = resRef.child(emailKey);
//
//        resNodeRef.addListenerForSingleValueEvent(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                if (dataSnapshot.exists())
//                {
//                    // User data found, retrieve it
//                    Restaurant res = dataSnapshot.getValue(Restaurant.class);
//                    //String resUrl = restaurants.get(position).getURL();
//                    Log.e("Glide", "Print URL " + res.getURL());
//
//                    Glide.with(holder.itemView.getContext())
//                            .load(res.getURL())
//                            .placeholder(R.drawable.ic_launcher_background)
//                            .error(R.drawable.ic_launcher_foreground)
//                            .listener(new RequestListener<Drawable>() {
//                                @Override
//                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                    // Log the error or show a toast to indicate the failure
//                                    Log.e("Glide", "Image load failed: " + e.getMessage());
//                                    Toast.makeText(holder.itemView.getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
//                                    return false; // Important to return false so Glide can continue with the default error handling
//                                }
//
//                                @Override
//                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                    // Image loaded successfully
//                                    return false; // Continue with Glide's default behavior
//                                }
//                            })
//                            .into(holder.foodImage);
//
//
//
//
//                } else
//                {
//                    // User data not found
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Error occurred while retrieving data
//            }
//        });


        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = menu.get(position).getName();
                String desc = menu.get(position).getDescription();
                double price = menu.get(position).getPrice();
                float nPrice = (float) price;
                int quantity = 0;

                Cart cartItem = new Cart(name, desc, quantity, nPrice);
                cartItem.setRestaurantId(menu.get(position).getRestaurantId());

                String quantityText = holder.textQuantity.getText().toString().trim();
                if (!quantityText.isEmpty()) {
                    int quantity_temp = Integer.parseInt(quantityText);
                    if (quantity_temp >= 0) {
                        quantity_temp++; // increment the quantity

                       // holder.textQuantity.setText(String.valueOf(quantity_temp));

                        cartItem.setItemQuantity(quantity_temp); // Update the EditText
                    } else {
                        // Optionally handle the case where quantity is already 0
                    }
                }

                checkAndAddToCart(cartItem,holder.textQuantity);
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


    private void checkAndAddToCart(Cart cartItem,EditText textQuan) {
        DatabaseReference menuItemsRef = getCartItemsReference();

        menuItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean containsItemsFromDifferentRestaurant = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart existingCartItem = snapshot.getValue(Cart.class);
                    if (existingCartItem != null && !existingCartItem.getRestaurantId().equals(cartItem.getRestaurantId())) {
                        containsItemsFromDifferentRestaurant = true;
                        break;
                    }
                }

                if (containsItemsFromDifferentRestaurant) {
                    showClearCartDialog(cartItem,textQuan);
                } else {
                    textQuan.setText(String.valueOf(cartItem.getItemQuantity()));
                    notifyDataSetChanged();

                    addCartItemToCart(cartItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void showClearCartDialog(Cart cartItem,EditText textQuan) {
        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Adding items from another restaurant will clear your current cart. Do you want to proceed?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    clearCartAndAddItem(cartItem,textQuan);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void clearCartAndAddItem(Cart cartItem,EditText textQuan) {
        DatabaseReference menuItemsRef = getCartItemsReference();
        menuItemsRef.removeValue(); // Clear the cart
        menuItemsRef.push().setValue(cartItem)
                .addOnSuccessListener(aVoid -> {
                    textQuan.setText("1");
                    // Item added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle item addition failure
                });
    }

    private DatabaseReference getCartItemsReference() {
        current_user_singleton currUser = current_user_singleton.getInstance();
        String UserIdTemp = currUser.getUserId();
        String UserId = UserIdTemp.replace('.', '_');

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference restaurantsRef = database.getReference("Carts");
        return restaurantsRef.child(UserId).child("cartItems");
    }

    private void addCartItemToCart(Cart cartItem) {
        DatabaseReference menuItemsRef = getCartItemsReference();

        menuItemsRef.orderByChild("itemName").equalTo(cartItem.getItemName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cart existingCartItem = snapshot.getValue(Cart.class);
                        if (existingCartItem != null) {
                            int updatedQuantity = existingCartItem.getItemQuantity() + 1;
                            snapshot.getRef().child("itemQuantity").setValue(updatedQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        // Quantity updated successfully
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle quantity update failure
                                    });
                        }
                    }
                } else {
                    // Item not in cart, add it
                    menuItemsRef.push().setValue(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                // Item added successfully
                            })
                            .addOnFailureListener(e -> {
                                // Handle item addition failure
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
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