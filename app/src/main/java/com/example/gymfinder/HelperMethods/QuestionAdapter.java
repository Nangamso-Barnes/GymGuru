package com.example.gymfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymfinder.Activity.EditQuestionsActivity;
import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.R;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

    // NEW: Enum to define the adapter's mode
    public enum Mode {
        EDIT, DELETE
    }

    private final List<Question> questions;
    private final Context context;
    private final QuestionDao questionDao;
    private final Mode mode; // NEW: Variable to hold the current mode

    public QuestionAdapter(@NonNull Context context, List<Question> questions, QuestionDao questionDao, Mode mode) {
        super(context, R.layout.list_item_question, questions);
        this.context = context;
        this.questions = questions;
        this.questionDao = questionDao;
        this.mode = mode; // NEW: Set the mode from the constructor
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.list_item_question, parent, false);

        TextView questionTextView = rowView.findViewById(R.id.questionTextView);
        ImageButton editButton = rowView.findViewById(R.id.editButton);
        ImageButton deleteButton = rowView.findViewById(R.id.deleteButton);

        Question currentQuestion = questions.get(position);
        questionTextView.setText(currentQuestion.question);

        // NEW: Show/hide buttons and set listeners based on the mode
        if (mode == Mode.EDIT) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE); // Hide delete button

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditQuestionsActivity.class);
                intent.putExtra("id", currentQuestion.queID);
                intent.putExtra("question", currentQuestion.question);
                context.startActivity(intent);
            });
        } else { // Mode is DELETE
            editButton.setVisibility(View.GONE); // Hide edit button
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this question?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteQuestion(currentQuestion))
                        .setNegativeButton("No", null)
                        .show();
            });
        }
        return rowView;
    }

    private void deleteQuestion(Question question) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            questionDao.deleteQuestion(question);
            ((Activity) context).runOnUiThread(() -> {
                remove(question);
                notifyDataSetChanged();
                Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }
}