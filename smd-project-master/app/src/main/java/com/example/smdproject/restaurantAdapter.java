package com.example.smdproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class restaurantAdapter extends RecyclerView.Adapter<restaurantAdapter.ViewHolder> implements Filterable {

    ArrayList<Restaurant> restaurants;
    ArrayList<Restaurant> filteredList;
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
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView restaurantName, deliveryFee, deliveryTime, TvRating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            deliveryFee = itemView.findViewById(R.id.deliveryFee);
            deliveryTime = itemView.findViewById(R.id.deliveryTime);
            TvRating=itemView.findViewById(R.id.TvRating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), restaurantName.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
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