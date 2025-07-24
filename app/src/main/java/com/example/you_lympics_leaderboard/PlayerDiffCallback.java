package com.example.you_lympics_leaderboard;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import java.util.List;
import java.util.Objects;

public class PlayerDiffCallback extends DiffUtil.Callback {

    private final List<Player> oldList;
    private final List<Player> newList;

    public PlayerDiffCallback(List<Player> oldList, List<Player> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Items are the same if their unique ID is the same
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Contents are the same if the points and name haven't changed
        Player oldPlayer = oldList.get(oldItemPosition);
        Player newPlayer = newList.get(newItemPosition);
        return oldPlayer.getTotalPoints() == newPlayer.getTotalPoints() &&
                oldPlayer.getName().equals(newPlayer.getName());
    }
}
