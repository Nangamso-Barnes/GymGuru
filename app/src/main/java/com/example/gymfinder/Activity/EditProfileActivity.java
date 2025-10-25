package com.example.gymfinder.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.DAO.UserDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.User;
import com.example.gymfinder.R;

public class EditProfileActivity extends AppCompatActivity {
    EditText EditFirstName;
    EditText EditLastName;
    EditText EditPhoneNumber;
    EditText EditStreetNumber;
    EditText EditStreetName;
    EditText Editemail;
    Button btnEditSave;
    private UserDao userDao;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditFirstName = findViewById(R.id.editFirstName);
        EditLastName = findViewById(R.id.editLastName);
        Editemail = findViewById(R.id.editEmail);
        EditPhoneNumber = findViewById(R.id.editPhoneNumber);
        EditStreetNumber = findViewById(R.id.editStreetNumber);
        EditStreetName = findViewById(R.id.editStreetName);
        btnEditSave = findViewById(R.id.btnEditSave);

        userDao = AppDatabase.getDatabase(this).userDao();

        // Get the logged-in user's ID
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getInt("userID", -1);

        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickedSave(View view) {
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in. Cannot save.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get updated text from fields
        String updatedFirstName = EditFirstName.getText().toString().trim();
        String updatedLastName = EditLastName.getText().toString().trim();
        String updatedEmail = Editemail.getText().toString().trim();
        String updatedPhone = EditPhoneNumber.getText().toString().trim();
        String updatedStreetName = EditStreetName.getText().toString().trim();
        String updatedStreetNumber = EditStreetNumber.getText().toString().trim();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Fetch the existing user object from the database
            User userToUpdate = userDao.getUserById(currentUserId);

            if (userToUpdate != null) {
                // 2. Modify the fields of the object
                userToUpdate.FirstName = updatedFirstName;
                userToUpdate.LastName = updatedLastName;
                userToUpdate.emailAddress = updatedEmail;
                userToUpdate.contactNumber = updatedPhone;
                userToUpdate.streetName = updatedStreetName;
                userToUpdate.streetNumber = updatedStreetNumber;

                // 3. Pass the entire object to the update method
                int rowsAffected = userDao.updateUser(userToUpdate);

                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to the profile view
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void onDeleteProfileClicked(View view) { // <-- 1. Added "View view"
        // Show a confirmation dialog first
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to permanently delete your profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // --- 2. Added all deletion logic here ---
                    if (currentUserId == -1) {
                        Toast.makeText(this, "Error: Could not find user.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Run the database deletion on the background thread
                    AppDatabase.databaseWriteExecutor.execute(() -> {

                        // Call the DAO method
                        userDao.deleteUserById(currentUserId);

                        // After deletion, go back to the Login screen on the UI thread
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();

                            // Clear the saved login session
                            clearUserSession();

                            // Create an intent to go back to LoginActivity
                            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                            // Clear the activity stack so the user can't press "back"
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    });
                })
                .setNegativeButton("Cancel", null) // Do nothing if canceled
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void clearUserSession() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("userID");
        editor.apply();
    }
    private void loadUserData() {
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.getUserById(currentUserId);
            runOnUiThread(() -> {
                if (user != null) {
                    EditFirstName.setText(user.FirstName);
                    EditLastName.setText(user.LastName);
                    EditPhoneNumber.setText(user.contactNumber);
                    Editemail.setText(user.emailAddress);
                    EditStreetName.setText(user.streetName);
                    EditStreetNumber.setText(user.streetNumber);
                }
            });
        });
    }
}