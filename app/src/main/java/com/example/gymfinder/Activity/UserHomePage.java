package com.example.gymfinder.Activity;

import android.content.Intent;
import android.content.SharedPreferences; // <-- ADD THIS IMPORT
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.R;

public class UserHomePage extends AppCompatActivity {
    ImageButton btnProfile;
    Button btnPotentialMatches;
    Button btnGoQuestionnaire;

    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        // --- ADD THIS BLOCK TO GET THE LOGGED-IN USER ID ---
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getInt("userID", -1);

        if (currentUserId == -1) {
            // No user is logged in, send them back to Login
            Intent intent = new Intent(UserHomePage.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        // Initialize buttons
        btnGoQuestionnaire = findViewById(R.id.btnGoQuestions);
        btnPotentialMatches = findViewById(R.id.btnGymList);
        btnProfile = findViewById(R.id.menuButton);

        btnGoQuestionnaire.setOnClickListener(view -> {
            Intent intent = new Intent(UserHomePage.this, QuestionnaireActivity.class);
            // --- FIX: Use the variable ---
            intent.putExtra("userID", currentUserId);
            startActivity(intent);
        });

        btnPotentialMatches.setOnClickListener(view -> {
            Intent intent = new Intent(UserHomePage.this, MatchListActivity.class);
            intent.putExtra("userID", currentUserId);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(UserHomePage.this, UserProfileOptions.class);
            intent.putExtra("userID", currentUserId);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}