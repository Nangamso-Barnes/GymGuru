package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.HelperMethods.QuestionAdapter;
import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.R;

import java.util.ArrayList;
import java.util.List;

public class EditQuestionListActivity extends AppCompatActivity {
    ListView listView;
    QuestionDao questionDao;
    ArrayList<Question> questionList;
    QuestionAdapter adapter;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_list); // Use your new layout

        listView = findViewById(R.id.listQuestions);
        questionDao = AppDatabase.getDatabase(this).questionDao();
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(EditQuestionListActivity.this, QuestionaireActivity.class));
        });

    }

    private void loadQuestions() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Question> questionsFromDb = questionDao.getAllQuestions();
            runOnUiThread(() -> {
                questionList = new ArrayList<>(questionsFromDb);
                // Use the adapter in EDIT mode
                adapter = new QuestionAdapter(this, questionList, questionDao, QuestionAdapter.Mode.EDIT);
                listView.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions(); // Refresh list every time the screen is shown
    }
}