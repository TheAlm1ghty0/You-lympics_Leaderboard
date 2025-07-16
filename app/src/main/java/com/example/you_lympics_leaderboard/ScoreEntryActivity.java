package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreEntryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreEntryAdapter adapter;
    private List<Player> playerList;
    private int eventNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        eventNumber = getIntent().getIntExtra("EVENT_NUMBER", 0);
        playerList = PlayerDataManager.getInstance().getPlayerList();

        TextView titleTextView = findViewById(R.id.textView_event_title);
        titleTextView.setText("Enter Scores for Event " + eventNumber);

        // Setup RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView_score_entry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScoreEntryAdapter(playerList, eventNumber);
        recyclerView.setAdapter(adapter);

        // Setup CheckBox Listeners
        CheckBox showRound2CheckBox = findViewById(R.id.checkbox_show_round2);
        showRound2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setRound2Visibility(isChecked);
            }
        });

        CheckBox showRound3CheckBox = findViewById(R.id.checkbox_show_round3);
        showRound3CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setRound3Visibility(isChecked);
            }
        });

        // Setup Save Button
        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerDataManager.getInstance().saveData(this);
    }
}
