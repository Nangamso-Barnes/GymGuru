package com.example.gymfinder.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymfinder.R;
import com.google.android.material.button.MaterialButton;

public class GymAdminHomePage extends AppCompatActivity {

    private int adminGymCode = -1; // Initialize with -1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_admin_home_page);

        // --- Get the gym code if it exists ---
        SharedPreferences prefs = getSharedPreferences("AdminSession", MODE_PRIVATE);
        adminGymCode = prefs.getInt("ADMIN_GYM_CODE", -1); // Default to -1 if not found

        // --- Setup Options ---

        // "Add Gym" is always enabled
        setupOption(R.id.option_add_gym, R.drawable.outline_add_circle_24, "Add Gym", v -> {
            Intent intent = new Intent(GymAdminHomePage.this, AddGym.class);
            startActivity(intent);
            // After adding a gym, you might need to refresh this page or update SharedPreferences
        });

        // Conditionally set up options that require a gymCode
        setupConditionalOption(R.id.option_edit_gym, R.drawable.outline_edit_24, "Edit Gym", v -> {
            Intent intent = new Intent(GymAdminHomePage.this, EditGym.class);
            intent.putExtra("gymCode", adminGymCode);
            startActivity(intent);
        });

        setupConditionalOption(R.id.option_delete_gym, R.drawable.outline_delete_24, "Delete Gym", v -> {
            Intent intent = new Intent(GymAdminHomePage.this, DeleteGym.class);
            intent.putExtra("gymCode", adminGymCode);
            startActivity(intent);
        });

        setupConditionalOption(R.id.option_view_report, R.drawable.outline_chart_data_24, "View Gym Report", v -> {
            Intent intent = new Intent(GymAdminHomePage.this, ViewGymReport.class);
            intent.putExtra("gymCode", adminGymCode);
            startActivity(intent);
        });

        // Notifications (assuming it doesn't need a specific gymCode)
        setupOption(R.id.option_notification, R.drawable.outline_notifications_24, "Notifications", v -> {
            Toast.makeText(GymAdminHomePage.this, "Notifications Clicked!", Toast.LENGTH_SHORT).show();
            // Implement Notifications activity start here if needed
        });


        // Sign Out Button
        MaterialButton signOutButton = findViewById(R.id.btn_sign_out);
        signOutButton.setOnClickListener(v -> {
            Toast.makeText(GymAdminHomePage.this, "Signing Out...", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("ADMIN_GYM_CODE");
            // editor.remove("ADMIN_USER_ID"); // Clear other session data if needed
            editor.apply();

            Intent loginIntent = new Intent(GymAdminHomePage.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });
    }

    /**
     * Sets up an option that is always available.
     */
    private void setupOption(int viewId, int iconResId, String text, View.OnClickListener listener) {
        View view = findViewById(viewId);
        if (view != null) {
            ImageView icon = view.findViewById(R.id.option_icon);
            TextView textView = view.findViewById(R.id.option_text);
            if (icon != null) {
                icon.setImageResource(iconResId);
            }
            if (textView != null) {
                textView.setText(text);
            }
            view.setOnClickListener(listener);
            view.setEnabled(true); // Ensure it's enabled
            view.setAlpha(1.0f);   // Ensure it's fully visible
        }
    }

    /**
     * Sets up an option that is ONLY available if adminGymCode is valid.
     * Otherwise, it disables the option visually.
     */
    private void setupConditionalOption(int viewId, int iconResId, String text, View.OnClickListener listener) {
        View view = findViewById(viewId);
        if (view != null) {
            ImageView icon = view.findViewById(R.id.option_icon);
            TextView textView = view.findViewById(R.id.option_text);
            if (icon != null) {
                icon.setImageResource(iconResId);
            }
            if (textView != null) {
                textView.setText(text);
            }

            if (adminGymCode != -1) {
                // Gym code exists, enable the option
                view.setOnClickListener(listener);
                view.setEnabled(true);
                view.setAlpha(1.0f); // Fully opaque
            } else {
                // No gym code, disable the option
                view.setOnClickListener(v -> Toast.makeText(GymAdminHomePage.this, "Please add a gym first.", Toast.LENGTH_SHORT).show());
                view.setEnabled(false);
                view.setAlpha(0.5f); // Make it look disabled (semi-transparent)
            }
        }
    }
}