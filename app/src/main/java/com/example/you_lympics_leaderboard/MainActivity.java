package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the data manager with context, which will load the data
        playerList = PlayerDataManager.getInstance(this).getPlayerList();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView_leaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerAdapter = new PlayerAdapter(playerList);
        recyclerView.setAdapter(playerAdapter);

        // Setup Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab_to_events);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When we return, the list might have changed, so refresh it
        playerAdapter.notifyDataSetChanged();
    }
}
