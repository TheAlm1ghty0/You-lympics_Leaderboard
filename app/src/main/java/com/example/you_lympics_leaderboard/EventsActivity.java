package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
    }

    /**
     * Handles clicks for all event buttons.
     * It reads the 'tag' from the button to identify which event was selected.
     * @param view The button that was clicked.
     */
    public void onEventButtonClick(View view) {
        // Get the event number from the button's tag
        int eventNumber = Integer.parseInt((String) view.getTag());

        // Create an Intent to start ScoreEntryActivity
        Intent intent = new Intent(this, ScoreEntryActivity.class);

        // Pass the selected event number to the next activity
        intent.putExtra("EVENT_NUMBER", eventNumber);

        // Start the activity
        startActivity(intent);
    }
}
