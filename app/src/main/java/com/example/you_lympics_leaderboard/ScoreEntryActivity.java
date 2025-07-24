package com.example.you_lympics_leaderboard;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScoreEntryActivity extends AppCompatActivity {

    private int eventNumber;
    private Player selectedPlayer;

    private AutoCompleteTextView playerAutoComplete;
    private ConstraintLayout scoreFieldsContainer;
    private LinearLayout round2Container, round3Container;
    private EditText round1EditText, round2EditText, round3EditText;
    private CheckBox showRound2CheckBox, showRound3CheckBox;
    private Button video1Button, video2Button;

    private TextWatcher r1Watcher, r2Watcher, r3Watcher;

    private final List<String> eventNames = Arrays.asList(
            "Strawpedo", "Race 2 Pint", "Crab Run",
            "Ping Pong Run", "Elimination Slaps", "Elimination Stacks",
            "Twist 1", "Twist 2"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        eventNumber = getIntent().getIntExtra("EVENT_NUMBER", 0);

        findViews();
        setupPlayerDropdown();
        setupCheckboxListeners();
        setupVideoButtons();

        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(v -> {
            if (selectedPlayer != null) {
                PlayerDataManager.getInstance().updatePlayer(selectedPlayer);
                Toast.makeText(this, "Saving scores for " + selectedPlayer.getName(), Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectedPlayer != null) {
            PlayerDataManager.getInstance().updatePlayer(selectedPlayer);
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
        scoreFieldsContainer = findViewById(R.id.score_fields_container);
        round2Container = findViewById(R.id.container_round2);
        round3Container = findViewById(R.id.container_round3);
        round1EditText = findViewById(R.id.editText_round1_score);
        round2EditText = findViewById(R.id.editText_round2_score);
        round3EditText = findViewById(R.id.editText_round3_score);
        showRound2CheckBox = findViewById(R.id.checkbox_show_round2);
        showRound3CheckBox = findViewById(R.id.checkbox_show_round3);
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


    private void setupPlayerDropdown() {
        List<Player> players = PlayerDataManager.getInstance().getPlayerList();
        Collections.sort(players, (p1, p2) -> p1.getName().compareTo(p2.getName()));

        List<String> playerNames = new ArrayList<>();
        for (Player p : players) {
            playerNames.add(p.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, playerNames);
        playerAutoComplete.setAdapter(adapter);

        playerAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            if (selectedPlayer != null) {
                PlayerDataManager.getInstance().updatePlayer(selectedPlayer);
            }
            selectedPlayer = players.get(position);
            updateUiForSelectedPlayer();
        });
    }

    private void setupCheckboxListeners() {
        SharedPreferences prefs = getSharedPreferences("YouLympicsUIPrefs", MODE_PRIVATE);
        showRound2CheckBox.setChecked(prefs.getBoolean("round2_visible", false));
        showRound3CheckBox.setChecked(prefs.getBoolean("round3_visible", false));
        updateRoundVisibility();

        showRound2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                showRound3CheckBox.setChecked(false);
            }
            saveCheckboxState();
            updateRoundVisibility();
        });

        showRound3CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showRound2CheckBox.setChecked(true);
            }
            saveCheckboxState();
            updateRoundVisibility();
        });
    }

    private void saveCheckboxState() {
        SharedPreferences prefs = getSharedPreferences("YouLympicsUIPrefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("round2_visible", showRound2CheckBox.isChecked())
                .putBoolean("round3_visible", showRound3CheckBox.isChecked())
                .apply();
    }

    private void updateUiForSelectedPlayer() {
        if (selectedPlayer == null) return;

        // For Twist events, only show video buttons, not score entry
        if (eventNumber > 6) {
            scoreFieldsContainer.setVisibility(View.GONE);
        } else {
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
    }

    private void updateRoundVisibility() {
        boolean r2Checked = showRound2CheckBox.isChecked();
        boolean r3Checked = showRound3CheckBox.isChecked();

        round2Container.setVisibility(r2Checked ? View.VISIBLE : View.GONE);
        round3Container.setVisibility(r2Checked && r3Checked ? View.VISIBLE : View.GONE);
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
                if (newVal.isEmpty()) {
                    return null;
                }
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input)) {
                    return null;
                }
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
