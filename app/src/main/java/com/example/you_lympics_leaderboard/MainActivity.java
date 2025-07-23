package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private List<Player> playerList;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerList = PlayerDataManager.getInstance(this).getPlayerList();

        menuButton = findViewById(R.id.button_menu);
        menuButton.setOnClickListener(this::showPopupMenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLeaderboard();
    }

    private void setupLeaderboard() {
        PlayerDataManager.getInstance().calculateAllPlayerPoints();

        Collections.sort(playerList, (p1, p2) -> {
            int pointsCompare = Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
            if (pointsCompare != 0) {
                return pointsCompare;
            } else {
                int seatValue1 = getSeatValue(p1.getPlaneSeat());
                int seatValue2 = getSeatValue(p2.getPlaneSeat());
                return Integer.compare(seatValue1, seatValue2);
            }
        });

        if (playerList.size() >= 3) {
            populatePodium(playerList.get(0), R.id.container_1st_place, R.id.textView_1st_name, R.id.textView_1st_points);
            populatePodium(playerList.get(1), R.id.container_2nd_place, R.id.textView_2nd_name, R.id.textView_2nd_points);
            populatePodium(playerList.get(2), R.id.container_3rd_place, R.id.textView_3rd_name, R.id.textView_3rd_points);

            RecyclerView recyclerViewRest = findViewById(R.id.recyclerView_leaderboard_rest);
            recyclerViewRest.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewRest.setNestedScrollingEnabled(false);

            PlayerAdapter adapterRest = new PlayerAdapter(playerList.subList(3, playerList.size()), this, 4);
            recyclerViewRest.setAdapter(adapterRest);
        }
    }

    private void populatePodium(Player player, int containerId, int nameId, int pointsId) {
        LinearLayout container = findViewById(containerId);
        TextView nameTextView = findViewById(nameId);
        TextView pointsTextView = findViewById(pointsId);

        nameTextView.setText(player.getName());
        pointsTextView.setText(String.valueOf(player.getTotalPoints()));

        container.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayerStatsActivity.class);
            int originalPosition = PlayerDataManager.getInstance().getPlayerList().indexOf(player);
            intent.putExtra("PLAYER_POSITION", originalPosition);
            startActivity(intent);
        });
    }

    private void showPopupMenu(View view) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

        // Animate the button opening
        Animation openAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        menuButton.startAnimation(openAnimation);

        popup.setOnDismissListener(menu -> {
            // Animate the button closing
            Animation closeAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
            menuButton.startAnimation(closeAnimation);
        });

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit_scores) {
                startActivity(new Intent(MainActivity.this, EventsActivity.class));
                return true;
            } else if (itemId == R.id.action_reset_scores) {
                showResetConfirmationDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Scores")
                .setMessage("Are you sure you want to reset all scores and plane seats? This action cannot be undone.")
                .setPositiveButton("Yes, Reset", (dialog, which) -> {
                    PlayerDataManager.getInstance().resetAllData(this);
                    this.playerList = PlayerDataManager.getInstance().getPlayerList();
                    setupLeaderboard();
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
