package com.Kohnqueror.you_lympics_leaderboard;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    public static final String VIDEO_RES_ID = "VIDEO_RES_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VideoView videoView = findViewById(R.id.videoView);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        int videoResId = getIntent().getIntExtra(VIDEO_RES_ID, -1);

        if (videoResId == -1) {
            Toast.makeText(this, "Error: Video not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String videoPath = "android.resource://" + getPackageName() + "/" + videoResId;
        Uri uri = Uri.parse(videoPath);

        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            videoView.start();
        });

        videoView.setOnCompletionListener(mp -> finish());

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Error playing video.", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        });
    }

    /**
     * Handles the click for the new back button.
     * It closes the current activity, returning to the score entry screen.
     * @param view The button that was clicked.
     */
    public void onBackToEventClick(View view) {
        finish();
    }
}
