package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import java.util.List;

public class ScoreEntryActivity extends AppCompatActivity {

    private int eventNumber;
    private Player selectedPlayer;

    // UI Components
    private AutoCompleteTextView playerAutoComplete;
    private ConstraintLayout scoreFieldsContainer;
    private LinearLayout round2Container, round3Container;
    private EditText round1EditText, round2EditText, round3EditText;
    private CheckBox showRound2CheckBox, showRound3CheckBox;
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
        setupPlayerDropdown();
        setupCheckboxListeners();
        setupVideoButtons();

        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerDataManager.getInstance().saveData(this);
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
        List<String> playerNames = new ArrayList<>();
        for (Player p : players) {
            playerNames.add(p.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, playerNames);
        playerAutoComplete.setAdapter(adapter);

        playerAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedPlayer = players.get(position);
            updateUiForSelectedPlayer();
        });
    }

    private void setupCheckboxListeners() {
        boolean r2Visible = PlayerDataManager.getInstance().isRound2Visible();
        boolean r3Visible = PlayerDataManager.getInstance().isRound3Visible();

        showRound2CheckBox.setChecked(r2Visible);
        showRound3CheckBox.setChecked(r3Visible);
        updateRoundVisibility();

        showRound2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PlayerDataManager.getInstance().setRound2Visible(isChecked);
            updateRoundVisibility();
        });

        showRound3CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PlayerDataManager.getInstance().setRound3Visible(isChecked);
            updateRoundVisibility();
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
            configureEditText(round1EditText, "MM:SS");
            configureEditText(round2EditText, "MM:SS");
            configureEditText(round3EditText, "MM:SS");
        } else { // Positional
            configureEditText(round1EditText, "1-10");
            configureEditText(round2EditText, "1-10");
            configureEditText(round3EditText, "1-10");
        }

        round1EditText.setText(selectedPlayer.getScore(1, eventNumber));
        round2EditText.setText(selectedPlayer.getScore(2, eventNumber));
        round3EditText.setText(selectedPlayer.getScore(3, eventNumber));

        r1Watcher = createTextWatcher(1);
        r2Watcher = createTextWatcher(2);
        r3Watcher = createTextWatcher(3);

        round1EditText.addTextChangedListener(r1Watcher);
        round2EditText.addTextChangedListener(r2Watcher);
        round3EditText.addTextChangedListener(r3Watcher);
    }

    private void updateRoundVisibility() {
        round2Container.setVisibility(showRound2CheckBox.isChecked() ? View.VISIBLE : View.GONE);
        round3Container.setVisibility(showRound3CheckBox.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void configureEditText(EditText editText, String hint) {
        editText.setHint(hint);
        if (eventNumber <= 4) {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
}
