package com.example.you_lympics_leaderboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single player in the tournament.
 * This class holds the player's name and their scores for all events.
 */
public class Player {

    private String name;
    private Map<String, String> scores;
    private int totalPoints;

    public Player(String name) {
        this.name = name;
        this.scores = new HashMap<>();
        initializeScores();
        this.totalPoints = 0;
    }

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

    public String getScore(int round, int event) {
        String key = "round" + round + "_event" + event;
        return scores.get(key);
    }

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
     * Helper method to convert "MM:SS" time strings into total seconds for comparison.
     * @param time The time string, e.g., "01:30".
     * @return The total number of seconds, or a very large number if the format is invalid.
     */
    private int timeToSeconds(String time) {
        if (time == null || !time.contains(":")) {
            return Integer.MAX_VALUE; // Invalid format, treat as worst possible time
        }
        String[] parts = time.split(":");
        if (parts.length != 2) {
            return Integer.MAX_VALUE;
        }
        try {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60) + seconds;
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // Invalid number, treat as worst possible time
        }
    }

    /**
     * Calculates the total points for the player based on personal improvement
     * across rounds for each event.
     */
    public void calculateTotalPoints() {
        int calculatedTotal = 0;
        // Iterate through each of the 6 events
        for (int event = 1; event <= 6; event++) {
            String r1ScoreStr = getScore(1, event);
            String r2ScoreStr = getScore(2, event);
            String r3ScoreStr = getScore(3, event);

            // Events 1-4 are timed, so a lower score is better.
            if (event <= 4) {
                int r1Time = timeToSeconds(r1ScoreStr);

                // If Round 1 was not played or is invalid, no points can be scored for this event.
                if (r1Time == 0 || r1Time == Integer.MAX_VALUE) {
                    continue;
                }

                int r2Time = timeToSeconds(r2ScoreStr);
                int r3Time = timeToSeconds(r3ScoreStr);

                // --- Round 2 Scoring ---
                // Only score if R2 has a valid, non-default time.
                if (r2Time != 0 && r2Time != Integer.MAX_VALUE) {
                    if (r2Time < r1Time) {
                        calculatedTotal += 2; // Beat previous score
                    } else if (r2Time == r1Time) {
                        calculatedTotal += 1; // Matched previous score
                    }
                }

                // --- Round 3 Scoring ---
                // Only score if R3 has a valid, non-default time.
                if (r3Time != 0 && r3Time != Integer.MAX_VALUE) {
                    int bestOfR1R2 = r1Time;
                    // If R2 was also played, find the best of R1 and R2.
                    if (r2Time != 0 && r2Time != Integer.MAX_VALUE) {
                        bestOfR1R2 = Math.min(r1Time, r2Time);
                    }

                    if (r3Time < bestOfR1R2) {
                        calculatedTotal += 2; // Beat previous best score
                    } else if (r3Time == bestOfR1R2) {
                        calculatedTotal += 1; // Matched previous best score
                    }
                }
            }
            // Events 5-6 are positional, so a lower score is better.
            else {
                try {
                    int r1Pos = Integer.parseInt(r1ScoreStr);

                    // If Round 1 was not played, no points can be scored for this event.
                    if (r1Pos == 0) {
                        continue;
                    }

                    int r2Pos = Integer.parseInt(r2ScoreStr);
                    int r3Pos = Integer.parseInt(r3ScoreStr);

                    // --- Round 2 Scoring ---
                    // Only score if R2 has a non-default score.
                    if (r2Pos != 0) {
                        if (r2Pos < r1Pos) {
                            calculatedTotal += 2;
                        } else if (r2Pos == r1Pos) {
                            calculatedTotal += 1;
                        }
                    }

                    // --- Round 3 Scoring ---
                    // Only score if R3 has a non-default score.
                    if (r3Pos != 0) {
                        int bestOfR1R2 = r1Pos;
                        // If R2 was also played, find the best of R1 and R2.
                        if (r2Pos != 0) {
                            bestOfR1R2 = Math.min(r1Pos, r2Pos);
                        }

                        if (r3Pos < bestOfR1R2) {
                            calculatedTotal += 2;
                        } else if (r3Pos == bestOfR1R2) {
                            calculatedTotal += 1;
                        }
                    }
                } catch (NumberFormatException e) {
                    // If a score is not a valid number (e.g., empty), do nothing.
                }
            }
        }
        this.totalPoints = calculatedTotal;
    }
}
