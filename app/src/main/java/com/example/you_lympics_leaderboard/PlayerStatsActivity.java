package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class PlayerStatsActivity extends AppCompatActivity implements PlayerDataManager.PlayerDataListener {

    private Player player;
    private String playerId;
    private EditText planeSeatEditText;
    private TableLayout scoresTable;
    private TextWatcher planeSeatWatcher;

    private final List<String> eventNames = Arrays.asList(
            "Strawpedo", "Race 2 Pint", "Crab Run",
            "Ping Pong Run", "Elimination Slaps", "Elimination Stacks"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        playerId = getIntent().getStringExtra("PLAYER_ID");
        planeSeatEditText = findViewById(R.id.editText_plane_seat);
        scoresTable = findViewById(R.id.tableLayout_scores);
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
        if (player != null) {
            PlayerDataManager.getInstance().updatePlayer(player);
        }
    }

    @Override
    public void onDataUpdated(List<Player> players) {
        if (playerId != null) {
            for (Player p : players) {
                if (playerId.equals(p.getId())) {
                    this.player = p;
                    runOnUiThread(this::populatePlayerData);
                    break;
                }
            }
        }
    }

    private void populatePlayerData() {
        if (player == null) return;

        TextView playerNameTextView = findViewById(R.id.textView_stats_playerName);
        TextView totalPointsTextView = findViewById(R.id.textView_stats_totalPoints);

        playerNameTextView.setText(player.getName());
        player.calculateTotalPoints();
        totalPointsTextView.setText("Total Points: " + player.getTotalPoints() + " pts");

        if (planeSeatWatcher != null) {
            planeSeatEditText.removeTextChangedListener(planeSeatWatcher);
        }
        planeSeatEditText.setText(player.getPlaneSeat());

        planeSeatWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (player != null) {
                    player.setPlaneSeat(s.toString().toUpperCase());
                }
            }
        };
        planeSeatEditText.addTextChangedListener(planeSeatWatcher);

        while (scoresTable.getChildCount() > 1) {
            scoresTable.removeViewAt(1);
        }

        for (int i = 0; i < eventNames.size(); i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setPadding(0, 8, 0, 8);

            int eventNum = i + 1;
            String r1Score = formatScore(player.getScore(1, eventNum), eventNum);
            String r2Score = formatScore(player.getScore(2, eventNum), eventNum);
            String r3Score = formatScore(player.getScore(3, eventNum), eventNum);

            TextView eventName = createTableCell(eventNames.get(i), 2);
            TextView round1ScoreView = createTableCell(r1Score, 1);
            TextView round2ScoreView = createTableCell(r2Score, 1);
            TextView round3ScoreView = createTableCell(r3Score, 1);

            row.addView(eventName);
            row.addView(round1ScoreView);
            row.addView(round2ScoreView);
            row.addView(round3ScoreView);

            scoresTable.addView(row);
        }
    }

    private String formatScore(String score, int eventNumber) {
        if (eventNumber <= 4) {
            return score.equals("0.0") ? "-" : score + "s";
        } else {
            try {
                int pos = Integer.parseInt(score);
                return pos == 0 ? "-" : pos + getOrdinalSuffix(pos);
            } catch (NumberFormatException e) {
                return "-";
            }
        }
    }

    private String getOrdinalSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
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

    /**
     * Handles the click for the new back button.
     * It closes the current activity, returning to the previous one (MainActivity).
     * @param view The button that was clicked.
     */
    public void onBackButtonClick(View view) {
        finish();
    }
}
