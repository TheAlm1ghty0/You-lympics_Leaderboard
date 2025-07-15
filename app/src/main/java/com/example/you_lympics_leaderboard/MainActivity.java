package com.example.you_lympics_leaderboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView_leaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Prepare the player data
        initializePlayerData();

        // 3. Create and set the adapter
        playerAdapter = new PlayerAdapter(playerList);
        recyclerView.setAdapter(playerAdapter);
    }

    private void initializePlayerData() {
        playerList = new ArrayList<>();
        // Create 10 default players
        for (int i = 1; i <= 10; i++) {
            Player player = new Player("Player " + i);
            // Example: Set some dummy points for demonstration
            // In the real app, this will be calculated
            player.setTotalPoints((11 - i) * 10); // Player 1 gets 100, Player 2 gets 90, etc.
            playerList.add(player);
        }
    }
}
