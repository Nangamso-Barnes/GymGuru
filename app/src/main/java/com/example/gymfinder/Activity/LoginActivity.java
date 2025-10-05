package com.example.gymfinder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.gymfinder.R;

public class LoginActivity extends AppCompatActivity {
    EditText LoginUserName;
    EditText editLoginPassword;
    Button btnLogin;
    TextView txtNewUser;
    Spinner spinnerUserType;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        // Initialize UI elements outside the inset listener
        LoginUserName = findViewById(R.id.LoginUserName);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtNewUser = findViewById(R.id.txtNewUser);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerUserType.setSelection(0);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // First item is just a hint, do nothing
                } else {
                    String selectedUser = parent.getItemAtPosition(position).toString();
                    // Use selectedUser: "Admin", "User", "Gym Admin"
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }
    public void onClickedSignIn(View view) {
        String username = LoginUserName.getText().toString().trim();
        String password = editLoginPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            Integer userID = db.userDao().login(username, password);


            runOnUiThread(() -> {
                if (userID != null) {
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();


                    SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userID", userID);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();


                    Intent intent = new Intent(LoginActivity.this, DIYActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void onClickedNewUser(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}