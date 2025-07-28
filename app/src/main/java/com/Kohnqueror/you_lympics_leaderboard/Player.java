package com.Kohnqueror.you_lympics_leaderboard;

import com.google.firebase.firestore.Exclude;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private String id;
    private String name;
    private Map<String, String> scores;
    private int totalPoints;
    private String planeSeat;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.scores = new HashMap<>();
        this.planeSeat = "";
        initializeScores();
        this.totalPoints = 0;
    }

    private void initializeScores() {
        // Initializes 8 events to accommodate the twists
        for (int round = 1; round <= 3; round++) {
            for (int event = 1; event <= 8; event++) {
                String key = "round" + round + "_event" + event;
                if (event <= 4) { // Timed events
                    scores.put(key, "0.0");
                } else { // Positional events
                    scores.put(key, "0");
                }
            }
        }
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, String> getScores() { return scores; }
    public void setScores(Map<String, String> scores) { this.scores = scores; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public String getPlaneSeat() { return planeSeat; }
    public void setPlaneSeat(String planeSeat) { this.planeSeat = planeSeat; }

    @Exclude
    public String getScore(int round, int event) {
        String key = "round" + round + "_event" + event;
        return scores.get(key);
    }

    @Exclude
    public void setScore(int round, int event, String score) {
        String key = "round" + round + "_event" + event;
        scores.put(key, score);
    }

    private int timeToMilliseconds(String time) {
        if (time == null || time.isEmpty()) return Integer.MAX_VALUE;
        try {
            double timeInSeconds = Double.parseDouble(time);
            return (int) (timeInSeconds * 1000);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    @Exclude
    public void calculateTotalPoints() {
        int calculatedTotal = 0;
        // Scoring logic only applies to the first 6 events
        for (int event = 1; event <= 6; event++) {
            String r1ScoreStr = getScore(1, event);
            String r2ScoreStr = getScore(2, event);
            String r3ScoreStr = getScore(3, event);

            // Events 1-4 are timed (lower is better)
            if (event <= 4) {
                int r1Time = timeToMilliseconds(r1ScoreStr);
                if (r1Time == 0 || r1Time == Integer.MAX_VALUE) continue;
                int r2Time = timeToMilliseconds(r2ScoreStr);
                int r3Time = timeToMilliseconds(r3ScoreStr);

                if (r2Time != 0 && r2Time != Integer.MAX_VALUE) {
                    if (r2Time < r1Time) calculatedTotal += 2;
                    else if (r2Time == r1Time) calculatedTotal += 1;
                }
                if (r3Time != 0 && r3Time != Integer.MAX_VALUE) {
                    int bestOfR1R2 = r1Time;
                    if (r2Time != 0 && r2Time != Integer.MAX_VALUE) bestOfR1R2 = Math.min(r1Time, r2Time);
                    if (r3Time < bestOfR1R2) calculatedTotal += 2;
                    else if (r3Time == bestOfR1R2) calculatedTotal += 1;
                }
            }
            // Events 5-6 are positional (lower is better)
            else {
                try {
                    int r1Pos = Integer.parseInt(r1ScoreStr);
                    if (r1Pos == 0) continue;
                    int r2Pos = Integer.parseInt(r2ScoreStr);
                    int r3Pos = Integer.parseInt(r3ScoreStr);

                    // Round 2 Scoring
                    if (r2Pos != 0) {
                        if (r2Pos < r1Pos) {
                            calculatedTotal += 2;
                        } else if (r2Pos == r1Pos) {
                            // If matching 1st place, give 2 points, otherwise 1
                            calculatedTotal += (r1Pos == 1) ? 2 : 1;
                        }
                    }

                    // Round 3 Scoring
                    if (r3Pos != 0) {
                        int bestOfR1R2 = r1Pos;
                        if (r2Pos != 0) {
                            bestOfR1R2 = Math.min(r1Pos, r2Pos);
                        }

                        if (r3Pos < bestOfR1R2) {
                            calculatedTotal += 2;
                        } else if (r3Pos == bestOfR1R2) {
                            // If matching the best score of 1st place, give 2 points, otherwise 1
                            calculatedTotal += (bestOfR1R2 == 1) ? 2 : 1;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        this.totalPoints = calculatedTotal;
    }
}
