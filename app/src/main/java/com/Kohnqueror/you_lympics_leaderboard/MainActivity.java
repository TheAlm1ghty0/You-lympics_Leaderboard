package com.Kohnqueror.you_lympics_leaderboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements PlayerDataManager.PlayerDataListener {

    private List<Player> playerList = new ArrayList<>();
    private ImageButton menuButton;
    private PlayerAdapter adapterRest;
    private RecyclerView recyclerViewRest;

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
        menuButton.setOnClickListener(this::showMenuPopup);
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
        runOnUiThread(this::setupLeaderboard);
    }

    private void setupLeaderboard() {
        Collections.sort(playerList, (p1, p2) -> {
            int pointsCompare = Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
            if (pointsCompare != 0) {
                return pointsCompare;
            }
            int seatValue1 = getSeatValue(p1.getPlaneSeat());
            int seatValue2 = getSeatValue(p2.getPlaneSeat());
            int seatCompare = Integer.compare(seatValue1, seatValue2);
            if (seatCompare != 0) {
                return seatCompare;
            }
            return p1.getName().compareTo(p2.getName());
        });

        if (playerList.size() >= 3) {
            populatePodium(playerList.get(0), R.id.container_1st_place, R.id.imageView_1st_avatar, R.id.textView_1st_name, R.id.textView_1st_points);
            populatePodium(playerList.get(1), R.id.container_2nd_place, R.id.imageView_2nd_avatar, R.id.textView_2nd_name, R.id.textView_2nd_points);
            populatePodium(playerList.get(2), R.id.container_3rd_place, R.id.imageView_3rd_avatar, R.id.textView_3rd_name, R.id.textView_3rd_points);

            // Use the correct method name 'submitList' to update the adapter
            adapterRest.submitList(playerList.subList(3, playerList.size()));
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
            Intent intent = new Intent(MainActivity.this, PlayerStatsActivity.class);
            intent.putExtra("PLAYER_ID", player.getId());
            startActivity(intent);
        });
    }

    private void showMenuPopup(View view) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        List<String> options = Arrays.asList("Edit Scores", "Reset All Scores");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, options);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.popup_menu_width));
        int verticalOffset = -view.getHeight();
        listPopupWindow.setVerticalOffset(verticalOffset);
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.spinner_dropdown_background));
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(MainActivity.this, EventsActivity.class));
            } else if (position == 1) {
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

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Scores")
                .setMessage("Are you sure you want to reset all scores and plane seats? This action cannot be undone.")
                .setPositiveButton("Yes, Reset", (dialog, which) -> {
                    // Reset the Firebase data
                    PlayerDataManager.getInstance().resetAllData();

                    // Reset the local UI preferences for the checkboxes
                    SharedPreferences uiPrefs = getSharedPreferences("YouLympicsUIPrefs", MODE_PRIVATE);
                    uiPrefs.edit().clear().apply();

                    // Show a confirmation message
                    Toast.makeText(this, "All scores have been reset.", Toast.LENGTH_SHORT).show();
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
