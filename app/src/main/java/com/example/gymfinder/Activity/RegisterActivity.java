package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymfinder.Database.AppDatabase;
import com.example.gymfinder.R;
import com.example.gymfinder.Database.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    EditText RegFirstName;
    EditText RegUsername;
    EditText RegLastName;
    EditText RegPassword;
    EditText confirmRegPassword;
    TextView regGender;
    EditText regPhoneNumber;
    EditText regStreetNumber;
    EditText RegStreetName;

    EditText email;
    Button btnRegister;
    TextView txtAccountExits;
    RadioButton regFemale, regMale;
    private ImageView ivTogglePassword, ivToggleConfirmPassword, RegBack;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            RegUsername = findViewById(R.id.regUsername);
            RegFirstName = findViewById(R.id.RegFirstName);
            RegLastName = findViewById(R.id.RegLastName);
            RegPassword = findViewById(R.id.RegPassword);
            confirmRegPassword = findViewById(R.id.RegConfirmPassword);
            email = findViewById(R.id.RegEmail);
            regGender = findViewById(R.id.RegGender);
            regFemale = findViewById(R.id.rbnRegFemale);
            regMale = findViewById(R.id.rbnRegMale);
            regPhoneNumber = findViewById(R.id.RegPhoneNumber);
            regStreetNumber = findViewById(R.id.RegStreetNumber);
            RegStreetName = findViewById(R.id.RegStreetName);
            btnRegister = findViewById(R.id.btnReg);
            ivTogglePassword = findViewById(R.id.ivTogglePassword);
            ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
            RegBack = findViewById(R.id.RegBackArrow);
            txtAccountExits = findViewById(R.id.textAlreadyExists);
            return insets;
        });
    }

    public void onClickedCreate(View view) {
        String username = RegUsername.getText().toString().trim();
        String regName = RegFirstName.getText().toString().trim();
        String regSurname = RegLastName.getText().toString().trim();
        String regEmail = email.getText().toString().trim();
        String regPhone = regPhoneNumber.getText().toString().trim();
        String RegStreetNumber = regStreetNumber.getText().toString().trim();
        String regStreetName = RegStreetName.getText().toString().trim();
        String password = RegPassword.getText().toString().trim();
        String confirmPassword = confirmRegPassword.getText().toString().trim();


        if (username.isEmpty() || regName.isEmpty() || regSurname.isEmpty() || regEmail.isEmpty() ||
                regPhone.isEmpty() || RegStreetNumber.isEmpty() || regStreetName.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(regEmail)) {
            email.setError("Please enter a valid email address.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        String regGender = "";
        if (regFemale.isChecked()) {
            regGender = "Female";
        } else if (regMale.isChecked()) {
            regGender = "Male";
        }

        if (regGender.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }
final String finalGender = regGender;
executor.execute(() ->{

    AppDatabase db = AppDatabase.getInstance(this);
    User user=new User();
    user.UserName=username;
    user.FirstName=regName;
    user.LastName=regSurname;
    user.emailAddress=regEmail;
    user.contactNumber=regPhone;
    user.streetNumber=RegStreetNumber;
    user.streetName=regStreetName;
    user.password=password;
    user.gender=finalGender;

    long userID = db.userDao().register(user);

    handler.post(() -> {
        ConstraintLayout mainLayout = findViewById(R.id.main);
        Snackbar snackbar = Snackbar.make(mainLayout, "Account has been created successfully", Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        snackbar.show();


    });


});

    }
    public void onClickedAlready(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    public void onClickedConfirmIcon(View view) {
        if (isConfirmPasswordVisible) {
            confirmRegPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
            isConfirmPasswordVisible = false;
        } else {
            confirmRegPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
            isConfirmPasswordVisible = true;
        }
        confirmRegPassword.setSelection(confirmRegPassword.getText().length());

    }

    public void onClickedPasswordIcon(View view) {
        if (isPasswordVisible) {

            RegPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = false;
        } else {

            RegPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            isPasswordVisible = true;
        }

        RegPassword.setSelection(RegPassword.getText().length());
    }

    public void onClickedBack(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}