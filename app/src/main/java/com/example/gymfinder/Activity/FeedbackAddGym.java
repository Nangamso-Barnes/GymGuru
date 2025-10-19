package com.example.gymfinder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymfinder.R;

public class FeedbackAddGym extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The boilerplate EdgeToEdge code isn't needed for a simple confirmation screen
        setContentView(R.layout.activity_feedback_add_gym);
    }

    /**
     * This method is called by the android:onClick="onClickOK" attribute in your XML.
     * It navigates the user to the GymAdminHomePage.
     * @param view The button that was clicked.
     */
    public void onClickOK(View view) {
        // Create an Intent to go to the admin home page
        Intent intent = new Intent(FeedbackAddGym.this, GymAdminHomePage.class);

        // These flags clear the previous activities (like AddGym) from the history.
        // This ensures the user can't press 'back' from the home page and end up on a completed form.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        // Finish this feedback activity so it's also removed from the history
        finish();
    }
}