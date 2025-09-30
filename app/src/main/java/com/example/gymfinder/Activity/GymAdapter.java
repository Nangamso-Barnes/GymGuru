package com.example.gymfinder.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.R;

import java.util.ArrayList;
import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {

    private List<Gym> gyms = new ArrayList<>();
    private final OnGymClickListener listener;

    // Interface for handling click events
    public interface OnGymClickListener {
        void onGymClick(Gym gym);
    }

    // Constructor
    public GymAdapter(OnGymClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view by inflating the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gym_list_item, parent, false);
        return new GymViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GymViewHolder holder, int position) {
        // Get the gym at the current position and bind its data to the ViewHolder
        Gym currentGym = gyms.get(position);
        holder.bind(currentGym, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of gyms
        return gyms.size();
    }

    // Method to update the list of gyms in the adapter
    public void setGyms(List<Gym> gyms) {
        this.gyms = gyms;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    // ViewHolder class to hold the views for each item
    static class GymViewHolder extends RecyclerView.ViewHolder {
        private final TextView gymNameTextView;

        public GymViewHolder(@NonNull View itemView) {
            super(itemView);
            gymNameTextView = itemView.findViewById(R.id.gymNameTextView);
        }

        public void bind(final Gym gym, final OnGymClickListener listener) {
            gymNameTextView.setText(gym.gymName);
            itemView.setOnClickListener(v -> listener.onGymClick(gym));
        }
    }
}
