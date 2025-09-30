package com.example.gymfinder.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// Make sure to import your R file
import com.example.gymfinder.R;

public class SystemAdminHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_admin_home_page);

        setupOption(R.id.option_create_list, R.drawable.outline_list_24, "Create List", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to a CreateListActivity
                Toast.makeText(SystemAdminHomePage.this, "Create List Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_questionnaire, R.drawable.outline_quiz_24, "Questionnaire", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to a QuestionnaireActivity
                Toast.makeText(SystemAdminHomePage.this, "Questionnaire Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_approve_gym, R.drawable.outline_check_circle_24, "Approve Gym", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to an ApproveGymActivity
                Toast.makeText(SystemAdminHomePage.this, "Approve Gym Clicked!", Toast.LENGTH_SHORT).show();
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
}