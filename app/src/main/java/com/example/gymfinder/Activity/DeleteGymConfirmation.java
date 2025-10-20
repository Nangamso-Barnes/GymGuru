package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.R;

public class DeleteGymConfirmation extends AppCompatActivity {

    private Button cancelButton;
    private AppDatabase appDatabase;
    private int gymIdToDelete = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We don't need EdgeToEdge for a simple dialog-like screen, so it's removed for clarity.
        setContentView(R.layout.activity_delete_gym_confirmation);

        // 1. Initialize the database instance and UI elements
        appDatabase = AppDatabase.getDatabase(this);
        cancelButton = findViewById(R.id.cancelButton);

        // 2. Get the gym ID passed from the previous activity
        gymIdToDelete = getIntent().getIntExtra("GYM_ID_TO_DELETE", -1);

        // 3. Handle the Cancel button click
        cancelButton.setOnClickListener(v -> {
            // Simply finish this activity and return to the previous one
            finish();
        });
    }

    // 4. Handle the Delete button click (linked from XML's android:onClick="onClickOK")
    public void onClickOK(View view) {
        // Safety check: ensure we have a valid gym ID
        if (gymIdToDelete == -1) {
            Toast.makeText(this, "Error: No gym specified for deletion.", Toast.LENGTH_LONG).show();
            return;
        }

        // Perform the database deletion on a background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Delete the gym using the DAO method
            appDatabase.gymDao().deleteGym(gymIdToDelete);

            // After deletion, navigate to the feedback screen on the main UI thread
            runOnUiThread(() -> {
                Intent intent = new Intent(DeleteGymConfirmation.this, FeedbackDeletionOfGym.class);
                // The FLAG_ACTIVITY_CLEAR_TOP will clear the activity stack so the user can't go back
                // to the delete selection screen after a successful deletion.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close this confirmation screen
            });
        });
    }
}