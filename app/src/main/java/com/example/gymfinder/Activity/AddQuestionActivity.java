package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.R;

public class AddQuestionActivity extends AppCompatActivity {
    EditText enterQuestion;
    Spinner questionTagSpinner;
    private QuestionDao questionDao;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        enterQuestion = findViewById(R.id.enterQuestion);
        questionTagSpinner = findViewById(R.id.questionTagSpinner);
        backButton = findViewById(R.id.backButton);

        questionDao = AppDatabase.getDatabase(this).questionDao();


        setupTagSpinner();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(AddQuestionActivity.this, QuestionaireActivity.class));
        });
    }


    private void setupTagSpinner() {
        String[] tags = {"BUDGET", "TRAINER_PREFERENCE", "CLASS_PREFERENCE", "TIME_SLOT", "EQUIPMENT_TYPE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionTagSpinner.setAdapter(adapter);
    }

    public void onClickedAddOneQ(View view) {
        String questionText = enterQuestion.getText().toString().trim();


        Object selectedItem = questionTagSpinner.getSelectedItem();

        if (selectedItem == null) {
            Toast.makeText(this, "Error: No tag was selected.", Toast.LENGTH_LONG).show();
            return;
        }

        String selectedTag = selectedItem.toString();


        if (questionText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a question", Toast.LENGTH_LONG).show();
        } else {
            Question newQuestion = new Question();
            newQuestion.question = questionText;
            newQuestion.questionTag = selectedTag;

            AppDatabase.databaseWriteExecutor.execute(() -> {
                questionDao.insertQuestion(newQuestion);

                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Question has been added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}