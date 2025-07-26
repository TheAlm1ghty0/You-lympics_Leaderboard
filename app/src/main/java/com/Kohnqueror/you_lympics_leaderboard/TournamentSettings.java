package com.Kohnqueror.you_lympics_leaderboard;

public class TournamentSettings {
    private int currentRound = 1; // Can be 1, 2, or 3

    public TournamentSettings() {}

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    // These methods are needed for the Stopwatch app
    public boolean isRound2Locked() {
        return currentRound < 2;
    }

    public boolean isRound3Locked() {
        return currentRound < 3;
    }
}
