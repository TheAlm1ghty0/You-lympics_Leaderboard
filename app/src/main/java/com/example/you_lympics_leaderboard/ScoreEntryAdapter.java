package com.example.you_lympics_leaderboard;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreEntryAdapter extends RecyclerView.Adapter<ScoreEntryAdapter.ScoreEntryViewHolder> {

    private List<Player> playerList;
    private int eventNumber;

    public ScoreEntryAdapter(List<Player> playerList, int eventNumber) {
        this.playerList = playerList;
        this.eventNumber = eventNumber;
    }

    @NonNull
    @Override
    public ScoreEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_entry_item, parent, false);
        return new ScoreEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreEntryViewHolder holder, int position) {
        // We pass the player object to the holder's bind method.
        // The holder is now responsible for managing its own listeners.
        holder.bind(playerList.get(position), eventNumber);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    /**
     * The ViewHolder now contains the TextWatchers. This is crucial for correctly
     * adding and removing them as views are recycled.
     */
    static class ScoreEntryViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        EditText round1ScoreEditText, round2ScoreEditText, round3ScoreEditText;

        // Store the watchers to remove them later
        private TextWatcher round1Watcher, round2Watcher, round3Watcher;

        public ScoreEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            // These findViewById calls link the layout's views to our Java objects.
            // An error here would mean the IDs in score_entry_item.xml are incorrect.
            playerNameTextView = itemView.findViewById(R.id.textView_playerName_entry);
            round1ScoreEditText = itemView.findViewById(R.id.editText_round1_score);
            round2ScoreEditText = itemView.findViewById(R.id.editText_round2_score);
            round3ScoreEditText = itemView.findViewById(R.id.editText_round3_score);
        }

        /**
         * Binds a player's data to the view and correctly manages TextWatchers.
         */
        public void bind(final Player player, final int eventNumber) {
            playerNameTextView.setText(player.getName());

            // --- THIS IS THE CRITICAL FIX ---
            // 1. Remove any existing listeners from the recycled view before using it.
            if (round1Watcher != null) round1ScoreEditText.removeTextChangedListener(round1Watcher);
            if (round2Watcher != null) round2ScoreEditText.removeTextChangedListener(round2Watcher);
            if (round3Watcher != null) round3ScoreEditText.removeTextChangedListener(round3Watcher);

            // 2. Configure the input fields based on the event type
            if (eventNumber <= 4) { // Timed event
                configureEditText(round1ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
                configureEditText(round2ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
                configureEditText(round3ScoreEditText, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME, "MM:SS");
            } else { // Positional event
                configureEditText(round1ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
                configureEditText(round2ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
                configureEditText(round3ScoreEditText, InputType.TYPE_CLASS_NUMBER, "1-10");
            }

            // 3. Set the text *after* removing the old listeners.
            round1ScoreEditText.setText(player.getScore(1, eventNumber));
            round2ScoreEditText.setText(player.getScore(2, eventNumber));
            round3ScoreEditText.setText(player.getScore(3, eventNumber));

            // 4. Create and add the new, correct listeners.
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
