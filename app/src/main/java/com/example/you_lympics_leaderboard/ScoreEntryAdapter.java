package com.example.you_lympics_leaderboard;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreEntryAdapter extends RecyclerView.Adapter<ScoreEntryAdapter.ScoreEntryViewHolder> {

    private List<Player> playerList;
    private int eventNumber;
    // Variables to hold the visibility state
    private boolean isRound2Visible = false;
    private boolean isRound3Visible = false;

    public ScoreEntryAdapter(List<Player> playerList, int eventNumber) {
        this.playerList = playerList;
        this.eventNumber = eventNumber;
    }

    // Methods to update visibility from the Activity
    public void setRound2Visibility(boolean isVisible) {
        isRound2Visible = isVisible;
        notifyDataSetChanged(); // Redraw the whole list
    }

    public void setRound3Visibility(boolean isVisible) {
        isRound3Visible = isVisible;
        notifyDataSetChanged(); // Redraw the whole list
    }


    @NonNull
    @Override
    public ScoreEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_entry_item, parent, false);
        return new ScoreEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreEntryViewHolder holder, int position) {
        holder.bind(playerList.get(position), eventNumber, isRound2Visible, isRound3Visible);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class ScoreEntryViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        EditText round1ScoreEditText, round2ScoreEditText, round3ScoreEditText;
        // Add references to the containers
        LinearLayout round2Container, round3Container;

        private TextWatcher round1Watcher, round2Watcher, round3Watcher;

        public ScoreEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.textView_playerName_entry);
            round1ScoreEditText = itemView.findViewById(R.id.editText_round1_score);
            round2ScoreEditText = itemView.findViewById(R.id.editText_round2_score);
            round3ScoreEditText = itemView.findViewById(R.id.editText_round3_score);
            // Link the container views
            round2Container = itemView.findViewById(R.id.container_round2);
            round3Container = itemView.findViewById(R.id.container_round3);
        }

        public void bind(final Player player, final int eventNumber, boolean isR2Visible, boolean isR3Visible) {
            playerNameTextView.setText(player.getName());

            // Set visibility of the containers
            round2Container.setVisibility(isR2Visible ? View.VISIBLE : View.GONE);
            round3Container.setVisibility(isR3Visible ? View.VISIBLE : View.GONE);

            // The rest of the bind logic remains the same...
            if (round1Watcher != null) round1ScoreEditText.removeTextChangedListener(round1Watcher);
            if (round2Watcher != null) round2ScoreEditText.removeTextChangedListener(round2Watcher);
            if (round3Watcher != null) round3ScoreEditText.removeTextChangedListener(round3Watcher);

            if (eventNumber <= 4) {
                configureEditText(round1ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
                configureEditText(round2ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
                configureEditText(round3ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
            } else {
                configureEditText(round1ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
                configureEditText(round2ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
                configureEditText(round3ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
            }

            round1ScoreEditText.setText(player.getScore(1, eventNumber));
            round2ScoreEditText.setText(player.getScore(2, eventNumber));
            round3ScoreEditText.setText(player.getScore(3, eventNumber));

            round1Watcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { player.setScore(1, eventNumber, s.toString()); }
            };
            round2Watcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { player.setScore(2, eventNumber, s.toString()); }
            };
            round3Watcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { player.setScore(3, eventNumber, s.toString()); }
            };

            round1ScoreEditText.addTextChangedListener(round1Watcher);
            round2ScoreEditText.addTextChangedListener(round2Watcher);
            round3ScoreEditText.addTextChangedListener(round3Watcher);
        }

        private void configureEditText(EditText editText, int inputType, String hint) {
            editText.setInputType(inputType);
            editText.setHint(hint);
        }
    }
}
