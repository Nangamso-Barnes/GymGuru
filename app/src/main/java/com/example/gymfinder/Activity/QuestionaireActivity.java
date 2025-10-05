package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.R;

public class QuestionaireActivity extends AppCompatActivity {
    Button btnAddQuestion;
    Button btnEditQuestion;
    Button btnDeleteQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionaire);

        // Initialize buttons outside the inset listener
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnEditQuestion = findViewById(R.id.btnEditQuestion);
        btnDeleteQuestion = findViewById(R.id.btnDeleteQuestion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickedEditQ(View view) {
        startActivity(new Intent(QuestionaireActivity.this, EditQuestionListActivity.class));

    }

    public void onClickedDeleteQ(View view) {
        startActivity(new Intent(QuestionaireActivity.this, DeleteQuestionListActivity.class));
    }

    public void onClickedAddQ(View view) {
        btnAddQuestion=findViewById(R.id.btnAddQuestion);
        startActivity(new Intent(QuestionaireActivity.this, AddQuestionActivity.class));
    }
}