package com.Kohnqueror.you_lympics_leaderboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class PlayerStatsActivity extends AppCompatActivity implements PlayerDataManager.PlayerDataListener {

    // --- PASTE YOUR GEMINI API KEY HERE ---
    private static final String GEMINI_API_KEY = "AIzaSyAOPasqMszcJNH2KOA9KuzZpXoUntMkmsU";
    private static final int MAX_RETRIES = 3;

    private Player player;
    private String playerId;
    private TournamentSettings tournamentSettings;
    private EditText planeSeatEditText;
    private TableLayout scoresTable;
    private TextWatcher planeSeatWatcher;
    private TextView headerRound2, headerRound3, summaryTextView;
    private Button generateSummaryButton;
    private ProgressBar summaryProgressBar;
    private RequestQueue requestQueue;

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
        headerRound2 = findViewById(R.id.header_round2);
        headerRound3 = findViewById(R.id.header_round3);
        summaryTextView = findViewById(R.id.textView_summary);
        generateSummaryButton = findViewById(R.id.button_generate_summary);
        summaryProgressBar = findViewById(R.id.progressBar_summary);

        requestQueue = Volley.newRequestQueue(this);

        generateSummaryButton.setOnClickListener(v -> generateSummaryWithRetry(0));
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

    @Override
    public void onSettingsUpdated(TournamentSettings settings) {
        this.tournamentSettings = settings;
        if (player != null) {
            runOnUiThread(this::populatePlayerData);
        }
    }

    private void generateSummaryWithRetry(int retryCount) {
        if (player == null) return;
        if (GEMINI_API_KEY.equals("YOUR_GEMINI_API_KEY") || GEMINI_API_KEY.isEmpty()) {
            Toast.makeText(this, "Please add your Gemini API Key.", Toast.LENGTH_LONG).show();
            return;
        }

        if (retryCount == 0) {
            summaryProgressBar.setVisibility(View.VISIBLE);
            summaryTextView.setText("");
        }

        StringBuilder scoresData = new StringBuilder();
        for (int i = 0; i < eventNames.size(); i++) {
            int eventNum = i + 1;
            String eventName = eventNames.get(i);
            String r1Score = formatScore(player.getScore(1, eventNum), eventNum);
            scoresData.append(eventName).append(": Round 1: ").append(r1Score);
            if (tournamentSettings != null && !tournamentSettings.isRound2Locked()) {
                String r2Score = formatScore(player.getScore(2, eventNum), eventNum);
                scoresData.append(", Round 2: ").append(r2Score);
            }
            if (tournamentSettings != null && !tournamentSettings.isRound3Locked()) {
                String r3Score = formatScore(player.getScore(3, eventNum), eventNum);
                scoresData.append(", Round 3: ").append(r3Score);
            }
            scoresData.append("\n");
        }

        String prompt = "";
        switch (BuildConfig.AI_GEN) {
            case 1: //WITTY
                prompt = "Light-heartedly roast the performance of a player named " + player.getName() +
                        " in a competition based on these scores. Lower is better for timed events (ending in 's') and positional events (ending in 'st', 'nd', 'rd', 'th'). " +
                        "Be witty, use some friendly banter, and give them one piece of sarcastic but still slightly useful advice. Keep it short and funny. The scores are:\n" + scoresData;
                break;
            case 2: //HYPE_MAN
                prompt = "Act as an overly-excited sports commentator. Generate a short, energetic, and ridiculously positive summary for a player named " + player.getName() +
                        " in a competition based on these scores. Lower is better for timed events (ending in 's') and positional events (ending in 'st', 'nd', 'rd', 'th'). " +
                        " Focus only on their best results and ignore any bad scores. The scores are:\n" + scoresData;
                break;
            case 3: //DRY_ANALYTICS
                prompt = "Provide a purely statistical, data-driven summary for a player named " + player.getName() +
                        " in a competition based on these scores. Lower is better for timed events (ending in 's') and positional events (ending in 'st', 'nd', 'rd', 'th'). " +
                        "Do not use any emotional or encouraging language. Simply state their best event and their worst event based on the provided scores. The scores are:\n" + scoresData;
                break;
            case 4: //FREINDLY_RIVAL
                prompt = "Generate a short, witty, and slightly sarcastic summary for a player named " + player.getName() + "as if you were their friendly rival " +
                        " in a competition based on these scores. Lower is better for timed events (ending in 's') and positional events (ending in 'st', 'nd', 'rd', 'th'). " +
                        " Make a funny comment about their best score and a joking jab about a weaker one. Keep it light and fun. The scores are:\n" + scoresData;
                break;
            default: // ORIGINAL
                prompt = "Analyze the performance of a player named " + player.getName() +
                " in a competition based on these scores. Lower is better for timed events (ending in 's') and positional events (ending in 'st', 'nd', 'rd', 'th'). " +
                "Provide a short, encouraging, and analytical summary (2-3 sentences) of their performance, highlighting a strength and a motivational tip. The scores are:\n" + scoresData;
                break;
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

        try {
            JSONArray partsArray = new JSONArray().put(new JSONObject().put("text", prompt));
            JSONObject contentObject = new JSONObject().put("parts", partsArray);
            JSONObject requestBody = new JSONObject().put("contents", new JSONArray().put(contentObject));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        summaryProgressBar.setVisibility(View.GONE);
                        try {
                            String summary = response.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");
                            summaryTextView.setText(summary);
                        } catch (JSONException e) {
                            summaryTextView.setText("Error: Could not parse the response.");
                        }
                    },
                    error -> {
                        // --- Exponential Backoff Logic ---
                        if (shouldRetry(error) && retryCount < MAX_RETRIES) {
                            long backoff = (long) (Math.pow(2, retryCount) * 1000);
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                generateSummaryWithRetry(retryCount + 1);
                            }, backoff);
                        } else {
                            summaryProgressBar.setVisibility(View.GONE);
                            String errorMessage = "Error: Failed to generate summary.";
                            if (error.networkResponse != null) {
                                Log.e("GeminiAPI", "Status Code: " + error.networkResponse.statusCode);
                                if (error.networkResponse.data != null) {
                                    try {
                                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                        JSONObject errorObj = new JSONObject(body).getJSONObject("error");
                                        errorMessage = "Error: " + errorObj.getString("message");
                                    } catch (Exception e) {
                                        Log.e("GeminiAPI", "Could not parse error response", e);
                                    }
                                }
                            }
                            summaryTextView.setText(errorMessage);
                            Log.e("GeminiAPI", "Volley Error: " + error.toString());
                        }
                    });

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            summaryProgressBar.setVisibility(View.GONE);
            summaryTextView.setText("Error: Could not build the request.");
        }
    }

    private boolean shouldRetry(VolleyError error) {
        // Retry on server errors (5xx) which includes "overloaded"
        return error.networkResponse != null &&
                (error.networkResponse.statusCode == 429 || // Too Many Requests
                        error.networkResponse.statusCode >= 500);  // Server Errors
    }

    private void populatePlayerData() {
        if (player == null) return;

        if (tournamentSettings != null) {
            headerRound2.setVisibility(tournamentSettings.isRound2Locked() ? View.GONE : View.VISIBLE);
            headerRound3.setVisibility(tournamentSettings.isRound3Locked() ? View.GONE : View.VISIBLE);
        } else {
            headerRound2.setVisibility(View.GONE);
            headerRound3.setVisibility(View.GONE);
        }

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

            row.addView(eventName);
            row.addView(round1ScoreView);

            if (tournamentSettings != null && !tournamentSettings.isRound2Locked()) {
                TextView round2ScoreView = createTableCell(r2Score, 1);
                row.addView(round2ScoreView);
            }

            if (tournamentSettings != null && !tournamentSettings.isRound3Locked()) {
                TextView round3ScoreView = createTableCell(r3Score, 1);
                row.addView(round3ScoreView);
            }

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
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}
