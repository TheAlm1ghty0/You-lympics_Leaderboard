package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class PlayerStatsActivity extends AppCompatActivity {

    private Player player;

    private final List<String> eventNames = Arrays.asList(
            "Strawpedo", "Race 2 Pint", "Crab Run",
            "Ping Pong Run", "Elimination Stacks", "Elimination Slaps"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        int playerPosition = getIntent().getIntExtra("PLAYER_POSITION", -1);
        if (playerPosition != -1) {
            player = PlayerDataManager.getInstance().getPlayer(playerPosition);
        }

        if (player != null) {
            populatePlayerData();
        }
    }

    private void populatePlayerData() {
        TextView playerNameTextView = findViewById(R.id.textView_stats_playerName);
        TextView totalPointsTextView = findViewById(R.id.textView_stats_totalPoints);
        TableLayout scoresTable = findViewById(R.id.tableLayout_scores);

        playerNameTextView.setText(player.getName());
        totalPointsTextView.setText("Total Points: " + player.getTotalPoints());

        // Populate the table
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
