package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymfinder.R;

public class FeedbackEditGym extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Boilerplate EdgeToEdge code isn't needed for a simple confirmation screen.
        setContentView(R.layout.activity_feedback_edit_gym);
    }

    /**
     * This method is triggered by the android:onClick="onClickOK" attribute in the XML.
     * It navigates the user back to the main admin home page.
     * @param view The button that was clicked.
     */
    public void onClickOK(View view) {
        // Create an Intent to go to the admin home page.
        Intent intent = new Intent(FeedbackEditGym.this, GymAdminHomePage.class);

        // These flags ensure that when the user goes to the home page, the back stack
        // (including the edit form and gym list) is cleared. This prevents them from
        // pressing the back button and returning to a completed task.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        // Finish this feedback activity so it's also removed from the history.
        finish();
    }
}