package com.example.gymfinder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.DAO.UserDao; // Import UserDao
import com.example.gymfinder.Database.AppDatabase; // Import AppDatabase
import com.example.gymfinder.Database.User; // Import User entity
import com.example.gymfinder.R;

public class ViewProfileDetailsActivity extends AppCompatActivity {
    TextView viewName;
    TextView viewSurname;
    Button btnEditProfile, btnDeleteProfile, btnProfileNotifications, btnChangePassword, btnSignOut;
    private UserDao userDao; // Use the DAO instead of the old Database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_details);

        viewName = findViewById(R.id.viewName);
        viewSurname = findViewById(R.id.viewSurname);
        btnEditProfile = findViewById(R.id.editProfile);
        btnDeleteProfile = findViewById(R.id.deleteProfile);
        btnProfileNotifications = findViewById(R.id.profileNotification);
        btnChangePassword = findViewById(R.id.changePassword);
        btnSignOut = findViewById(R.id.profileSignOut);

        // Initialize the UserDao from your Room AppDatabase
        userDao = AppDatabase.getDatabase(this).userDao();

        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickedEditProfile(View view) {
        startActivity(new Intent(ViewProfileDetailsActivity.this, EditProfileActivity.class));
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = prefs.getInt("userID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch data on a background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.getUserById(userId);

            // Update the UI on the main thread
            runOnUiThread(() -> {
                if (user != null) {
                    viewName.setText(user.FirstName);
                    viewSurname.setText(user.LastName);
                } else {
                    Toast.makeText(this, "Could not load user data", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}