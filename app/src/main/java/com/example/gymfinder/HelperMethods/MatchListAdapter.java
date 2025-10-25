package com.example.gymfinder.HelperMethods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfinder.HelperMethods.MatchWithGym;
import com.example.gymfinder.R;

import java.text.DecimalFormat;

public class MatchListAdapter extends ListAdapter<MatchWithGym, MatchListAdapter.MatchViewHolder> {

    public MatchListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MatchWithGym> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MatchWithGym>() {
                @Override
                public boolean areItemsTheSame(@NonNull MatchWithGym oldItem, @NonNull MatchWithGym newItem) {
                    return oldItem.match.matchId == newItem.match.matchId;
                }

                @Override
                public boolean areContentsTheSame(@NonNull MatchWithGym oldItem, @NonNull MatchWithGym newItem) {
                    return oldItem.match.matchScore == newItem.match.matchScore &&
                            oldItem.gym.gymName.equals(newItem.gym.gymName);
                }
            };

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchWithGym currentMatch = getItem(position);
        holder.bind(currentMatch);
    }

    class MatchViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGymName;
        private final TextView tvMatchScore;
        private final TextView tvDistance;
        private final TextView tvPrice;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGymName = itemView.findViewById(R.id.tv_gym_name);
            tvMatchScore = itemView.findViewById(R.id.tv_match_score);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }

        public void bind(MatchWithGym matchWithGym) {
            if (matchWithGym.gym == null || matchWithGym.match == null) {
                return;
            }

            DecimalFormat df = new DecimalFormat("#.0");

            tvGymName.setText(matchWithGym.gym.gymName);
            tvMatchScore.setText("Score: " + df.format(matchWithGym.match.matchScore));
            tvDistance.setText(df.format(matchWithGym.match.distanceKm) + " km away");
            tvPrice.setText("R" + df.format(matchWithGym.gym.price) + " / month");
        }
    }
}