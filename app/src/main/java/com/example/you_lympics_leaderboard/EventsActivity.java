package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
    }

    /**
     * Handles clicks for event buttons 1-6, navigating to the score entry screen.
     * @param view The button that was clicked.
     */
    public void onEventButtonClick(View view) {
        int eventNumber = Integer.parseInt((String) view.getTag());
        Intent intent = new Intent(this, ScoreEntryActivity.class);
        intent.putExtra("EVENT_NUMBER", eventNumber);
        startActivity(intent);
    }

    /**
     * Handles clicks for the Twist buttons, navigating directly to the video player.
     * @param view The button that was clicked.
     */
    public void onTwistButtonClick(View view) {
        int twistNumber = Integer.parseInt((String) view.getTag()) - 6; // Tag 7 -> 1, Tag 8 -> 2
        String videoFileName = "twist" + twistNumber;

        int videoResId = getResources().getIdentifier(videoFileName, "raw", getPackageName());

        if (videoResId != 0) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_RES_ID, videoResId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Video file not found: " + videoFileName + ".mp4", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click for the back button.
     * @param view The button that was clicked.
     */
    public void onBackButtonClick(View view) {
        finish();
    }
}
