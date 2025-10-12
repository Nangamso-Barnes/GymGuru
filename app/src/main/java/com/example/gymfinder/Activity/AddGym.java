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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ImageView gymImage;
    private Uri imageUri;

    // Data Lists
    private final ArrayList<String> equipmentList = new ArrayList<>();
    private final ArrayList<String> fitnessClassList = new ArrayList<>();
    private final ArrayList<String> trainerList = new ArrayList<>();

    // The modern way to handle activity results for picking an image
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

        // Correctly handles screen insets to prevent UI from hiding under system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_gym), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       //  Setup Toolbar
      //  Toolbar toolbar = findViewById(R.id.toolbar_add_gym);
      //  setSupportActionBar(toolbar);
    //  if (getSupportActionBar() != null) {
       //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       //     getSupportActionBar().setDisplayShowHomeEnabled(true);
       // }

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

        // Set OnClick Listeners
        gymOpeningTime.setOnClickListener(v -> showTimePicker(gymOpeningTime));
        gymClosingTime.setOnClickListener(v -> showTimePicker(gymClosingTime));
    }

    // Replace your existing onClickSave method with this one
    public void onClickSave(View view) {
        if (!validation()) {
            return;
        }

        // This part is still correct
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

        // --- THIS IS THE CORRECTED LOGIC ---
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Insert the Gym and get its ID
                long gymId = gymDao.insertGym(newGym);

                // 2. Handle Equipment
                for (String equipmentName : equipmentList) {
                    // Insert into the master Equipment table and get its ID
                    long equipId = miscDao.insertEquipment(new Equipment(equipmentName));
                    // Create the link in the join table
                    GymEquipmentCrossRef crossRef = new GymEquipmentCrossRef();
                    crossRef.gymCode = (int) gymId;
                    crossRef.equipID = (int) equipId;
                    miscDao.insertGymEquipmentCrossRef(crossRef);
                }

                // 3. Handle Fitness Classes
                for (String className : fitnessClassList) {
                    // Insert into the master GymClassType table and get its ID
                    long classId = miscDao.insertGymClassType(new GymClassType(className));
                    // Create the link in the join table
                    GymClassCrossRef crossRef = new GymClassCrossRef();
                    crossRef.gymCode = (int) gymId;
                    crossRef.classID = (int) classId;
                    miscDao.insertGymClassCrossRef(crossRef);
                }

                // 4. Handle Trainers
                for (String trainerName : trainerList) {
                    // Insert into the master TrainerType table and get its ID
                    long trainerId = miscDao.insertTrainerType(new TrainerType(trainerName));
                    // Create the link in the join table
                    GymTrainerCrossRef crossRef = new GymTrainerCrossRef();
                    crossRef.gymCode = (int) gymId;
                    crossRef.trainerID = (int) trainerId;
                    miscDao.insertGymTrainerCrossRef(crossRef);
                }

                // 5. Show success message on the UI thread
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Gym added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }

    private boolean validation() {
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
        View chipView = getLayoutInflater().inflate(R.layout.chip_item, container, false);
        TextView chipText = chipView.findViewById(R.id.chip_text);
        ImageButton removeButton = chipView.findViewById(R.id.chip_remove);

        chipText.setText(text);
        removeButton.setOnClickListener(v -> {
            list.remove(text);
            container.removeView(chipView);
        });
        container.addView(chipView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to go back? Any unsaved information will be lost.")
                .setPositiveButton("Discard", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }
}