package com.example.you_lympics_leaderboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private Context context;
    private int rankOffset; // To start ranking from a specific number (e.g., 4)

    public PlayerAdapter(List<Player> playerList, Context context, int rankOffset) {
        this.playerList = new ArrayList<>(playerList); // Use a mutable copy
        this.context = context;
        this.rankOffset = rankOffset;
    }

    /**
     * Efficiently updates the data in the adapter using DiffUtil.
     * This enables animations for item changes.
     * @param newPlayers The new list of players to display.
     */
    public void submitList(List<Player> newPlayers) {
        PlayerDiffCallback diffCallback = new PlayerDiffCallback(this.playerList, newPlayers);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.playerList.clear();
        this.playerList.addAll(newPlayers);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);

        int rank = position + rankOffset;
        holder.rankTextView.setText(rank + getOrdinalSuffix(rank));
        holder.playerNameTextView.setText(player.getName());
        holder.totalPointsTextView.setText(player.getTotalPoints() + " pts");

        // Load the avatar
        if (context != null) {
            String avatarFileName = player.getName().toLowerCase() + "px";
            int avatarResId = context.getResources().getIdentifier(avatarFileName, "drawable", context.getPackageName());
            if (avatarResId != 0) {
                holder.avatarImageView.setImageResource(avatarResId);
            } else {
                // Set a default avatar if the specific one isn't found
                holder.avatarImageView.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }

        if (context != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayerStatsActivity.class);
                intent.putExtra("PLAYER_ID", player.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    private String getOrdinalSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        ImageView avatarImageView;
        TextView playerNameTextView;
        TextView totalPointsTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.textView_rank);
            avatarImageView = itemView.findViewById(R.id.imageView_avatar);
            playerNameTextView = itemView.findViewById(R.id.textView_playerName);
            totalPointsTextView = itemView.findViewById(R.id.textView_totalPoints);
        }
    }
}
