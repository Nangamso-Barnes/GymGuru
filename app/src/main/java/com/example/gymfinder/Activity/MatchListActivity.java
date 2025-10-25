package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfinder.HelperMethods.MatchListAdapter;
import com.example.gymfinder.HelperMethods.MatchViewModel;
import com.example.gymfinder.R;

public class MatchListActivity extends AppCompatActivity {

    private MatchViewModel matchViewModel;
    private RecyclerView recyclerView;
    private MatchListAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private ImageView backButton;

    private int currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        currentUserId = getIntent().getIntExtra("userID", -1);
        if (currentUserId == -1) {
            finish();
            return;
        }

        setupViews();

        matchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);

        progressBar.setVisibility(View.VISIBLE);
        matchViewModel.generateMatches(currentUserId);

        // We observe the LiveData from the ViewModel
        matchViewModel.getMatches(currentUserId).observe(this, matches -> {
            progressBar.setVisibility(View.GONE);

            if (matches == null || matches.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(matches);
            }
        });
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recycler_view_matches);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        backButton = findViewById(R.id.backButton);

        adapter = new MatchListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            finish();
        });

    }
}