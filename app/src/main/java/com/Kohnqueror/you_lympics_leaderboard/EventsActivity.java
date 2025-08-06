package com.Kohnqueror.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Handles clicks for the Twist buttons.
     * @param view The button that was clicked.
     */
    public void onTwistButtonClick(View view) {
        int twistNumber = Integer.parseInt((String) view.getTag());

        if (twistNumber == 7) { // Twist 1
            playVideo("twist1", "");
        } else if (twistNumber == 8) { // Twist 2
            showTwist2Dialog();
        }
    }

    private void showTwist2Dialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_twist2, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Twist 2")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        Button part1Button = dialogView.findViewById(R.id.button_part1);
        Button part2Button = dialogView.findViewById(R.id.button_part2);

        part1Button.setOnClickListener(v -> {
            determineAndPlayLastPlaceVideo();
            dialog.dismiss();
        });

        part2Button.setOnClickListener(v -> {
            playEndVideo();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void determineAndPlayLastPlaceVideo() {
        List<Player> currentPlayers = PlayerDataManager.getInstance().getPlayerList();
        if (currentPlayers.isEmpty()) {
            Toast.makeText(this, "Player data not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sort the list to find the last place player
        Collections.sort(currentPlayers, (p1, p2) -> {
            int pointsCompare = Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
            if (pointsCompare != 0) return pointsCompare;
            int seatValue1 = getSeatValue(p1.getPlaneSeat());
            int seatValue2 = getSeatValue(p2.getPlaneSeat());
            int seatCompare = Integer.compare(seatValue1, seatValue2);
            if (seatCompare != 0) return seatCompare;
            return p1.getName().compareTo(p2.getName());
        });

        Player lastPlacePlayer = currentPlayers.get(currentPlayers.size() - 1);
        String lastName = lastPlacePlayer.getName().toLowerCase();

        // Save the name to Firebase for Part 2
        TournamentSettings settings = PlayerDataManager.getInstance().getTournamentSettings();
        settings.setLastPlacePlayerName(lastName);
        PlayerDataManager.getInstance().updateTournamentSettings(settings);

        // Play the video
        playVideo(lastName, "_last");
    }

    private void playEndVideo() {
        TournamentSettings settings = PlayerDataManager.getInstance().getTournamentSettings();
        String lastName = settings.getLastPlacePlayerName();

        if (lastName == null || lastName.isEmpty()) {
            Toast.makeText(this, "Part 1 must be played first.", Toast.LENGTH_SHORT).show();
            return;
        }
        playVideo(lastName, "_end");
    }

    private void playVideo(String name, String suffix) {
        String videoFileName = name + suffix;
        int videoResId = getResources().getIdentifier(videoFileName, "raw", getPackageName());

        if (videoResId != 0) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_RES_ID, videoResId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Video file not found: " + videoFileName + ".mp4", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSeatValue(String seat) {
        if (seat == null || seat.isEmpty()) return Integer.MAX_VALUE;
        Pattern pattern = Pattern.compile("(\\d+)([A-F])");
        Matcher matcher = pattern.matcher(seat.toUpperCase());
        if (matcher.matches()) {
            try {
                int row = Integer.parseInt(matcher.group(1));
                int letterValue = matcher.group(2).charAt(0) - 'A';
                return (row * 10) + letterValue;
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        }
        return Integer.MAX_VALUE;
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}
