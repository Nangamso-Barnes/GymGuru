package com.example.gymfinder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.DAO.UserDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.User;
import com.example.gymfinder.R;

// Make sure this file is named ViewProfileOptions.java
// The class name must match the file name.
public class UserProfileOptions extends AppCompatActivity {
    TextView viewName;
    TextView viewSurname;
    private UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_options);
        viewName = findViewById(R.id.viewName);
        viewSurname = findViewById(R.id.viewSurname);
        userDao = AppDatabase.getDatabase(this).userDao();

        loadUserData();

        setupOption(R.id.option_edit_profile, R.drawable.outline_edit_24, "Edit Profile", new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Intent to go to EditProfileActivity
                startActivity(new Intent(UserProfileOptions.this, EditProfileActivity.class));
                Toast.makeText(UserProfileOptions.this, "Edit Profile Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_delete_profile, R.drawable.outline_delete_24, "Delete Profile", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to confirm and delete profile
                Toast.makeText(UserProfileOptions.this, "Delete Profile Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_change_password, R.drawable.ic_eye, "Change password", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to ChangePasswordActivity
                Toast.makeText(UserProfileOptions.this, "Change Password Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * A helper function to find an included layout and set its icon, text, and click listener.
     */
    private void setupOption(int viewId, int iconResId, String text, View.OnClickListener listener) {
        View view = findViewById(viewId);
        ImageView icon = view.findViewById(R.id.option_icon);
        TextView textView = view.findViewById(R.id.option_text);
        icon.setImageResource(iconResId);
        textView.setText(text);
        view.setOnClickListener(listener);
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

    public void gymAdminSignOut(View view) {
    }
}