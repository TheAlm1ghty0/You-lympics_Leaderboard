package com.Kohnqueror.you_lympics_leaderboard;

public class TournamentSettings {
    private int currentRound = 1;
    private String lastPlacePlayerName = ""; // New field to store the loser's name

    public TournamentSettings() {}

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getLastPlacePlayerName() {
        return lastPlacePlayerName;
    }

    public void setLastPlacePlayerName(String lastPlacePlayerName) {
        this.lastPlacePlayerName = lastPlacePlayerName;
    }

    // These methods are needed for the Stopwatch app
    public boolean isRound2Locked() {
        return currentRound < 2;
    }

    public boolean isRound3Locked() {
        return currentRound < 3;
    }
}
