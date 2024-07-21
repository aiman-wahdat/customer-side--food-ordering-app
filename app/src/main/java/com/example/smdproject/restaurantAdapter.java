package com.example.smdproject;

import static androidx.core.content.ContextCompat.startActivity;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.ArrayList;

public class restaurantAdapter extends RecyclerView.Adapter<restaurantAdapter.ViewHolder> implements Filterable {

    ArrayList<Restaurant> restaurants;
    ArrayList<Restaurant> filteredList;
    ArrayList<MenuItem> menuList;
    int positionClicked;
    Context context;
    public restaurantAdapter(ArrayList<Restaurant> list)
    {
        restaurants = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.single_restaurant_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.restaurantName.setText(restaurants.get(position).getRestaurantName());
        holder.deliveryFee.setText("Delivery Fee Rs."+restaurants.get(position).getDeliveryFee()+"");
        holder.deliveryTime.setText("Delivery Time "+restaurants.get(position).getDeliveryTime()+""+" min");
        holder.TvRating.setText("( "+restaurants.get(position).getRating()+" )");

        Restaurant restaurant = restaurants.get(position);

        current_user_singleton currUser = current_user_singleton.getInstance();
        String email = currUser.getUserId();
        String emailKey = email.replace(".", "_");
        DatabaseReference  favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(emailKey);
        favoritesRef.child(restaurant.getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFavorite = dataSnapshot.exists();
                restaurant.setFavorite(isFavorite);
                holder.favoriteToggleButton.setChecked(isFavorite);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });

        menuList=new ArrayList<>();

        holder.restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MenuActivity.class);
                intent.putExtra("ResId",restaurants.get(position).getEmail());
                Toast.makeText(context, restaurants.get(position).getEmail(), Toast.LENGTH_SHORT).show();
                startActivity(context,intent,null);
            }
        });

        String url = restaurants.get(position).getURL();
        Log.e("Glide", "Print URL " + url);

        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error or show a toast to indicate the failure
                        Log.e("Glide", "Image load failed: " + e.getMessage());
                        Toast.makeText(holder.itemView.getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false; // Important to return false so Glide can continue with the default error handling
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        return false; // Continue with Glide's default behavior
                    }
                })
                .into(holder.restaurantImage);


        holder.favoriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleFavorite(isChecked,restaurants.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView restaurantName, deliveryFee, deliveryTime, TvRating;
        ImageView restaurantImage;
        ToggleButton favoriteToggleButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            deliveryFee = itemView.findViewById(R.id.deliveryFee);
            deliveryTime = itemView.findViewById(R.id.deliveryTime);
            TvRating=itemView.findViewById(R.id.TvRating);
            restaurantImage=itemView.findViewById(R.id.restaurantImage);
            favoriteToggleButton = itemView.findViewById(R.id.favoriteToggleButton);

            context=itemView.getContext();


        }
    }

    private void toggleFavorite(boolean isChecked,Restaurant restaurant) {

        current_user_singleton currentUser = current_user_singleton.getInstance();
        String email=currentUser.getUserId();
        String emailKey = email.replace(".", "_");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites").child(emailKey);


        String resEmail = restaurant.getEmail().replace(".", "_");

        if (isChecked) {
            // Add the restaurant to favorites
            favoritesRef.child(resEmail).setValue(restaurant);
            Log.d("ToggleFavorite", "Adding restaurant to favorites: " + restaurant.getRestaurantName());
            restaurant.setFavorite(true);
        } else {
            // Remove the restaurant from favorites
            favoritesRef.child(resEmail).removeValue();
            restaurant.setFavorite(false);
            Log.d("ToggleFavorite", "Removing restaurant from favorites: " + restaurant.getRestaurantName());
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                filteredList.clear();

                if (filterPattern.isEmpty()) {
                    filteredList.addAll(restaurants);
                } else {
                    for (Restaurant restaurant : restaurants) {
                        if (restaurant.getRestaurantName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(restaurant);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Restaurant>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}