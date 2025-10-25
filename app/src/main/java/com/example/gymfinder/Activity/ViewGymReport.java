package com.example.gymfinder.Activity;

// Imports are alphabetical for clarity
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast; // Keep for potential future error messages
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.ReportResult;
import com.example.gymfinder.HelperMethods.ReportAdapter; // Ensure this import is correct
import com.example.gymfinder.R;

import java.util.ArrayList;
import java.util.List;

public class ViewGymReport extends AppCompatActivity {

    private Spinner reportSpinner;
    private RecyclerView reportRecyclerView;
    private ImageView backButton;
    private ReportAdapter reportAdapter;
    private AppDatabase appDatabase;
    // Removed currentGymCode, as it's not needed for global reports

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gym_report);

        // Get database instance
        appDatabase = AppDatabase.getDatabase(this);

        // Find Views
        reportSpinner = findViewById(R.id.reportSpinner);
        reportRecyclerView = findViewById(R.id.reportRecyclerView);
        backButton = findViewById(R.id.backButton);

        // Setup RecyclerView
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with an empty list
        reportAdapter = new ReportAdapter(new ArrayList<>());
        reportRecyclerView.setAdapter(reportAdapter);

        // Setup Spinner with updated report options text
        // Make sure R.array.report_options_global exists in your strings.xml
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.report_options_global, // Using a new array name for clarity
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportSpinner.setAdapter(spinnerAdapter);

        // Handle spinner selection
        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Run the report based on the selected item's position
                runReport(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Clear the results if nothing is selected
                reportAdapter.updateData(new ArrayList<>());
            }
        });

        // Set back button listener
        backButton.setOnClickListener(v -> finish()); // Closes the current activity
    }

    /**
     * Executes the appropriate global report query based on the spinner position.
     * @param position The selected item position in the spinner.
     */
    private void runReport(int position) {
        // Position 0 is usually the prompt "Select a report..."
        if (position == 0) {
            reportAdapter.updateData(new ArrayList<>()); // Clear the list
            return;
        }

        // Run the database query on a background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ReportResult> results;
            // Determine which global query to run based on spinner position
            switch (position) {
                case 1: // Assumes "Most Popular Classes (All Gyms)" is at index 1
                    results = appDatabase.miscDao().getMostPopularClassesGlobal();
                    break;
                case 2: // Assumes "Most Popular Equipment (All Gyms)" is at index 2
                    results = appDatabase.miscDao().getMostPopularEquipmentGlobal();
                    break;
                case 3: // Assumes "Most Popular Trainers (All Gyms)" is at index 3
                    results = appDatabase.miscDao().getMostPopularTrainersGlobal();
                    break;
                default:
                    results = new ArrayList<>(); // Default to empty list
                    break;
            }
            // Update the RecyclerView adapter on the main UI thread
            runOnUiThread(() -> reportAdapter.updateData(results));
        });
    }
}