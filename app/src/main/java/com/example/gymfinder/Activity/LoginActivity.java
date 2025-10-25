package com.example.gymfinder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
// Removed unused AdapterView import
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.User;
import com.example.gymfinder.R;

// --- ADD THESE IMPORTS ---
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    EditText LoginUserName;
    EditText editLoginPassword;
    Button btnLogin;
    TextView txtNewUser;
    Spinner spinnerUserType;

    private AppDatabase db;

    // --- FIX 1: Add a local executor ---
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        LoginUserName = findViewById(R.id.LoginUserName);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtNewUser = findViewById(R.id.txtNewUser);
        spinnerUserType = findViewById(R.id.spinnerUserType);

        // This is correct
        db = AppDatabase.getDatabase(getApplicationContext()); // Changed to getInstance

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerUserType.setSelection(0);
        // Removed the unused setOnItemSelectedListener
    }

    public void onClickedSignIn(View view) {
        String username = LoginUserName.getText().toString().trim();
        String password = editLoginPassword.getText().toString().trim();
        if (spinnerUserType.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedRole = spinnerUserType.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- FIX 2: Use the local 'executor' ---
        executor.execute(() -> {
            // This code is now correct
            User user = db.userDao().login(username, password);

            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
                else if(!user.userRole.equals(selectedRole)) {
                    Toast.makeText(getApplicationContext(), "Permission denied for this role", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userID", user.userID);
                    editor.putString("userRole", user.userRole);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Intent intent;
                    switch(user.userRole){
                        case "Admin":
                            intent = new Intent(LoginActivity.this, SystemAdminHomePage.class);
                            break;

                        case "Gym Admin":
                            intent = new Intent(LoginActivity.this, GymAdminHomePage.class);
                            break;
                        case "User":
                        default:
                            intent = new Intent(LoginActivity.this, UserHomePage.class);
                            break;
                    }
                    startActivity(intent);
                    finish();
                }
            });
        });
    }

    public void onClickedNewUser(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}

