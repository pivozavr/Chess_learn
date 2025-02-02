package com.example.chess3;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private Board board = new Board();
    private String fen;


    public GameModel(){
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1");
        fen = board.getFen();
    }

    public GameModel(String f){
        board.loadFromFen(f);
        fen = board.getFen();
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }



    private static String replaceDigitsWithA(String input) {
        StringBuilder result = new StringBuilder();

        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch)) {
                int count = Character.getNumericValue(ch);
                result.append("a".repeat(count)); // Добавляем 'a' count раз
            } else {
                result.append(ch); // Оставляем буквы как есть
            }
        }

        return result.toString();
    }

    public void loadFromMovesList(List<Move> moves){
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1");
        for (Move move:
             moves) {
            board.doMove(move);
        }
        fen = board.getFen();
    }

    public String[][] getBoardArray() {
        fen = board.getFen();
        fen = fen.substring(0, fen.indexOf(' '));
        fen = replaceDigitsWithA(fen);
        String[] fenn = fen.split("/");
        String[][] fennn = new String[8][8];


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fennn[i][j] = fenn[i].substring(j, j + 1);
            }
        }
        return fennn;
    }

    public boolean isPromotionRequired() {
        fen = board.getFen();
        fen = replaceDigitsWithA(fen);
        fen = fen.substring(0, fen.indexOf(' '));
        String black = fen.split("/")[0];
        String white = fen.split("/")[7];
        boolean res = black.contains("P");

        if (white.contains("p"))
            res = true;

        return res;
    }

    public List<Square> getPossibleMoves(Square square) {
        List<Move> legalMoves = board.legalMoves();
        List<Square> movesFromSquare = new ArrayList<>();
        for (Move move : legalMoves) {
            if (move.getFrom().equals(square)) {
                movesFromSquare.add(move.getTo());
            }
        }
        return movesFromSquare;
    }

}
