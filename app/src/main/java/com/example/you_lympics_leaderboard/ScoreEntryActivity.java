package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        // Get the event number passed from EventsActivity
        eventNumber = getIntent().getIntExtra("EVENT_NUMBER", 0);

        // Get player data from the manager
        playerList = PlayerDataManager.getInstance().getPlayerList();

        // Setup the title
        TextView titleTextView = findViewById(R.id.textView_event_title);
        titleTextView.setText("Enter Scores for Event " + eventNumber);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView_score_entry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScoreEntryAdapter(playerList, eventNumber);
        recyclerView.setAdapter(adapter);

        // Setup Save Button
        Button saveButton = findViewById(R.id.button_save_scores);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The data is already saved in PlayerDataManager thanks to TextWatcher.
                // We just need to finish the activity to go back.
                finish();
            }
        });
    }
}
