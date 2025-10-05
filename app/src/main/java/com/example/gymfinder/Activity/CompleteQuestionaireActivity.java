package com.example.gymfinder.Activity;

import android.os.Bundle;
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

import java.util.List;

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

    // Store the ID of the logged-in user
    private int currentUserId;
    private static final String USER_ID_EXTRA = "user_id";

    // Store Question IDs using their tags for robust saving
    private int budgetQuestionId = -1;
    private int trainerQuestionId = -1;
    private int classesQuestionId = -1;
    private int timeSlotQuestionId = -1;
    private int equipmentQuestionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_questionaire);

        // Initialize DAOs from your Room Database
        AppDatabase db = AppDatabase.getDatabase(this);
        questionDao = db.questionDao();
        userResponseDao = db.userResponseDao();
        miscDao = db.miscDao();
        gymDao = db.gymDao();

        // Get user ID from the intent
        currentUserId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish(); // Can't proceed without a valid user
            return;
        }

        // Find all UI elements
        initializeViews();

        // Load questions and spinner data from the database
        populateQuestionsFromDb();
        loadSpinners();

        // Set up listeners for UI interactions
        setupListeners();

        // Handle window insets for edge-to-edge display
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
        AppDatabase.databaseWriteExecutor.execute(() -> {
            final List<Question> questions = questionDao.getAllQuestions();
            runOnUiThread(() -> {
                if (questions.isEmpty()) {
                    Toast.makeText(this, "No questionnaire questions found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Loop through questions and map them to UI elements using the stable TAG
                for (Question q : questions) {
                    if (q.questionTag == null) continue; // Skip questions that don't have a tag

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
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // FIX: Call the method names exactly as they are in your MiscDao.java
            final List<String> trainers = miscDao.getAllTrainers();
            final List<String> classes = miscDao.getAllClasses();
            final List<String> equipment = miscDao.getAllEquipment();
            final List<String> timeSlots = gymDao.getDistinctTimeSlots();

            runOnUiThread(() -> {
                // This UI code is correct and does not need to change
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
        // Collect all answers from the UI
        String budgetAnswer = budget.getText().toString();
        String trainerAnswer = getTrainerAnswer();
        String classesAnswer = getClassesAnswer();
        String timeSlotAnswer = (timeSlotSpinner.getSelectedItem() != null) ? timeSlotSpinner.getSelectedItem().toString() : "";
        String equipmentAnswer = (equipmentSpinner.getSelectedItem() != null) ? equipmentSpinner.getSelectedItem().toString() : "";

        // Save each response to the database
        saveResponse(budgetQuestionId, budgetAnswer);
        saveResponse(trainerQuestionId, trainerAnswer);
        saveResponse(classesQuestionId, classesAnswer);
        saveResponse(timeSlotQuestionId, timeSlotAnswer);
        saveResponse(equipmentQuestionId, equipmentAnswer);

        Toast.makeText(this, "Questionnaire saved!", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity after saving
    }


    private void saveResponse(int questionId, String answerText) {
        // Only save if the question ID is valid and the answer is not empty
        if (questionId != -1 && answerText != null && !answerText.isEmpty()) {
            UserResponse response = new UserResponse();
            response.userID = currentUserId;
            response.queID = questionId;
            response.answer = answerText;

            AppDatabase.databaseWriteExecutor.execute(() -> {
                userResponseDao.saveUserResponse(response);
            });
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