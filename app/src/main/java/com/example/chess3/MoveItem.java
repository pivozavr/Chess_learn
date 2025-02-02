package com.example.chess3;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.List;

public class MoveItem {
    private String pos;
    private Move move1;
    private Move move2;
    private List<Move> moveList;

    public MoveItem(String pos, Move move1, Move move2, List<Move> moveList){
        this.pos=pos;
        this.move1=move1;
        this.move2=move2;
        this.moveList=moveList;
    }


    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Move getMove1() {
        return move1;
    }

    public void setMove1(Move move1) {
        this.move1 = move1;
    }

    public Move getMove2() {
        return move2;
    }

    public void setMove2(Move move2) {
        this.move2 = move2;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public void setMoveList(List<Move> moveList) {
        this.moveList = moveList;
    }
}
