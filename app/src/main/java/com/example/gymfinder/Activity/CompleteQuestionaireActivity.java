package com.example.gymfinder.Activity;

import android.os.Bundle;
import android.os.Looper; // Import
import android.os.Handler; // Import
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.DAO.GymDao;
import com.example.gymfinder.DAO.MiscDao;
import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.DAO.UserResponseDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.Database.UserResponse;
import com.example.gymfinder.R;

import java.util.ArrayList; // Import
import java.util.List;
import java.util.concurrent.ExecutorService; // Import
import java.util.concurrent.Executors; // Import

public class CompleteQuestionaireActivity extends AppCompatActivity {
    // UI Elements
    EditText budget;
    Spinner trainerSpinner, classSpinner, timeSlotSpinner, equipmentSpinner;
    RadioGroup rbntrainers, rbnclasses;
    TextView txtBudget, txtTrainer, txtClasses, txtTimeSlot, txtEquipment;
    Button btnSave;

    // Database DAOs
    private QuestionDao questionDao;
    private UserResponseDao userResponseDao;
    private MiscDao miscDao;
    private GymDao gymDao;

    // Database Executor
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private int currentUserId;
    // --- FIX 1: This MUST match the key from UserHomePage.java ---
    private static final String USER_ID_EXTRA = "userID";

    private int budgetQuestionId = -1;
    private int trainerQuestionId = -1;
    private int classesQuestionId = -1;
    private int timeSlotQuestionId = -1;
    private int equipmentQuestionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_questionaire);


        AppDatabase db = AppDatabase.getDatabase(this);
        questionDao = db.questionDao();
        userResponseDao = db.userResponseDao();
        miscDao = db.miscDao();
        gymDao = db.gymDao();

        currentUserId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        populateQuestionsFromDb();
        loadSpinners();
        setupListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {

        budget = findViewById(R.id.createBudget);
        trainerSpinner = findViewById(R.id.trainerTypeSpinner);
        classSpinner = findViewById(R.id.classesTypeSpinner);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        equipmentSpinner = findViewById(R.id.equipmentSpinner);
        rbntrainers = findViewById(R.id.trainerRadioGroup);
        rbnclasses = findViewById(R.id.classesRadioGroup);
        txtBudget = findViewById(R.id.txtBudget);
        txtTrainer = findViewById(R.id.txtTrainer);
        txtClasses = findViewById(R.id.txtClasses);
        txtTimeSlot = findViewById(R.id.txtTimeSlot);
        txtEquipment = findViewById(R.id.txtEquipment);
        btnSave = findViewById(R.id.btnCreateQSave);
    }

    private void setupListeners() {

        rbntrainers.setOnCheckedChangeListener((group, checkedId) -> {
            trainerSpinner.setVisibility(checkedId == R.id.trainerYes ? View.VISIBLE : View.GONE);
        });

        rbnclasses.setOnCheckedChangeListener((group, checkedId) -> {
            classSpinner.setVisibility(checkedId == R.id.classesYes ? View.VISIBLE : View.GONE);
        });
    }

    private void populateQuestionsFromDb() {
        dbExecutor.execute(() -> {

            final List<Question> questions = questionDao.getAllQuestions();
            mainHandler.post(() -> {
                if (questions.isEmpty()) {
                    Toast.makeText(this, "No questionnaire questions found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Question q : questions) {
                    if (q.questionTag == null) continue;


                    switch (q.questionTag) {
                        case "BUDGET":
                            txtBudget.setText(q.question);
                            budgetQuestionId = q.queID;
                            break;
                        case "TRAINER_PREFERENCE":
                            txtTrainer.setText(q.question);
                            trainerQuestionId = q.queID;
                            break;
                        case "CLASS_PREFERENCE":
                            txtClasses.setText(q.question);
                            classesQuestionId = q.queID;
                            break;
                        case "TIME_SLOT":
                            txtTimeSlot.setText(q.question);
                            timeSlotQuestionId = q.queID;
                            break;
                        case "EQUIPMENT_TYPE":
                            txtEquipment.setText(q.question);
                            equipmentQuestionId = q.queID;
                            break;
                    }
                }
            });
        });
    }

    private void loadSpinners() {
        dbExecutor.execute(() -> {

            final List<String> trainers = miscDao.getAllTrainers();
            final List<String> classes = miscDao.getAllClasses();
            final List<String> equipment = miscDao.getAllEquipment();
            final List<String> timeSlots = gymDao.getDistinctTimeSlots();

            mainHandler.post(() -> {
                ArrayAdapter<String> trainerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainers);
                trainerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                trainerSpinner.setAdapter(trainerAdapter);

                ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classes);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(classAdapter);

                ArrayAdapter<String> equipmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, equipment);
                equipmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                equipmentSpinner.setAdapter(equipmentAdapter);

                ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
                timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSlotSpinner.setAdapter(timeSlotAdapter);
            });
        });
    }

    public void onClickedSave(View view) {

        List<UserResponse> responses = new ArrayList<>();

        addResponseToList(responses, budgetQuestionId, budget.getText().toString());
        addResponseToList(responses, trainerQuestionId, getTrainerAnswer());
        addResponseToList(responses, classesQuestionId, getClassesAnswer());
        addResponseToList(responses, timeSlotQuestionId, (timeSlotSpinner.getSelectedItem() != null) ? timeSlotSpinner.getSelectedItem().toString() : "");
        addResponseToList(responses, equipmentQuestionId, (equipmentSpinner.getSelectedItem() != null) ? equipmentSpinner.getSelectedItem().toString() : "");

        dbExecutor.execute(() -> {
            // Use the new transaction to clear old answers and insert new ones
            userResponseDao.clearAndInsertAllResponses(currentUserId, responses);
        });

        Toast.makeText(this, "Questionnaire saved!", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void addResponseToList(List<UserResponse> list, int questionId, String answerText) {
        if (questionId != -1 && answerText != null && !answerText.isEmpty()) {
            UserResponse response = new UserResponse();
            response.userID = currentUserId;
            response.queID = questionId;
            response.answer = answerText;
            list.add(response);
        }
    }


    private String getTrainerAnswer() {
        int checkedId = rbntrainers.getCheckedRadioButtonId();
        if (checkedId == R.id.trainerYes && trainerSpinner.getSelectedItem() != null) {
            return trainerSpinner.getSelectedItem().toString();
        } else if (checkedId == R.id.trainerNo) {
            return "No";
        }
        return "";
    }


    private String getClassesAnswer() {
        int checkedId = rbnclasses.getCheckedRadioButtonId();
        if (checkedId == R.id.classesYes && classSpinner.getSelectedItem() != null) {
            return classSpinner.getSelectedItem().toString();
        } else if (checkedId == R.id.classesNo) {
            return "No";
        }
        return "";
    }

    public void onClickedBack(View view) {
        finish();
    }
}