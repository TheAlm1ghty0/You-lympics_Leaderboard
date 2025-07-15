package com.example.you_lympics_leaderboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single player in the tournament.
 * This class holds the player's name and their scores for all events.
 */
public class Player {

    private String name;
    // Using a Map to store scores. The key will be a unique identifier for the event,
    // e.g., "round1_event1", "round2_event4".
    private Map<String, String> scores;
    private int totalPoints;

    /**
     * Constructor for a new Player.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.scores = new HashMap<>();
        initializeScores();
        this.totalPoints = 0;
    }

    /**
     * Initializes the scores for all events to default values.
     * Timed events default to "00:00".
     * Positional events default to "0".
     */
    private void initializeScores() {
        for (int round = 1; round <= 3; round++) {
            for (int event = 1; event <= 6; event++) {
                String key = "round" + round + "_event" + event;
                if (event <= 4) { // Timed events
                    scores.put(key, "00:00");
                } else { // Positional events
                    scores.put(key, "0");
                }
            }
        }
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getScores() {
        return scores;
    }

    /**
     * Gets a specific score for a given round and event.
     * @param round The round number (1-3).
     * @param event The event number (1-6).
     * @return The score as a String.
     */
    public String getScore(int round, int event) {
        String key = "round" + round + "_event" + event;
        return scores.get(key);
    }

    /**
     * Sets a specific score for a given round and event.
     * @param round The round number (1-3).
     * @param event The event number (1-6).
     * @param score The score as a String (e.g., "01:30" or "8").
     */
    public void setScore(int round, int event, String score) {
        String key = "round" + round + "_event" + event;
        scores.put(key, score);
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Calculates the total points for the player based on all event scores.
     * This method needs to be implemented based on the scoring logic
     * for converting times and positions into points.
     */
    public void calculateTotalPoints() {
        // This is a placeholder. The actual calculation logic will be complex
        // and will depend on how times are converted to points.
        // For now, we'll just sum the positional scores as a simple example.
        int calculatedPoints = 0;
        for (int round = 1; round <= 3; round++) {
            for (int event = 5; event <= 6; event++) { // Only positional events
                String key = "round" + round + "_event" + event;
                calculatedPoints += Integer.parseInt(scores.get(key));
            }
        }
        this.totalPoints = calculatedPoints;
    }
}
