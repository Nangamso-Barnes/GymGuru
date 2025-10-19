package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.R;

import java.util.ArrayList;
import java.util.List;

public class DeleteGym extends AppCompatActivity {

    private Spinner gymSpinner;
    private ImageView backButton;
    private AppDatabase appDatabase;
    private List<Gym> gymList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_gym);

        // 1. Initialize views and database
        gymSpinner = findViewById(R.id.gymSpinner); // Use the new ID
        backButton = findViewById(R.id.backButton);
        appDatabase = AppDatabase.getDatabase(this);

        // 2. Set up the spinner with an empty adapter initially
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gymSpinner.setAdapter(spinnerAdapter);

        // 3. Load gyms from the database and observe for changes
        loadAndObserveGyms();

        // 4. Set a listener for the back button
        backButton.setOnClickListener(v -> finish());
    }

    private void loadAndObserveGyms() {
        // Use LiveData to fetch data on a background thread and update UI automatically
        appDatabase.gymDao().getAllGyms().observe(this, updatedGymList -> {
            if (updatedGymList == null || updatedGymList.isEmpty()) {
                Toast.makeText(this, "No gyms available to delete.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Keep a local copy of the full Gym objects
            this.gymList.clear();
            this.gymList.addAll(updatedGymList);

            // Create a list of just the names to display in the spinner
            ArrayList<String> gymNames = new ArrayList<>();
            for (Gym gym : this.gymList) {
                gymNames.add(gym.gymName);
            }

            // Update the adapter with the new gym names
            spinnerAdapter.clear();
            spinnerAdapter.addAll(gymNames);
            spinnerAdapter.notifyDataSetChanged();
        });
    }

    // This method is called by the android:onClick attribute in your XML
    public void onClickDeleteGym(View view) {
        // Check if there are any gyms loaded
        if (gymList.isEmpty()) {
            Toast.makeText(this, "There are no gyms to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the position of the selected item in the spinner
        int selectedPosition = gymSpinner.getSelectedItemPosition();

        // Get the corresponding Gym object from our list
        Gym selectedGym = gymList.get(selectedPosition);

        // Create an intent to start the confirmation activity
        Intent intent = new Intent(DeleteGym.this, DeleteGymConfirmation.class);

        // Pass the unique ID of the gym to be deleted
        intent.putExtra("GYM_ID_TO_DELETE", selectedGym.gymCode);

        // Start the activity
        startActivity(intent);
    }
}