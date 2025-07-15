package com.example.you_lympics_leaderboard;

import java.util.ArrayList;
import java.util.List;

/**
 * A Singleton class to manage the list of players throughout the app.
 * This ensures that all activities are working with the same data set.
 */
public class PlayerDataManager {

    private static PlayerDataManager instance;
    private List<Player> playerList;

    // Private constructor to prevent instantiation from other classes
    private PlayerDataManager() {
        playerList = new ArrayList<>();
        // Create 10 default players
        for (int i = 1; i <= 10; i++) {
            playerList.add(new Player("Player " + i));
        }
    }

    /**
     * Provides a global access point to the single instance of this class.
     * @return The singleton instance of PlayerDataManager.
     */
    public static synchronized PlayerDataManager getInstance() {
        if (instance == null) {
            instance = new PlayerDataManager();
        }
        return instance;
    }

    /**
     * Gets the list of all players.
     * @return The list of players.
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Gets a specific player by their position in the list.
     * @param position The index of the player.
     * @return The Player object.
     */
    public Player getPlayer(int position) {
        if (position >= 0 && position < playerList.size()) {
            return playerList.get(position);
        }
        return null;
    }

    /**
     * This method will be used later to calculate all player points.
     * For now, it's a placeholder.
     */
    public void calculateAllPlayerPoints() {
        for (Player player : playerList) {
            // TODO: Implement the logic to convert time and position scores into points.
            // This will be a complex but important part of the app.
            // For now, we just call the placeholder method.
            player.calculateTotalPoints();
        }
        // After calculating, you might want to sort the list
        // playerList.sort(...);
    }
}
