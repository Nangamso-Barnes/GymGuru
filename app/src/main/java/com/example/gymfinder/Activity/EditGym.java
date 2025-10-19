package com.example.gymfinder.Activity;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log; // Import for better logging
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback; // --- FIXED --- Import for the new back press handler
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Equipment;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.Database.GymClassCrossRef;
import com.example.gymfinder.Database.GymClassType;
import com.example.gymfinder.Database.GymEquipmentCrossRef;
import com.example.gymfinder.Database.GymTrainerCrossRef;
import com.example.gymfinder.Database.TrainerType;
import com.example.gymfinder.R;
import com.google.android.material.chip.Chip;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditGym extends AppCompatActivity {

    private static final String TAG = "EditGym"; // --- ADDED --- Tag for logging
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // UI Elements
    private EditText inputGymName, inpustreetnumber, inputStrName, gymDescription, inputgymPrice;
    private EditText inputEquipment, inputFitnessClass, inputPersonalTrainer;
    private TextView txtOpeningTime, txtClosingTime;
    private LinearLayout equipmentChipContainer, fitnessClassChipContainer, trainerChipContainer;
    private ImageView gymImage, backButton;

    // Data Holders
    // --- FIXED --- Made lists final as they are never reassigned
    private final ArrayList<String> equipmentList = new ArrayList<>();
    private final ArrayList<String> fitnessClassList = new ArrayList<>();
    private final ArrayList<String> trainerList = new ArrayList<>();
    private Uri imageUri;
    private int gymCode;

    // Database
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gym);

        appDatabase = AppDatabase.getDatabase(this);
        bindViews();

        gymCode = getIntent().getIntExtra("gymCode", -1);
        if (gymCode == -1) {
            Toast.makeText(this, "Error: No gym selected!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadGymData();
        setupListeners();

        // --- FIXED --- Modern way to handle the back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Show the confirmation dialog
                new AlertDialog.Builder(EditGym.this)
                        .setTitle("Discard Changes?")
                        .setMessage("Are you sure you want to go back? Any changes will be lost.")
                        .setPositiveButton("Discard", (dialog, which) -> {
                            // If user confirms, disable this callback and call the default back press action
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        inputGymName = findViewById(R.id.inputGymName);
        inpustreetnumber = findViewById(R.id.inpustreetnumber);
        inputStrName = findViewById(R.id.inputStrName);
        gymDescription = findViewById(R.id.gymDescription);
        inputgymPrice = findViewById(R.id.inputgymPrice);
        txtOpeningTime = findViewById(R.id.txtOpeningTime);
        txtClosingTime = findViewById(R.id.txtClosingTime);
        inputEquipment = findViewById(R.id.inputEquipment);
        equipmentChipContainer = findViewById(R.id.equipmentChipContainer);
        inputFitnessClass = findViewById(R.id.inputFitnessClass);
        fitnessClassChipContainer = findViewById(R.id.fitnessClassChipContainer);
        inputPersonalTrainer = findViewById(R.id.inputPersonalTrainer);
        trainerChipContainer = findViewById(R.id.trainerChipContainer);
        gymImage = findViewById(R.id.gymImage);
    }

    private void setupListeners() {
        // --- FIXED --- Simplified lambda to method reference for clarity
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        txtOpeningTime.setOnClickListener(v -> showTimePicker(txtOpeningTime));
        txtClosingTime.setOnClickListener(v -> showTimePicker(txtClosingTime));
    }

    private void loadGymData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Gym gym = appDatabase.gymDao().getGymByCode(gymCode);
            List<Equipment> equipment = appDatabase.miscDao().getEquipmentForGym(gymCode);
            List<GymClassType> classes = appDatabase.miscDao().getClassesForGym(gymCode);
            List<TrainerType> trainers = appDatabase.miscDao().getTrainersForGym(gymCode);

            runOnUiThread(() -> {
                if (gym != null) {
                    populateUI(gym, equipment, classes, trainers);
                } else {
                    Toast.makeText(this, "Could not find gym data.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void populateUI(Gym gym, List<Equipment> equipment, List<GymClassType> classes, List<TrainerType> trainers) {
        inputGymName.setText(gym.gymName);
        inpustreetnumber.setText(String.valueOf(gym.gymStreetNumber));
        inputStrName.setText(gym.gymStreetName);
        gymDescription.setText(gym.gymDescription);
        inputgymPrice.setText(String.format(Locale.US, "%.2f", gym.price));
        txtOpeningTime.setText(gym.openingTime);
        txtClosingTime.setText(gym.closingTime);

        if (gym.gymPicture != null && gym.gymPicture.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(gym.gymPicture, 0, gym.gymPicture.length);
            gymImage.setImageBitmap(bitmap);
        }

        equipmentList.clear();
        equipmentChipContainer.removeAllViews();
        for (Equipment item : equipment) {
            equipmentList.add(item.equipName);
            addChip(item.equipName, equipmentChipContainer, equipmentList);
        }

        fitnessClassList.clear();
        fitnessClassChipContainer.removeAllViews();
        for (GymClassType item : classes) {
            fitnessClassList.add(item.fitnessClass);
            addChip(item.fitnessClass, fitnessClassChipContainer, fitnessClassList);
        }

        trainerList.clear();
        trainerChipContainer.removeAllViews();
        for (TrainerType item : trainers) {
            trainerList.add(item.serviceList);
            addChip(item.serviceList, trainerChipContainer, trainerList);
        }
    }

    public void onClickSave(View view) {
        String name = inputGymName.getText().toString().trim();
        String streetNumStr = inpustreetnumber.getText().toString().trim();
        String streetName = inputStrName.getText().toString().trim();
        String description = gymDescription.getText().toString().trim();
        String priceStr = inputgymPrice.getText().toString().trim();

        if (name.isEmpty() || streetNumStr.isEmpty() || streetName.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Gym gymToUpdate = appDatabase.gymDao().getGymByCode(gymCode);
            if (gymToUpdate == null) return;

            gymToUpdate.gymName = name;
            gymToUpdate.gymStreetNumber = Integer.parseInt(streetNumStr);
            gymToUpdate.gymStreetName = streetName;
            gymToUpdate.gymDescription = description;
            gymToUpdate.price = Double.parseDouble(priceStr);
            gymToUpdate.openingTime = txtOpeningTime.getText().toString();
            gymToUpdate.closingTime = txtClosingTime.getText().toString();

            if (imageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    gymToUpdate.gymPicture = stream.toByteArray();
                } catch (Exception e) {
                    // --- FIXED --- Replaced printStackTrace with robust logging
                    Log.e(TAG, "Error processing image stream.", e);
                }
            }

            appDatabase.gymDao().updateGym(gymToUpdate);
            updateRelationships();

            runOnUiThread(() -> {
                Intent intent = new Intent(EditGym.this, FeedbackEditGym.class);
                startActivity(intent);
                finish();
            });
        });
    }

    private void updateRelationships() {
        appDatabase.miscDao().clearEquipmentForGym(gymCode);
        for (String name : equipmentList) {
            Equipment item = appDatabase.miscDao().findEquipmentByName(name);
            long id = (item != null) ? item.equipID : appDatabase.miscDao().insertEquipment(new Equipment(name));
            appDatabase.miscDao().insertGymEquipmentCrossRef(new GymEquipmentCrossRef(gymCode, (int) id));
        }

        appDatabase.miscDao().clearClassesForGym(gymCode);
        for (String name : fitnessClassList) {
            GymClassType item = appDatabase.miscDao().findClassByName(name);
            long id = (item != null) ? item.classID : appDatabase.miscDao().insertGymClassType(new GymClassType(name));
            appDatabase.miscDao().insertGymClassCrossRef(new GymClassCrossRef(gymCode, (int) id));
        }

        appDatabase.miscDao().clearTrainersForGym(gymCode);
        for (String name : trainerList) {
            TrainerType item = appDatabase.miscDao().findTrainerByName(name);
            long id = (item != null) ? item.trainerID : appDatabase.miscDao().insertTrainerType(new TrainerType(name));
            appDatabase.miscDao().insertGymTrainerCrossRef(new GymTrainerCrossRef(gymCode, (int) id));
        }
    }

    public void onClickAddEquipment(View view) {
        String name = inputEquipment.getText().toString().trim();
        if (!name.isEmpty() && !equipmentList.contains(name)) {
            equipmentList.add(name);
            addChip(name, equipmentChipContainer, equipmentList);
            inputEquipment.setText("");
        }
    }

    public void onClickAddFitnessClass(View view) {
        String name = inputFitnessClass.getText().toString().trim();
        if (!name.isEmpty() && !fitnessClassList.contains(name)) {
            fitnessClassList.add(name);
            addChip(name, fitnessClassChipContainer, fitnessClassList);
            inputFitnessClass.setText("");
        }
    }

    public void onClickAddTrainer(View view) {
        String name = inputPersonalTrainer.getText().toString().trim();
        if (!name.isEmpty() && !trainerList.contains(name)) {
            trainerList.add(name);
            addChip(name, trainerChipContainer, trainerList);
            inputPersonalTrainer.setText("");
        }
    }

    private void addChip(String text, LinearLayout container, ArrayList<String> list) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            container.removeView(chip);
            list.remove(text);
        });
        container.addView(chip);
    }

    private void showTimePicker(TextView timeField) {
        Calendar c = Calendar.getInstance();
        // --- FIXED --- Simplified lambda by removing unnecessary curly braces
        new TimePickerDialog(this, (view, hourOfDay, min) ->
                timeField.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, min)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    public void onClickAddPicture(View view) {
        if (checkPermission()) {
            openImagePicker();
        } else {
            requestPermission();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private boolean checkPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            gymImage.setImageURI(imageUri);
        }
    }
}