package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData; // Make sure to import LiveData
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.R;
import java.util.List;

public class GymListActivity extends AppCompatActivity {
    private RecyclerView gymRecyclerView;
    private GymAdapter gymAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get instance of the Room database
        db = AppDatabase.getDatabase(this);

        // Setup RecyclerView
        gymRecyclerView = findViewById(R.id.gymRecyclerView);
        gymRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Adapter and Click Listener
        gymAdapter = new GymAdapter(gym -> {
            Intent intent = new Intent(GymListActivity.this, AcceptOrRejectActivity.class);
            intent.putExtra("gymCode", gym.gymCode);
            startActivity(intent);
        });
        gymRecyclerView.setAdapter(gymAdapter);

        // --- THIS IS THE NEW CODE ---
        // Observe the LiveData from the database
        db.gymDao().getAllGyms().observe(this, gyms -> {
            // This code will run automatically whenever the data changes.
            gymAdapter.setGyms(gyms);
        });
    }

    // You no longer need onResume() or loadGyms()
    // @Override
    // protected void onResume() { ... }
    //
    // private void loadGyms() { ... }
}