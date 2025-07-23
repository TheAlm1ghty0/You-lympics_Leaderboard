package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class PlayerStatsActivity extends AppCompatActivity {

    private Player player;
    private EditText planeSeatEditText;

    private final List<String> eventNames = Arrays.asList(
            "Strawpedo", "Race 2 Pint", "Crab Run",
            "Ping Pong Run", "Elimination Slaps", "Elimination Stacks"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        int playerPosition = getIntent().getIntExtra("PLAYER_POSITION", -1);
        if (playerPosition != -1) {
            player = PlayerDataManager.getInstance().getPlayer(playerPosition);
        }

        planeSeatEditText = findViewById(R.id.editText_plane_seat);

        if (player != null) {
            populatePlayerData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save data when the user leaves the screen
        PlayerDataManager.getInstance().saveData(this);
    }

    private void populatePlayerData() {
        TextView playerNameTextView = findViewById(R.id.textView_stats_playerName);
        TextView totalPointsTextView = findViewById(R.id.textView_stats_totalPoints);
        TableLayout scoresTable = findViewById(R.id.tableLayout_scores);

        playerNameTextView.setText(player.getName());
        totalPointsTextView.setText("Total Points: " + player.getTotalPoints());

        // Set plane seat and add a listener to save changes
        planeSeatEditText.setText(player.getPlaneSeat());
        planeSeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                player.setPlaneSeat(s.toString().toUpperCase());
            }
        });


        // Populate the scores table
        for (int i = 0; i < eventNames.size(); i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setPadding(0, 8, 0, 8);

            TextView eventName = createTableCell(eventNames.get(i), 2);
            TextView round1Score = createTableCell(player.getScore(1, i + 1), 1);
            TextView round2Score = createTableCell(player.getScore(2, i + 1), 1);
            TextView round3Score = createTableCell(player.getScore(3, i + 1), 1);

            row.addView(eventName);
            row.addView(round1Score);
            row.addView(round2Score);
            row.addView(round3Score);

            scoresTable.addView(row);
        }
    }

    private TextView createTableCell(String text, int weight) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
