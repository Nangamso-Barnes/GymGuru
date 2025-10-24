package com.example.gymfinder; // This now matches the file's location

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymfinder.Database.ReportResult; // This import is still correct
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportResult> reportResults;

    public ReportAdapter(List<ReportResult> reportResults) {
        this.reportResults = reportResults;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportResult currentItem = reportResults.get(position);
        holder.itemName.setText(currentItem.name);
        holder.itemCount.setText(String.valueOf(currentItem.count));
    }

    @Override
    public int getItemCount() {
        return reportResults.size();
    }

    public void updateData(List<ReportResult> newResults) {
        this.reportResults = newResults;
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemCount;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCount = itemView.findViewById(R.id.itemCount);
        }
    }
}