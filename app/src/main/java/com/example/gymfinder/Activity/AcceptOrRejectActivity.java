
package com.example.gymfinder.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.R;

public class AcceptOrRejectActivity extends AppCompatActivity {
    private TextView gymName;
    private EditText gymStreetNumber;
    private EditText gymStreetName;
    private EditText gymDes;
    private EditText gymPrice;
    private EditText gymHrs;
    private Button btnAccept;
    private Button btnReject;
    private AppDatabase db; // Use Room database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_or_reject);

        // Standard boilerplate for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        gymName = findViewById(R.id.gymName);
        gymStreetName = findViewById(R.id.gymSreetName);
        gymStreetNumber = findViewById(R.id.gymStreetNumber);
        gymDes = findViewById(R.id.gymDes);
        gymPrice = findViewById(R.id.gymPrice);
        gymHrs = findViewById(R.id.gymHrs);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);


        db = AppDatabase.getDatabase(this);


        int gymCode = getIntent().getIntExtra("gymCode", -1);
        if (gymCode != -1) {
            loadGymData(gymCode);
        }

        btnAccept.setOnClickListener(v -> {
            Toast.makeText(this, "Gym accepted!", Toast.LENGTH_SHORT).show();

            finish();
        });

        btnReject.setOnClickListener(v -> {
            if (gymCode != -1) {

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    int deletedRows = db.gymDao().deleteGym(gymCode);

                    runOnUiThread(() -> {
                        if (deletedRows > 0) {
                            Toast.makeText(this, "Gym rejected and deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to delete gym", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    });
                });
            }
        });
    }

    private void loadGymData(int gymCode) {
    
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Gym gym = db.gymDao().getGymByCode(gymCode);


            if (gym != null) {
                runOnUiThread(() -> {
                    gymName.setText(gym.gymName);
                    gymStreetName.setText(gym.gymStreetName);
                    gymStreetNumber.setText(String.valueOf(gym.gymStreetNumber));
                    gymDes.setText(gym.gymDescription);
                    gymPrice.setText(String.valueOf(gym.price));
                    gymHrs.setText(gym.operationalHours);
                });
            }
        });
    }
}