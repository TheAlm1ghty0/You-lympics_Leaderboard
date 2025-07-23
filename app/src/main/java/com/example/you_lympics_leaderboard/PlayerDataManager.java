package com.example.you_lympics_leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDataManager {

    private static PlayerDataManager instance;
    private List<Player> playerList;

    // New fields to store checkbox visibility
    private boolean round2Visible = false;
    private boolean round3Visible = false;

    private static final String PREFS_NAME = "YouLympicsPrefs";
    private static final String PLAYERS_KEY = "PlayerList";
    private static final String R2_VISIBLE_KEY = "Round2Visible";
    private static final String R3_VISIBLE_KEY = "Round3Visible";


    private static final List<String> PLAYER_NAMES = Arrays.asList(
            "Callum", "Carrie", "Charlotte", "Conor", "Dave",
            "Jamie", "Joel", "Oscar", "Peter", "Tim"
    );

    private PlayerDataManager(Context context) {
        loadData(context);
    }

    public static synchronized PlayerDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerDataManager(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized PlayerDataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PlayerDataManager is not initialized, call getInstance(Context) first.");
        }
        return instance;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Player getPlayer(int position) {
        if (position >= 0 && position < playerList.size()) {
            return playerList.get(position);
        }
        return null;
    }

    // --- Getters and Setters for Visibility ---
    public boolean isRound2Visible() {
        return round2Visible;
    }

    public void setRound2Visible(boolean round2Visible) {
        this.round2Visible = round2Visible;
    }

    public boolean isRound3Visible() {
        return round3Visible;
    }

    public void setRound3Visible(boolean round3Visible) {
        this.round3Visible = round3Visible;
    }


    public void saveData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        editor.putString(PLAYERS_KEY, json);

        // Save the visibility states
        editor.putBoolean(R2_VISIBLE_KEY, round2Visible);
        editor.putBoolean(R3_VISIBLE_KEY, round3Visible);

        editor.apply();
    }

    private void loadData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PLAYERS_KEY, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Player>>() {}.getType();
            playerList = gson.fromJson(json, type);
            // Load visibility states, defaulting to false if not found
            round2Visible = prefs.getBoolean(R2_VISIBLE_KEY, false);
            round3Visible = prefs.getBoolean(R3_VISIBLE_KEY, false);
        } else {
            playerList = new ArrayList<>();
            for (String name : PLAYER_NAMES) {
                playerList.add(new Player(name));
            }
            // Ensure visibility is false on first launch
            round2Visible = false;
            round3Visible = false;
        }
    }

    public void resetAllData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        loadData(context);
    }

    public void calculateAllPlayerPoints() {
        for (Player player : playerList) {
            player.calculateTotalPoints();
        }
    }
}
