package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.R;

public class EditQuestionsActivity extends AppCompatActivity {
    EditText questionss;
    Button btnSave;
    QuestionDao questionDao; 
    int questionId;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_questions);

        questionss = findViewById(R.id.editQuestions);
        btnSave = findViewById(R.id.btnEditQuestionSave);
        backButton = findViewById(R.id.backButton);
        questionDao = AppDatabase.getDatabase(this).questionDao();

        Intent intent = getIntent();
        questionId = intent.getIntExtra("id", -1);
        String questionText = intent.getStringExtra("question");


        if (questionId == -1) {
            Toast.makeText(this, "Error: Could not load question.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        questionss.setText(questionText);

        btnSave.setOnClickListener(v -> {
            String newText = questionss.getText().toString().trim();
            if (newText.isEmpty()) {
                Toast.makeText(this, "Question cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Question questionToUpdate = new Question();
            questionToUpdate.queID = questionId;
            questionToUpdate.question = newText;


            AppDatabase.databaseWriteExecutor.execute(() -> {
                questionDao.updateQuestion(questionToUpdate);


                runOnUiThread(() -> {
                    Toast.makeText(this, "Question updated!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(EditQuestionsActivity.this, EditQuestionListActivity.class));
        });
    }
}