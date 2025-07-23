package com.example.you_lympics_leaderboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private Context context;
    private int rankOffset; // To start ranking from a specific number (e.g., 4)

    public PlayerAdapter(List<Player> playerList, Context context, int rankOffset) {
        this.playerList = playerList;
        this.context = context;
        this.rankOffset = rankOffset;
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
        // Use the offset to display the correct rank
        holder.rankTextView.setText(String.valueOf(position + rankOffset));
        holder.playerNameTextView.setText(player.getName());
        holder.totalPointsTextView.setText(String.valueOf(player.getTotalPoints()));

        if (context != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayerStatsActivity.class);
                int originalPosition = PlayerDataManager.getInstance().getPlayerList().indexOf(player);
                intent.putExtra("PLAYER_POSITION", originalPosition);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView playerNameTextView;
        TextView totalPointsTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.textView_rank);
            playerNameTextView = itemView.findViewById(R.id.textView_playerName);
            totalPointsTextView = itemView.findViewById(R.id.textView_totalPoints);
        }
    }
}
