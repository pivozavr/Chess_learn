package com.example.chess3;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class CoolerSquare {
    Square square;

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }


    public CoolerSquare(Square sq){
        square=sq;
    }

    public void parseRowCol(int row, int col) {
        int row2 = 8 - row;
        char col2 = (char) (65 + col);
        String squareString = col2 + String.valueOf(row2);
        square =  Square.valueOf(squareString);
    }

    public int getSquareRow() {
        String i = square.value();
        return 8 - Integer.parseInt(i.substring(1));
    }

    public int getSquareCol() {
        String i = square.value();
        String c = i.substring(0, 1);
        char[] cc = c.toCharArray();
        return cc[0] - 65;
    }
}
