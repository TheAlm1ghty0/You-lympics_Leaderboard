package com.example.you_lympics_leaderboard;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreEntryActivity extends AppCompatActivity {

    private List<Player> playerList;
    private int eventNumber;
    private ScoreEntryAdapter adapterLeft, adapterRight;

    // Flags to prevent infinite scroll loops
    private boolean isScrollingLeft = false;
    private boolean isScrollingRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        eventNumber = getIntent().getIntExtra("EVENT_NUMBER", 0);
        playerList = PlayerDataManager.getInstance().getPlayerList();

        TextView titleTextView = findViewById(R.id.textView_event_title);
        titleTextView.setText("Enter Scores for Event " + eventNumber);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLandscapeLayout();
        } else {
            setupPortraitLayout();
        }

        CheckBox showRound2CheckBox = findViewById(R.id.checkbox_show_round2);
        showRound2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateRoundVisibility(2, isChecked));

        CheckBox showRound3CheckBox = findViewById(R.id.checkbox_show_round3);
        showRound3CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateRoundVisibility(3, isChecked));

        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(v -> finish());
    }

    private void setupPortraitLayout() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_score_entry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterLeft = new ScoreEntryAdapter(playerList, eventNumber);
        recyclerView.setAdapter(adapterLeft);
    }

    private void setupLandscapeLayout() {
        final RecyclerView recyclerLeft = findViewById(R.id.recyclerView_score_entry_left);
        final RecyclerView recyclerRight = findViewById(R.id.recyclerView_score_entry_right);

        recyclerLeft.setLayoutManager(new LinearLayoutManager(this));
        recyclerRight.setLayoutManager(new LinearLayoutManager(this));

        int midpoint = playerList.size() / 2;
        adapterLeft = new ScoreEntryAdapter(playerList.subList(0, midpoint), eventNumber);
        adapterRight = new ScoreEntryAdapter(playerList.subList(midpoint, playerList.size()), eventNumber);

        recyclerLeft.setAdapter(adapterLeft);
        recyclerRight.setAdapter(adapterRight);

        // --- Synchronized Scrolling Logic ---
        final RecyclerView.OnScrollListener leftScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isScrollingRight) {
                    isScrollingLeft = true;
                    recyclerRight.scrollBy(dx, dy);
                }
                isScrollingLeft = false;
            }
        };

        final RecyclerView.OnScrollListener rightScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isScrollingLeft) {
                    isScrollingRight = true;
                    recyclerLeft.scrollBy(dx, dy);
                }
                isScrollingRight = false;
            }
        };

        recyclerLeft.addOnScrollListener(leftScrollListener);
        recyclerRight.addOnScrollListener(rightScrollListener);
    }

    private void updateRoundVisibility(int round, boolean isVisible) {
        if (adapterLeft != null) {
            if (round == 2) adapterLeft.setRound2Visibility(isVisible);
            else if (round == 3) adapterLeft.setRound3Visibility(isVisible);
        }
        if (adapterRight != null) {
            if (round == 2) adapterRight.setRound2Visibility(isVisible);
            else if (round == 3) adapterRight.setRound3Visibility(isVisible);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerDataManager.getInstance().saveData(this);
    }
}
