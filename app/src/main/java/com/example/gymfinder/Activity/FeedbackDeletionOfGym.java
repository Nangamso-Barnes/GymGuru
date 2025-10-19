package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymfinder.R;

public class FeedbackDeletionOfGym extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The EdgeToEdge boilerplate is not needed for a simple confirmation screen.
        setContentView(R.layout.activity_feedback_deletion_of_gym);
    }

    /**
     * This method is called when the "OK" button is clicked, as defined by the
     * android:onClick="onClickOK" attribute in the XML layout.
     * @param view The button that was clicked.
     */
    public void onClickOK(View view) {
        // Create an Intent to navigate back to the DeleteGym activity.
        Intent intent = new Intent(FeedbackDeletionOfGym.this, GymAdminHomePage.class);

        // Start the DeleteGym activity.
        startActivity(intent);

        // Finish the current FeedbackDeletionOfGym activity so the user cannot navigate back to it.
        finish();
    }
}