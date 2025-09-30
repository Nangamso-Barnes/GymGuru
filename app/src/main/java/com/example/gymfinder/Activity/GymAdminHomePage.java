package com.example.gymfinder.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymfinder.R;
import com.google.android.material.button.MaterialButton;

// 1. CLASS NAME WAS CHANGED from MainActivity to GymAdminHomePage
public class GymAdminHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. LAYOUT FILE WAS CHANGED to match your XML file name
        setContentView(R.layout.activity_gym_admin_home_page);

        // --- Use the helper function to set up each option ---

        setupOption(R.id.option_add_gym, R.drawable.outline_add_circle_24, "Add Gym", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GymAdminHomePage.this, "Add Gym Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_edit_gym, R.drawable.outline_edit_24, "Edit Gym", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GymAdminHomePage.this, "Edit Gym Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_delete_gym, R.drawable.outline_delete_24, "Delete Gym", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GymAdminHomePage.this, "Delete Gym Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_notification, R.drawable.outline_notifications_24, "Notifications", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GymAdminHomePage.this, "Notifications Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the separate sign out button
        MaterialButton signOutButton = findViewById(R.id.btn_sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GymAdminHomePage.this, "Sign Out Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOption(int viewId, int iconResId, String text, View.OnClickListener listener) {
        View view = findViewById(viewId);
        ImageView icon = view.findViewById(R.id.option_icon);
        TextView textView = view.findViewById(R.id.option_text);
        icon.setImageResource(iconResId);
        textView.setText(text);
        view.setOnClickListener(listener);
    }
   // setupOption(R.id.option_add_gym, R.drawable.outline_add_circle_24, "Add Gym", new View.OnClickListener() {
       // @Override
        public void onClick(View v) {
            // 1. Create an Intent to describe the new page you want to open.
            // It needs the context (this page) and the class of the page to open.
          //  Intent intent = new Intent(GymAdminHomePage.this, AddGymActivity.class);

            // 2. Execute the Intent to start the new activity.
         //   startActivity(intent);
        }

    public void gymAdminSignOut(View view) {

    }
    //});
}