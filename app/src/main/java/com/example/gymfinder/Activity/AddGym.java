package com.example.gymfinder.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gymfinder.DAO.GymDao;
import com.example.gymfinder.DAO.MiscDao;
import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Equipment;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.Database.GymClassCrossRef;
import com.example.gymfinder.Database.GymClassType;
import com.example.gymfinder.Database.GymEquipmentCrossRef;
import com.example.gymfinder.Database.GymTrainerCrossRef;
import com.example.gymfinder.Database.TrainerType;
import com.example.gymfinder.R;
import com.google.android.material.chip.Chip; // --- IMPROVED --- Import Material Chip

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddGym extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    // UI Elements
    private EditText gymName, gymStrNo, gymStrName, gymDescr, gymPrice;
    private EditText inputEquipment, inputFitnessClass, inputPersonalTrainer;
    private TextView gymOpeningTime, gymClosingTime;
    private LinearLayout equipmentChipContainer, fitnessClassChipContainer, trainerChipContainer;
    private ImageView gymImage, backButton;
    private Uri imageUri;

    // Data Lists
    private final ArrayList<String> equipmentList = new ArrayList<>();
    private final ArrayList<String> fitnessClassList = new ArrayList<>();
    private final ArrayList<String> trainerList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    gymImage.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gym);

        // Initialize Views
        gymName = findViewById(R.id.inputGymName);
        gymStrNo = findViewById(R.id.inpustreetnumber);
        gymStrName = findViewById(R.id.inputStrName);
        gymDescr = findViewById(R.id.gymDescription);
        gymPrice = findViewById(R.id.inputgymPrice);
        gymOpeningTime = findViewById(R.id.txtOpeningTime);
        gymClosingTime = findViewById(R.id.txtClosingTime);
        inputEquipment = findViewById(R.id.inputEquipment);
        inputFitnessClass = findViewById(R.id.inputFitnessClass);
        inputPersonalTrainer = findViewById(R.id.inputPersonalTrainer);
        equipmentChipContainer = findViewById(R.id.equipmentChipContainer);
        fitnessClassChipContainer = findViewById(R.id.fitnessClassChipContainer);
        trainerChipContainer = findViewById(R.id.trainerChipContainer);
        gymImage = findViewById(R.id.gymImage);
        backButton = findViewById(R.id.backButton);

        // Set OnClick Listeners
        gymOpeningTime.setOnClickListener(v -> showTimePicker(gymOpeningTime));
        gymClosingTime.setOnClickListener(v -> showTimePicker(gymClosingTime));
        backButton.setOnClickListener(v -> onBackPressed());
    }

    public void onClickSave(View view) {
        if (!validation()) {
            return;
        }

        String name = gymName.getText().toString().trim();
        int streetNo = Integer.parseInt(gymStrNo.getText().toString().trim());
        String streetName = gymStrName.getText().toString().trim();
        String description = gymDescr.getText().toString().trim();
        String openingTime = gymOpeningTime.getText().toString().trim();
        String closingTime = gymClosingTime.getText().toString().trim();
        double price = Double.parseDouble(gymPrice.getText().toString().trim());
        byte[] imageBytes = imageViewToByte(gymImage);

        Gym newGym = new Gym(name, streetNo, streetName, description, price, imageBytes, openingTime, closingTime);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        GymDao gymDao = db.gymDao();
        MiscDao miscDao = db.miscDao();

        // --- CORRECTED LOGIC ---
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Insert the Gym and get its new ID
                long gymId = gymDao.insertGym(newGym);

                // 2. Handle Equipment
                for (String equipmentName : equipmentList) {
                    long equipId = miscDao.insertEquipment(new Equipment(equipmentName));
                    miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int) gymId, (int) equipId)); // Use correct constructor
                }

                // 3. Handle Fitness Classes
                for (String className : fitnessClassList) {
                    long classId = miscDao.insertGymClassType(new GymClassType(className));
                    miscDao.insertGymClassCrossRef(new GymClassCrossRef((int) gymId, (int) classId)); // Use correct constructor
                }

                // 4. Handle Trainers
                for (String trainerName : trainerList) {
                    long trainerId = miscDao.insertTrainerType(new TrainerType(trainerName));
                    miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int) gymId, (int) trainerId)); // Use correct constructor
                }

                // 5. Navigate to feedback screen on the UI thread
                runOnUiThread(() -> {
                    Intent intent = new Intent(AddGym.this, FeedbackAddGym.class);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }

    private boolean validation() {
        // Your validation logic is good, no changes needed here.
        if (gymName.getText().toString().trim().isEmpty()) {
            gymName.setError("Gym name is required!");
            return false;
        }
        if (gymStrNo.getText().toString().trim().isEmpty()) {
            gymStrNo.setError("Street number is required!");
            return false;
        }
        if (gymStrName.getText().toString().trim().isEmpty()) {
            gymStrName.setError("Street name is required!");
            return false;
        }
        if (gymDescr.getText().toString().trim().isEmpty()) {
            gymDescr.setError("Description is required!");
            return false;
        }
        if (gymPrice.getText().toString().trim().isEmpty()) {
            gymPrice.setError("Price is required!");
            return false;
        }
        if (gymImage.getDrawable() == null) {
            Toast.makeText(this, "A picture of the gym is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private byte[] imageViewToByte(ImageView imageView) {
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private void showTimePicker(TextView timeField) {
        // No changes needed here.
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    timeField.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    public void onClickAddPicture(View view) {
        // No changes needed here.
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickAddEquipment(View view) {
        // No changes needed here.
        String name = inputEquipment.getText().toString().trim();
        if (!name.isEmpty() && !equipmentList.contains(name)) {
            equipmentList.add(name);
            addChip(name, equipmentChipContainer, equipmentList);
            inputEquipment.setText("");
        }
    }

    public void onClickAddFitnessClass(View view) {
        // No changes needed here.
        String name = inputFitnessClass.getText().toString().trim();
        if (!name.isEmpty() && !fitnessClassList.contains(name)) {
            fitnessClassList.add(name);
            addChip(name, fitnessClassChipContainer, fitnessClassList);
            inputFitnessClass.setText("");
        }
    }

    public void onClickAddTrainer(View view) {
        // No changes needed here.
        String name = inputPersonalTrainer.getText().toString().trim();
        if (!name.isEmpty() && !trainerList.contains(name)) {
            trainerList.add(name);
            addChip(name, trainerChipContainer, trainerList);
            inputPersonalTrainer.setText("");
        }
    }

    // --- IMPROVED CHIP METHOD ---
    private void addChip(String text, LinearLayout container, ArrayList<String> list) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            list.remove(text);
            container.removeView(chip);
        });
        container.addView(chip);
    }

    @Override
    public void onBackPressed() {
        // No changes needed here.
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to go back? Any unsaved information will be lost.")
                .setPositiveButton("Discard", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }
}