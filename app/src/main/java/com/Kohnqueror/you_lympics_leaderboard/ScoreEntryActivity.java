package com.Kohnqueror.you_lympics_leaderboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScoreEntryActivity extends AppCompatActivity implements PlayerDataManager.PlayerDataListener {

    private int eventNumber;
    private Player selectedPlayer;
    private TournamentSettings tournamentSettings;
    private List<Player> sortedPlayerList;

    // UI Components
    private AutoCompleteTextView playerAutoComplete;
    private TextInputLayout playerSelectLayout; // Added for focus clearing
    private ConstraintLayout scoreFieldsContainer;
    private LinearLayout round2Container, round3Container;
    private EditText round1EditText, round2EditText, round3EditText;
    private Button video1Button, video2Button;

    private TextWatcher r1Watcher, r2Watcher, r3Watcher;

    private final List<String> eventNames = Arrays.asList(
            "Strawpedo", "Race 2 Pint", "Crab Run",
            "Ping Pong Run", "Elimination Slaps", "Elimination Stacks"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        eventNumber = getIntent().getIntExtra("EVENT_NUMBER", 0);

        findViews();
        setupVideoButtons();

        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(v -> finish());
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
        if (selectedPlayer != null) {
            PlayerDataManager.getInstance().updatePlayer(selectedPlayer);
        }
    }

    @Override
    public void onDataUpdated(List<Player> players) {
        runOnUiThread(() -> setupPlayerDropdown(players));
    }

    @Override
    public void onSettingsUpdated(TournamentSettings settings) {
        this.tournamentSettings = settings;
        if (selectedPlayer != null) {
            runOnUiThread(this::updateRoundVisibility);
        }
    }

    private void findViews() {
        TextView titleTextView = findViewById(R.id.textView_event_title);
        if (eventNumber > 0 && eventNumber <= eventNames.size()) {
            titleTextView.setText("Scores for " + eventNames.get(eventNumber - 1));
        } else {
            titleTextView.setText("Enter Scores");
        }

        playerAutoComplete = findViewById(R.id.player_autocomplete);
        playerSelectLayout = findViewById(R.id.player_select_layout); // Find the layout
        scoreFieldsContainer = findViewById(R.id.score_fields_container);
        round2Container = findViewById(R.id.container_round2);
        round3Container = findViewById(R.id.container_round3);
        round1EditText = findViewById(R.id.editText_round1_score);
        round2EditText = findViewById(R.id.editText_round2_score);
        round3EditText = findViewById(R.id.editText_round3_score);
        video1Button = findViewById(R.id.button_video1);
        video2Button = findViewById(R.id.button_video2);
    }

    private void setupVideoButtons() {
        video1Button.setOnClickListener(v -> playVideo(1));
        video2Button.setOnClickListener(v -> playVideo(2));
    }

    private void playVideo(int videoNumber) {
        if (eventNumber < 1 || eventNumber > eventNames.size()) return;
        String eventNameRaw = eventNames.get(eventNumber - 1).toLowerCase().replaceAll("\\s+", "");
        String videoFileName = eventNameRaw + videoNumber;
        int videoResId = getResources().getIdentifier(videoFileName, "raw", getPackageName());

        if (videoResId != 0) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_RES_ID, videoResId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Video file not found: " + videoFileName, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPlayerDropdown(List<Player> players) {
        sortedPlayerList = new ArrayList<>(players);
        Collections.sort(sortedPlayerList, (p1, p2) -> p1.getName().compareTo(p2.getName()));

        List<String> playerNames = new ArrayList<>();
        for (Player p : sortedPlayerList) {
            playerNames.add(p.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, playerNames);
        playerAutoComplete.setAdapter(adapter);

        playerAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            if (selectedPlayer != null) {
                PlayerDataManager.getInstance().updatePlayer(selectedPlayer);
            }
            selectedPlayer = sortedPlayerList.get(position);
            updateUiForSelectedPlayer();
        });

        playerAutoComplete.setOnDismissListener(() -> {
            playerSelectLayout.clearFocus();
            playerAutoComplete.clearFocus();
        });
    }

    private void updateUiForSelectedPlayer() {
        if (selectedPlayer == null) return;

        scoreFieldsContainer.setVisibility(View.VISIBLE);
        updateRoundVisibility();

        if (r1Watcher != null) round1EditText.removeTextChangedListener(r1Watcher);
        if (r2Watcher != null) round2EditText.removeTextChangedListener(r2Watcher);
        if (r3Watcher != null) round3EditText.removeTextChangedListener(r3Watcher);

        if (eventNumber <= 4) { // Timed
            configureEditText(round1EditText, "SS.ms", false);
            configureEditText(round2EditText, "SS.ms", false);
            configureEditText(round3EditText, "SS.ms", false);
        } else { // Positional
            configureEditText(round1EditText, "1-10", true);
            configureEditText(round2EditText, "1-10", true);
            configureEditText(round3EditText, "1-10", true);
        }

        String r1Score = selectedPlayer.getScore(1, eventNumber);
        String r2Score = selectedPlayer.getScore(2, eventNumber);
        String r3Score = selectedPlayer.getScore(3, eventNumber);

        if (eventNumber <= 4) {
            round1EditText.setText(r1Score.equals("0.0") ? "" : r1Score);
            round2EditText.setText(r2Score.equals("0.0") ? "" : r2Score);
            round3EditText.setText(r3Score.equals("0.0") ? "" : r3Score);
        } else {
            round1EditText.setText(r1Score.equals("0") ? "" : r1Score);
            round2EditText.setText(r2Score.equals("0") ? "" : r2Score);
            round3EditText.setText(r3Score.equals("0") ? "" : r3Score);
        }

        r1Watcher = createTextWatcher(1);
        r2Watcher = createTextWatcher(2);
        r3Watcher = createTextWatcher(3);

        round1EditText.addTextChangedListener(r1Watcher);
        round2EditText.addTextChangedListener(r2Watcher);
        round3EditText.addTextChangedListener(r3Watcher);
    }

    private void updateRoundVisibility() {
        if (tournamentSettings == null) return;
        round2Container.setVisibility(tournamentSettings.isRound2Locked() ? View.GONE : View.VISIBLE);
        round3Container.setVisibility(tournamentSettings.isRound3Locked() ? View.GONE : View.VISIBLE);
    }

    private void configureEditText(EditText editText, String hint, boolean isPositional) {
        editText.setHint(hint);
        if (isPositional) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 10)});
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setFilters(new InputFilter[]{});
        }
    }

    private TextWatcher createTextWatcher(final int round) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (selectedPlayer != null) {
                    selectedPlayer.setScore(round, eventNumber, s.toString());
                }
            }
        };
    }

    private static class InputFilterMinMax implements InputFilter {
        private final int min;
        private final int max;
        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String newVal = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend, dest.length());
                if (newVal.isEmpty()) return null;
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException nfe) {}
            return "";
        }
        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
