package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.R;

import java.util.ArrayList;
import java.util.List;

public class ListOfGymsFromTheDatabase extends AppCompatActivity {

    private ListView gymListView;
    private AppDatabase appDatabase;
    private List<Gym> gymList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_from_the_database);

        // 1. Initialize UI and Database
        gymListView = findViewById(R.id.gymListView);
        appDatabase = AppDatabase.getDatabase(this);

        // 2. Setup the adapter once with an empty list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        gymListView.setAdapter(adapter);

        // 3. Load and observe the gym data
        loadAndObserveGyms();

        // 4. Set click listener
        gymListView.setOnItemClickListener((parent, view, position, id) -> {
            Gym selectedGym = gymList.get(position);
            Toast.makeText(ListOfGymsFromTheDatabase.this,
                    "You selected: " + selectedGym.gymName,
                    Toast.LENGTH_SHORT).show();

            Intent intent =new Intent(ListOfGymsFromTheDatabase.this, EditGym.class);
            intent.putExtra("gymCode",selectedGym.gymCode);
            startActivity(intent);
        });
    }

    private void loadAndObserveGyms() {
        // Get the LiveData object from the DAO and start observing it.
        // The 'this' refers to the LifecycleOwner (the Activity itself).
        appDatabase.gymDao().getAllGyms().observe(this, updatedGymList -> {
            // This code block will run automatically whenever the data in the Gym table changes.
            if (updatedGymList == null || updatedGymList.isEmpty()) {
                Toast.makeText(this, "No gyms found.", Toast.LENGTH_SHORT).show();
            } else {
                // Update our local list
                this.gymList.clear();
                this.gymList.addAll(updatedGymList);

                // Create a list of just the names for the adapter
                ArrayList<String> gymNames = new ArrayList<>();
                for (Gym gym : this.gymList) {
                    gymNames.add(gym.gymName);
                }


                adapter.clear();
                adapter.addAll(gymNames);
                adapter.notifyDataSetChanged(); // Tell the ListView to refresh
            }
        });
    }
}