package com.example.chess3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ButtonViewHolder> {

    private final List<String> buttonTexts;
    private final OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onButtonClick(String text);
    }

    public LevelsAdapter(List<String> buttonTexts, OnButtonClickListener listener) {
        this.buttonTexts = buttonTexts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_item, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String text = buttonTexts.get(position);
        holder.button.setText(text);
        holder.button.setOnClickListener(v -> listener.onButtonClick(text));
    }

    @Override
    public int getItemCount() {
        return buttonTexts.size();
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.lvl_button);
        }
    }
}
