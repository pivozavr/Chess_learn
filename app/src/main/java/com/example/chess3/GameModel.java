package com.example.chess3;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameModel extends AppCompatActivity implements View.OnClickListener {
    GridView boardGV;

    private HashMap<String, Integer> map = new HashMap<>();

    private void initHashMap(){
        map.put("r", R.drawable.black_rook);
        map.put("p", R.drawable.black_pawn);
        map.put("n", R.drawable.black_knight);
        map.put("k", R.drawable.black_king);
        map.put("q", R.drawable.black_queen);
        map.put("b", R.drawable.black_bishop);

        map.put("R", R.drawable.white_rook);
        map.put("P", R.drawable.white_pawn);
        map.put("N", R.drawable.white_knight);
        map.put("K", R.drawable.white_king);
        map.put("Q", R.drawable.white_queen);
        map.put("B", R.drawable.white_bishop);

        map.put("a", 0);

    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateChessBoardCell(int row, int col, int drawableResId) {
        if (adapter != null) {
            adapter.updateCellImage(row, col, drawableResId);
        }
    }

    private void lightUpChessBoardCell(int row, int col) {
        if (adapter != null) {
            adapter.lightUpCell(row, col);
        }
    }
    private void lightChessBoardCell(int row, int col) {
        if (adapter != null) {
            adapter.lightCell(row, col);
        }
    }

    private Square parseRowCol(int row, int col){
        int row2 = 8-row;
        char col2 = (char) (65+col);
        String square = col2+String.valueOf(row2);
        return Square.valueOf(square);
    }
    
    private int getSquareRow(Square square){
        String i = square.value();
        return 8-Integer.parseInt(i.substring(1));
    }

    private int getSquareCol(Square square){
        String i = square.value();
        String c = i.substring(0, 1);
        char[] cc= c.toCharArray();
        return cc[0]-65;
    }


    public void lightPossibleMovesFromSquare(Board board, Square square){
        for (Square squaree: getPossibleMoves(board, square)) {
            lightUpChessBoardCell(getSquareRow(squaree), getSquareCol(squaree));
        }
        lightChessBoardCell(getSquareRow(square), getSquareCol(square));
    }

    private void drawBoard(String fen){
        fen = fen.substring(0, fen.indexOf(' '));
        String[] fenn = fen.split("/");
        String[][] fennn = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(isNumeric(fenn[i].substring(j, j+1))){

                    for (int k = 0; k < Integer.parseInt(fenn[i]); k++) {
                        fennn[i][j] = "a";
                        j++;
                    }
                }
                else {
                    fennn[i][j] = fenn[i].substring(j, j+1);
                }

            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int resId = map.get(fennn[i][j]);
                updateChessBoardCell(i,j,resId);


            }
        }

    }

    private List<Square> getPossibleMoves(Board board, Square square){
        // Получаем все возможные ходы на доске
        List<Move> legalMoves = board.legalMoves();

        // Фильтруем ходы по клетке fromSquare
        List<Square> movesFromSquare = new ArrayList<>();
        for (Move move : legalMoves) {
            if (move.getFrom().equals(square)) {
                movesFromSquare.add(move.getTo());
            }
        }
        return  movesFromSquare;

    }
    Board boards = new Board();
    private ChessBoardAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        initHashMap();

        boardGV = findViewById(R.id.chessBoardGridView);
        adapter = new ChessBoardAdapter(this);
        boardGV.setAdapter(adapter);
        boardGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    adapter.clearBoard();
                    int row = adapter.getItemRow(i);
                    int col = adapter.getItemCol(i);
                    lightPossibleMovesFromSquare(boards, parseRowCol(row, col));
                    Log.d("Artemp", " "+getSquareRow(Square.A8));
                    Log.d("Artemp", " "+getSquareCol(Square.A8));
                } catch (Exception e) {
                    Log.e("Artemp", e.toString());
                    // Дополнительная обработка, если необходимо
                }

            }
        });

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1";
        drawBoard(fen);

        boards.loadFromFen(fen);



    }



    @Override
    public void onClick(View view) {
        Log.e("ART", view.toString());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

            }
        }
   }
}


