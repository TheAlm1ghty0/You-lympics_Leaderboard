package com.example.you_lympics_leaderboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerList = PlayerDataManager.getInstance(this).getPlayerList();

        FloatingActionButton fab = findViewById(R.id.fab_to_events);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EventsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recalculate points and sort the list every time we return
        PlayerDataManager.getInstance().calculateAllPlayerPoints();
        Collections.sort(playerList, (p1, p2) -> Integer.compare(p2.getTotalPoints(), p1.getTotalPoints()));

        // Check the orientation and setup the UI accordingly
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLandscapeView();
        } else {
            setupPortraitView();
        }
    }

    private void setupPortraitView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_leaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlayerAdapter playerAdapter = new PlayerAdapter(playerList);
        recyclerView.setAdapter(playerAdapter);
    }

    private void setupLandscapeView() {
        if (playerList.size() >= 3) {
            // Populate the podium
            populatePodium(playerList.get(0), R.id.container_1st_place, R.id.textView_1st_name, R.id.textView_1st_points);
            populatePodium(playerList.get(1), R.id.container_2nd_place, R.id.textView_2nd_name, R.id.textView_2nd_points);
            populatePodium(playerList.get(2), R.id.container_3rd_place, R.id.textView_3rd_name, R.id.textView_3rd_points);

            // Populate the rest of the list
            RecyclerView recyclerViewRest = findViewById(R.id.recyclerView_leaderboard_rest);
            recyclerViewRest.setLayoutManager(new LinearLayoutManager(this));
            PlayerAdapter adapterRest = new PlayerAdapter(playerList.subList(3, playerList.size()));
            recyclerViewRest.setAdapter(adapterRest);
        } else {
            // Handle case with fewer than 3 players if necessary
        }
    }

    private void populatePodium(Player player, int containerId, int nameId, int pointsId) {
        LinearLayout container = findViewById(containerId);
        TextView nameTextView = findViewById(nameId);
        TextView pointsTextView = findViewById(pointsId);

        nameTextView.setText(player.getName());
        pointsTextView.setText(String.valueOf(player.getTotalPoints()));
    }
}
