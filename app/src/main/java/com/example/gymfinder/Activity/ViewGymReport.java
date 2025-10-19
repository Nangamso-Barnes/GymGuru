package com.example.gymfinder.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.ReportResult;
import com.example.gymfinder.R;
import com.example.gymfinder.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewGymReport extends AppCompatActivity {

    private Spinner reportSpinner;
    private RecyclerView reportRecyclerView;
    private ImageView backButton;
    private ReportAdapter reportAdapter;
    private AppDatabase appDatabase;
    private int currentGymCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gym_report);

        appDatabase = AppDatabase.getDatabase(this);
        reportSpinner = findViewById(R.id.reportSpinner);
        reportRecyclerView = findViewById(R.id.reportRecyclerView);
        backButton = findViewById(R.id.backButton);

        // Setup RecyclerView
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(new ArrayList<>());
        reportRecyclerView.setAdapter(reportAdapter);

        // Setup Spinner with all three report options
        String[] reportOptions = {
                "Select a report...",
                "Most Popular Classes",
                "Most Popular Equipment",
                "Most Popular Trainers"
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportSpinner.setAdapter(spinnerAdapter);

        // Handle spinner selection
        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                runReport(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                reportAdapter.updateData(new ArrayList<>());
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void runReport(int position) {
        if (position == 0) {
            reportAdapter.updateData(new ArrayList<>());
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ReportResult> results;
            if (position == 1) { // Most Popular Classes
              //  results = appDatabase.miscDao().getMostPopularClasses(currentGymCode);
            } else if (position == 2) { // Most Popular Equipment
              // results = appDatabase.miscDao().getMostPopularEquipment(currentGymCode);
            } else if (position == 3) { // Most Popular Trainers
               // results = appDatabase.miscDao().getMostPopularTrainers(currentGymCode);
            } else {
                results = new ArrayList<>();
            }
           // runOnUiThread(() -> reportAdapter.updateData(results));
        });
    }
}