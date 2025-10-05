package com.example.gymfinder.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gymfinder.R;

public class QuestionnaireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        setupOption(R.id.option_add_question, R.drawable.outline_add_circle_24, "Add Question", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to an AddQuestionActivity
                Toast.makeText(QuestionnaireActivity.this, "Add Question Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_edit_question, R.drawable.outline_edit_24, "Edit Question", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to an EditQuestionActivity
                Toast.makeText(QuestionnaireActivity.this, "Edit Question Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        setupOption(R.id.option_delete_question, R.drawable.outline_delete_24, "Delete Question", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Intent to go to a DeleteQuestionActivity
                Toast.makeText(QuestionnaireActivity.this, "Delete Question Clicked!", Toast.LENGTH_SHORT).show();
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