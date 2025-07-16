package com.example.you_lympics_leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Singleton class to manage the list of players throughout the app.
 * This version includes saving and loading data to SharedPreferences.
 */
public class PlayerDataManager {

    private static PlayerDataManager instance;
    private List<Player> playerList;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "YouLympicsPrefs";
    private static final String PLAYERS_KEY = "PlayerList";

    // The list of official player names
    private static final List<String> PLAYER_NAMES = Arrays.asList(
            "Callum", "Carrie", "Charlotte", "Conor", "Dave",
            "Jamie", "Joel", "Oscar", "Peter", "Tim"
    );

    // Private constructor to prevent direct instantiation
    private PlayerDataManager(Context context) {
        loadData(context);
    }

    /**
     * Provides a global access point to the single instance of this class.
     * The context is required for the first initialization to access SharedPreferences.
     * @param context The application context.
     * @return The singleton instance of PlayerDataManager.
     */
    public static synchronized PlayerDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerDataManager(context.getApplicationContext());
        }
        return instance;
    }

    // Overloaded getInstance for convenience when context is not needed after initialization
    public static synchronized PlayerDataManager getInstance() {
        if (instance == null) {
            // This should not happen if getInstance(context) is called first in MainActivity
            throw new IllegalStateException("PlayerDataManager is not initialized, call getInstance(Context) first.");
        }
        return instance;
    }


    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Saves the current list of players to SharedPreferences.
     * @param context The application context.
     */
    public void saveData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        editor.putString(PLAYERS_KEY, json);
        editor.apply();
    }

    /**
     * Loads the list of players from SharedPreferences.
     * If no data is found, it initializes a new list with default names.
     * @param context The application context.
     */
    private void loadData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PLAYERS_KEY, null);

        if (json != null) {
            // If data exists, load it
            Type type = new TypeToken<ArrayList<Player>>() {}.getType();
            playerList = gson.fromJson(json, type);
        } else {
            // If no data exists, create a new list with the specified names
            playerList = new ArrayList<>();
            for (String name : PLAYER_NAMES) {
                playerList.add(new Player(name));
            }
        }
    }

    public void calculateAllPlayerPoints() {
        for (Player player : playerList) {
            player.calculateTotalPoints();
        }
        // Add sorting logic here if needed
    }
}
