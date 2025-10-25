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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

    private EditText gymName, gymStrNo, gymStrName, gymDescr, gymPrice;
    private EditText inputEquipment, inputFitnessClass, inputPersonalTrainer;
    private TextView gymOpeningTime, gymClosingTime;
    private LinearLayout equipmentChipContainer, fitnessClassChipContainer, trainerChipContainer;
    private ImageView gymImage, backButton;
    private Uri imageUri;

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
        // Using activity_add_gym.xml (the one with the gray background)
        setContentView(R.layout.activity_add_gym);

        // This listener is for edge-to-edge display, you may or may not need it
        // If your root ID is 'addnewgym', use that. If 'add_gym', use that.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addnewgym), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- MODERN BACK BUTTON HANDLING ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Show confirmation dialog
                new AlertDialog.Builder(AddGym.this)
                        .setTitle("Discard Changes?")
                        .setMessage("Are you sure you want to go back? Any unsaved information will be lost.")
                        .setPositiveButton("Discard", (dialog, which) -> {
                            setEnabled(false); // Disable this callback
                            AddGym.super.onBackPressed(); // Call the system back behavior
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // --- Find Views ---
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
        backButton = findViewById(R.id.backButton); // Find the back button

        // --- Set OnClick Listeners ---
        gymOpeningTime.setOnClickListener(v -> showTimePicker(gymOpeningTime));
        gymClosingTime.setOnClickListener(v -> showTimePicker(gymClosingTime));

        // Set listener for the back button to trigger the dispatcher
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    /**
     * Handles saving the new gym to the database.
     * This method now includes logic to check for existing equipment, classes,
     * and trainers to avoid creating duplicates in the master tables.
     */
    public void onClickSave(View view) {
        if (!validation()) {
            return; // Stop if validation fails
        }

        // Get all data from fields
        String name = gymName.getText().toString().trim();
        int streetNo = Integer.parseInt(gymStrNo.getText().toString().trim());
        String streetName = gymStrName.getText().toString().trim();
        String description = gymDescr.getText().toString().trim();
        String openingTime = gymOpeningTime.getText().toString().trim();
        String closingTime = gymClosingTime.getText().toString().trim();
        double price = Double.parseDouble(gymPrice.getText().toString().trim());
        byte[] imageBytes = imageViewToByte(gymImage);

        // Create new Gym object
        Gym newGym = new Gym(name, streetNo, streetName, description, price, imageBytes, openingTime, closingTime);

        // Get database instance and DAOs
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        GymDao gymDao = db.gymDao();
        MiscDao miscDao = db.miscDao();

        // Run database operations on a background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Insert the Gym and get its new ID
                long gymId = gymDao.insertGym(newGym);

                // 2. Handle Equipment
                for (String equipmentName : equipmentList) {
                    // Check if equipment already exists
                    Equipment item = miscDao.findEquipmentByName(equipmentName);
                    long equipId;
                    if (item != null) {
                        equipId = item.equipID; // Use existing ID
                    } else {
                        // Insert new equipment and get its ID
                        equipId = miscDao.insertEquipment(new Equipment(equipmentName));
                    }
                    // Create the cross-reference link
                    GymEquipmentCrossRef crossRef = new GymEquipmentCrossRef((int) gymId, (int) equipId);
                    miscDao.insertGymEquipmentCrossRef(crossRef);
                }

                // 3. Handle Fitness Classes
                for (String className : fitnessClassList) {
                    // Check if class already exists
                    GymClassType item = miscDao.findClassByName(className);
                    long classId;
                    if (item != null) {
                        classId = item.classID; // Use existing ID
                    } else {
                        // Insert new class and get its ID
                        classId = miscDao.insertGymClassType(new GymClassType(className));
                    }
                    // Create the cross-reference link
                    GymClassCrossRef crossRef = new GymClassCrossRef((int) gymId, (int) classId);
                    miscDao.insertGymClassCrossRef(crossRef);
                }

                // 4. Handle Trainers
                for (String trainerName : trainerList) {
                    // Check if trainer type already exists
                    TrainerType item = miscDao.findTrainerByName(trainerName);
                    long trainerId;
                    if (item != null) {
                        trainerId = item.trainerID; // Use existing ID
                    } else {
                        // Insert new trainer type and get its ID
                        trainerId = miscDao.insertTrainerType(new TrainerType(trainerName));
                    }
                    // Create the cross-reference link
                    GymTrainerCrossRef crossRef = new GymTrainerCrossRef((int) gymId, (int) trainerId);
                    miscDao.insertGymTrainerCrossRef(crossRef);
                }

                // 5. On success, show message and finish activity on the UI thread
                runOnUiThread(() -> {

                    Intent intent = new Intent(AddGym.this, FeedbackAddGym.class);
                    startActivity(intent);
                    finish(); // Close the AddGym activity
                });

            } catch (Exception e) {
                // On failure, show error message on the UI thread
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }

    /**
     * Validates all input fields before saving.
     */
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
        // Check if an image has been selected
        if (imageUri == null) {
            Toast.makeText(this, "A picture of the gym is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check if the image drawable is actually set (might not be if URI is invalid)
        if (gymImage.getDrawable() == null) {
            Toast.makeText(this, "A picture of the gym is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (equipmentList.isEmpty()) {
            Toast.makeText(this, "At least one equipment is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fitnessClassList.isEmpty()) {
            Toast.makeText(this, "At least one fitness class is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (trainerList.isEmpty()) {
            Toast.makeText(this, "At least one trainer is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Converts the content of an ImageView to a byte array for database storage.
     */
    private byte[] imageViewToByte(ImageView imageView) {
        try {
            // Ensure the drawable is a BitmapDrawable
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress as JPEG for better size efficiency
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                return stream.toByteArray();
            }
            return null; // Return null if no image is set
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Shows a TimePickerDialog to select a time.
     */
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
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    /**
     * Handles the "Add Picture" button click, checking for permissions.
     */
    public void onClickAddPicture(View view) {
        // Determine the correct permission based on Android version
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission is granted, open the image picker
            openImagePicker();
        }
    }

    /**
     * Opens the system image picker using the ActivityResultLauncher.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * Callback for permission request results.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, open image picker
                openImagePicker();
            } else {
                // Permission was denied
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- CHIP HANDLING METHODS ---

    /**
     * Handles "Add" button click for equipment.
     */
    public void onClickAddEquipment(View view) {
        String name = inputEquipment.getText().toString().trim();
        if (!name.isEmpty() && !equipmentList.contains(name)) {
            equipmentList.add(name);
            addChip(name, equipmentChipContainer, equipmentList);
            inputEquipment.setText("");
        }
    }

    /**
     * Handles "Add" button click for fitness classes.
     */
    public void onClickAddFitnessClass(View view) {
        String name = inputFitnessClass.getText().toString().trim();
        if (!name.isEmpty() && !fitnessClassList.contains(name)) {
            fitnessClassList.add(name);
            addChip(name, fitnessClassChipContainer, fitnessClassList);
            inputFitnessClass.setText("");
        }
    }

    /**
     * Handles "Add" button click for trainers.
     */
    public void onClickAddTrainer(View view) {
        String name = inputPersonalTrainer.getText().toString().trim();
        if (!name.isEmpty() && !trainerList.contains(name)) {
            trainerList.add(name);
            addChip(name, trainerChipContainer, trainerList);
            inputPersonalTrainer.setText("");
        }
    }

    /**
     * --- THIS IS THE UPDATED METHOD ---
     * Dynamically creates a chip layout (like in gymguru) and adds it to the container.
     */
    private void addChip(String text, LinearLayout container, ArrayList<String> list) {
        // Create the chip's parent layout
        LinearLayout chipLayout = new LinearLayout(this);
        chipLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Use R.drawable.bordered_box (you must have this drawable in your res/drawable folder)
        chipLayout.setBackgroundResource(R.drawable.bordered_box);

        chipLayout.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 8, 0); // Set right margin
        chipLayout.setLayoutParams(params);

        // Create the text view
        TextView chipText = new TextView(this);
        chipText.setText(text);
        chipText.setTextSize(14);
        chipText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Create the remove button
        ImageButton removeButton = new ImageButton(this);
        removeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        removeButton.setBackground(null); // Make button transparent
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        removeButton.setPadding(8, 0, 0, 0); // Add padding to the left of the 'X'

        // Set the remove logic
        final String itemToRemove = text;
        removeButton.setOnClickListener(v -> {
            list.remove(itemToRemove); // Remove from the data list
            container.removeView(chipLayout); // Remove from the UI
        });

        // Add views to the chip layout
        chipLayout.addView(chipText);
        chipLayout.addView(removeButton);

        // Add the new chip to the container
        container.addView(chipLayout);
    }
}