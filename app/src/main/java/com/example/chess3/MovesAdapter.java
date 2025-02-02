package com.example.chess3;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.List;


public class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<MoveItem> moves;
    private OnClickListener onClickListener;

    MovesAdapter(Context context, List<MoveItem> moves) {
        this.inflater = LayoutInflater.from(context);
        this.moves = moves;
    }
    @Override
    public MovesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_move, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovesAdapter.ViewHolder holder, int position) {
        MoveItem moveItem = moves.get(position);
        holder.moveNumber.setText(moveItem.getPos());
        holder.move1.setText(moveItem.getMove1().toString());
        if(moveItem.getMove2()!=null)
            holder.move2.setText(moveItem.getMove2().toString());
        else{
            holder.move2.setClickable(false);
            holder.move2.setVisibility(View.INVISIBLE);
        }
        holder.move1.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(1, moveItem.getMoveList());
            }
        });
        holder.move2.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(2, moveItem.getMoveList());
            }
        });
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, List<Move> move);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final Button move1, move2;
        final TextView moveNumber;
        ViewHolder(View view){
            super(view);
            move1 = view.findViewById(R.id.move1);
            move2 = view.findViewById(R.id.move2);
            moveNumber = view.findViewById(R.id.moveNumber);
        }
    }
}