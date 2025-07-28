package com.Kohnqueror.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements PlayerDataManager.PlayerDataListener {

    private List<Player> playerList = new ArrayList<>();
    private TournamentSettings tournamentSettings;
    private ImageButton menuButton;
    private PlayerAdapter adapterRest;
    private RecyclerView recyclerViewRest;
    private ImageView connectionStatusIcon;
    private CoordinatorLayout coordinatorLayout;
    private Button showWinnerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewRest = findViewById(R.id.recyclerView_leaderboard_rest);
        recyclerViewRest.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRest.setNestedScrollingEnabled(false);
        adapterRest = new PlayerAdapter(new ArrayList<>(), this, 4);
        recyclerViewRest.setAdapter(adapterRest);

        menuButton = findViewById(R.id.button_menu);
        connectionStatusIcon = findViewById(R.id.imageView_connection_status);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        showWinnerButton = findViewById(R.id.button_show_winner);

        menuButton.setOnClickListener(this::showMenuPopup);
        showWinnerButton.setOnClickListener(v -> showWinnerVideo());
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerDataManager.getInstance().addListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerDataManager.getInstance().removeListener(this);
    }

    @Override
    public void onDataUpdated(List<Player> players) {
        this.playerList = players;
        runOnUiThread(() -> {
            connectionStatusIcon.setImageResource(R.drawable.ic_status_connected);
            setupLeaderboard();
        });
    }

    @Override
    public void onSettingsUpdated(TournamentSettings settings) {
        this.tournamentSettings = settings;
    }

    private void setupLeaderboard() {
        Collections.sort(playerList, (p1, p2) -> {
            int pointsCompare = Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
            if (pointsCompare != 0) return pointsCompare;
            int seatValue1 = getSeatValue(p1.getPlaneSeat());
            int seatValue2 = getSeatValue(p2.getPlaneSeat());
            int seatCompare = Integer.compare(seatValue1, seatValue2);
            if (seatCompare != 0) return seatCompare;
            return p1.getName().compareTo(p2.getName());
        });

        if (playerList.size() >= 3) {
            populatePodium(playerList.get(0), R.id.container_1st_place, R.id.imageView_1st_avatar, R.id.textView_1st_name, R.id.textView_1st_points);
            populatePodium(playerList.get(1), R.id.container_2nd_place, R.id.imageView_2nd_avatar, R.id.textView_2nd_name, R.id.textView_2nd_points);
            populatePodium(playerList.get(2), R.id.container_3rd_place, R.id.imageView_3rd_avatar, R.id.textView_3rd_name, R.id.textView_3rd_points);
            adapterRest.submitList(playerList.subList(3, playerList.size()));
        }

        checkWinnerButtonVisibility();
    }

    private void checkWinnerButtonVisibility() {
        boolean allScoresEntered = true;
        if (playerList.isEmpty()) {
            allScoresEntered = false;
        }

        for (Player player : playerList) {
            for (int event = 1; event <= 6; event++) {
                for (int round = 1; round <= 3; round++) {
                    String score = player.getScore(round, event); // Corrected getScore parameters
                    // Correctly check for both default timed and positional scores
                    if (score == null || score.equals("0") || score.equals("0.0")) {
                        allScoresEntered = false;
                        break;
                    }
                }
                if (!allScoresEntered) break;
            }
            if (!allScoresEntered) break;
        }

        showWinnerButton.setVisibility(allScoresEntered ? View.VISIBLE : View.GONE);
    }

    private void showWinnerVideo() {
        if (playerList.isEmpty()) return;

        Player winner = playerList.get(0);
        String winnerName = winner.getName();
        String videoFileName = winnerName.toLowerCase() + "_winner"; // e.g., callum_winner.mp4

        int videoResId = getResources().getIdentifier(videoFileName, "raw", getPackageName());

        if (videoResId != 0) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_RES_ID, videoResId);
            intent.putExtra(VideoPlayerActivity.WINNER_NAME, winnerName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Winner video not found for " + winnerName, Toast.LENGTH_SHORT).show();
        }
    }

    private void populatePodium(Player player, int containerId, int avatarId, int nameId, int pointsId) {
        LinearLayout container = findViewById(containerId);
        ImageView avatarImageView = findViewById(avatarId);
        TextView nameTextView = findViewById(nameId);
        TextView pointsTextView = findViewById(pointsId);

        nameTextView.setText(player.getName());
        pointsTextView.setText(player.getTotalPoints() + " pts");

        String avatarFileName = player.getName().toLowerCase() + "px";
        int avatarResId = getResources().getIdentifier(avatarFileName, "drawable", getPackageName());
        if (avatarResId != 0) {
            avatarImageView.setImageResource(avatarResId);
        } else {
            avatarImageView.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        container.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerStatsActivity.class);
            intent.putExtra("PLAYER_ID", player.getId());
            startActivity(intent);
        });
    }

    private void showMenuPopup(View view) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        List<String> options = Arrays.asList("Edit Scores", "Tournament Settings", "Reset All Scores");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, options);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.popup_menu_width));
        int verticalOffset = -view.getHeight();
        listPopupWindow.setVerticalOffset(verticalOffset);
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.spinner_dropdown_background));
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position == 0) { // Edit Scores
                startActivity(new Intent(MainActivity.this, EventsActivity.class));
            } else if (position == 1) { // Tournament Settings
                showSettingsDialog();
            } else if (position == 2) { // Reset All Scores
                showResetConfirmationDialog();
            }
            listPopupWindow.dismiss();
        });

        Animation openAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        menuButton.startAnimation(openAnimation);

        listPopupWindow.setOnDismissListener(() -> {
            Animation closeAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
            menuButton.startAnimation(closeAnimation);
        });

        listPopupWindow.show();
    }

    private void showSettingsDialog() {
        if (tournamentSettings == null) {
            Toast.makeText(this, "Settings not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tournament_settings, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup_rounds);
        if (tournamentSettings.getCurrentRound() == 1) {
            radioGroup.check(R.id.radioButton_round1);
        } else if (tournamentSettings.getCurrentRound() == 2) {
            radioGroup.check(R.id.radioButton_round2);
        } else {
            radioGroup.check(R.id.radioButton_round3);
        }

        new AlertDialog.Builder(this)
                .setTitle("Tournament Settings")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    int newRound = 1;
                    if (selectedId == R.id.radioButton_round2) {
                        newRound = 2;
                    } else if (selectedId == R.id.radioButton_round3) {
                        newRound = 3;
                    }
                    tournamentSettings.setCurrentRound(newRound);
                    PlayerDataManager.getInstance().updateTournamentSettings(tournamentSettings);
                    Toast.makeText(this, "Settings saved.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Scores")
                .setMessage("Are you sure you want to reset all scores and plane seats? This action cannot be undone.")
                .setPositiveButton("Yes, Reset", (dialog, which) -> {
                    final List<Player> playersToRestore = new ArrayList<>(playerList);
                    final TournamentSettings settingsToRestore = tournamentSettings;

                    PlayerDataManager.getInstance().resetAllData();

                    Snackbar.make(coordinatorLayout, "All scores have been reset.", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                PlayerDataManager.getInstance().restorePlayers(playersToRestore);
                                PlayerDataManager.getInstance().updateTournamentSettings(settingsToRestore);
                                Toast.makeText(MainActivity.this, "Reset undone.", Toast.LENGTH_SHORT).show();
                            })
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private int getSeatValue(String seat) {
        if (seat == null || seat.isEmpty()) {
            return Integer.MAX_VALUE;
        }
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
}
