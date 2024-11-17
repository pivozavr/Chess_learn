package com.example.chess3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameModel extends AppCompatActivity{
    GridView boardGV;

    private final HashMap<String, Integer> map = new HashMap<>();

    private void initHashMap() {
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

    private Square parseRowCol(int row, int col) {
        int row2 = 8 - row;
        char col2 = (char) (65 + col);
        String square = col2 + String.valueOf(row2);
        return Square.valueOf(square);
    }

    private int getSquareRow(Square square) {
        String i = square.value();
        return 8 - Integer.parseInt(i.substring(1));
    }

    private int getSquareCol(Square square) {
        String i = square.value();
        String c = i.substring(0, 1);
        char[] cc = c.toCharArray();
        return cc[0] - 65;
    }


    public void lightPossibleMovesFromSquare(Board board, Square square) {
        for (Square squaree : getPossibleMoves(board, square)) {
            lightUpChessBoardCell(getSquareRow(squaree), getSquareCol(squaree));
        }
        lightChessBoardCell(getSquareRow(square), getSquareCol(square));
    }

    public static String replaceDigitsWithA(String input) {
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


    private void drawBoard(String fen) {
        fen = fen.substring(0, fen.indexOf(' '));
        fen = replaceDigitsWithA(fen);
        String[] fenn = fen.split("/");
        String[][] fennn = new String[8][8];


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fennn[i][j] = fenn[i].substring(j, j + 1);
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int resId = map.get(fennn[i][j]);
                updateChessBoardCell(i, j, resId);
            }
        }

    }

    private List<Square> getPossibleMoves(Board board, Square square) {
        List<Move> legalMoves = board.legalMoves();
        List<Square> movesFromSquare = new ArrayList<>();
        for (Move move : legalMoves) {
            if (move.getFrom().equals(square)) {
                movesFromSquare.add(move.getTo());
            }
        }
        return movesFromSquare;

    }

    private Square getPromotionRequiredSuare(Board board) {
        String fen = board.getFen();
        fen = replaceDigitsWithA(fen);
        fen = fen.substring(0, fen.indexOf(' '));
        String black = fen.split("/")[0];
        String white = fen.split("/")[7];
        Square res = null;

        if (black.contains("P")) {
            res = parseRowCol(0, black.indexOf("P"));
        }

        if (white.contains("p")) {
            res = parseRowCol(7, white.indexOf("p"));
        }
        return res;
    }

    private boolean isPromotionRequired(Board board) {
        String fen = board.getFen();
        fen = replaceDigitsWithA(fen);
        fen = fen.substring(0, fen.indexOf(' '));
        String black = fen.split("/")[0];
        String white = fen.split("/")[7];
        boolean res = black.contains("P");

        if (white.contains("p"))
            res = true;

        return res;
    }


    private void promotePawn(Board board, String piece) {
        try {
            HashMap<String, Piece> map = new HashMap<>();
            map.put("KnightBLACK", Piece.BLACK_KNIGHT);
            map.put("KnightWHITE", Piece.WHITE_KNIGHT);
            map.put("BishopBLACK", Piece.BLACK_BISHOP);
            map.put("BishopWHITE", Piece.WHITE_BISHOP);
            map.put("RookBLACK", Piece.BLACK_ROOK);
            map.put("RookWHITE", Piece.WHITE_ROOK);
            map.put("QueenBLACK", Piece.BLACK_QUEEN);
            map.put("QueenWHITE", Piece.WHITE_QUEEN);

            Square square = getPromotionRequiredSuare(board);


            board.setPiece(map.get(piece + board.getSideToMove().flip()), square);
        } catch (Exception e) {
            Log.e("Artemp", piece + board.getSideToMove().flip());
        }
    }


    Board boards = new Board();
    private ChessBoardAdapter adapter;
    Square squre_from;
    Square squareTo;
    TextView movesHistory;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    ArrayList<String> moves = new ArrayList<String>();
    String s = "";
    Side side;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);


        HashMap<String, Piece> promotionPieces = new HashMap<>();
        promotionPieces.put("KnightBLACK", Piece.BLACK_KNIGHT);
        promotionPieces.put("KnightWHITE", Piece.WHITE_KNIGHT);
        promotionPieces.put("BishopBLACK", Piece.BLACK_BISHOP);
        promotionPieces.put("BishopWHITE", Piece.WHITE_BISHOP);
        promotionPieces.put("RookBLACK", Piece.BLACK_ROOK);
        promotionPieces.put("RookWHITE", Piece.WHITE_ROOK);
        promotionPieces.put("QueenBLACK", Piece.BLACK_QUEEN);
        promotionPieces.put("QueenWHITE", Piece.WHITE_QUEEN);


        initHashMap();
        movesHistory = findViewById(R.id.moves_history);

        Intent intent = getIntent();
        String host_id = intent.getStringExtra("host_id");
        side = Side.valueOf(intent.getStringExtra("side"));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String move = dataSnapshot.child("Lobbies").child(host_id).child("lastMove").getValue(String.class);
                try {
                    boards.doMove(move);
                    moves.add(String.valueOf(move));
                    String s = "";
                    for (int j = 0; j < moves.size(); j++) {
                        if (j % 2 == 0) {
                            s += "\n";
                            s += j / 2 + 1;
                            s += ". ";
                        }
                        s += moves.get(j);
                        s += " ";
                    }
                    movesHistory.setText(s);
                } catch (Exception e) {
                    Log.e("Artemp", e.toString());
                }

                drawBoard(boards.getFen());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ART", "Failed to read value.", error.toException());
            }
        });

        boardGV = findViewById(R.id.chessBoardGridView);
        adapter = new ChessBoardAdapter(this);
        boardGV.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose promotion figure");
        final String[] charSequence = new String[]{"Knight", "Bishop", "Rook", "Queen"};
        builder.setCancelable(false);
        builder.setSingleChoiceItems(charSequence, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Move newMove = new Move(squre_from, squareTo, promotionPieces.get(charSequence[which] + boards.getSideToMove().flip()));
                boards.undoMove();
                boards.doMove(newMove);
                myRef.child("Lobbies").child(host_id).child("lastMove").setValue(String.valueOf(newMove));
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();

        boardGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int row = adapter.getItemRow(i);
                int col = adapter.getItemCol(i);
                squareTo = parseRowCol(row, col);
                adapter.clearBoard();

                if (!boards.isMated() && boards.getSideToMove().equals(side)) {

                    if (getPossibleMoves(boards, squre_from).contains(squareTo)) {
                        Move newMove = new Move(squre_from, squareTo);
                        boards.doMove(newMove);
                        moves.add(String.valueOf(newMove));
                        myRef.child("Lobbies").child(host_id).child("lastMove").setValue(String.valueOf(newMove));
                    } else {
                        lightPossibleMovesFromSquare(boards, squareTo);
                        squre_from = squareTo;
                    }
                } else {

                }
                if (isPromotionRequired(boards)) {
                    dialog.show();
                }
                drawBoard(boards.getFen());

                try {
                    s = "";
                    for (int j = 0; j < moves.size(); j++) {
                        if (j % 2 == 0) {
                            s += "\n";
                            s += j / 2 + 1;
                            s += ". ";
                        }
                        s += moves.get(j);
                        s += " ";

                    }
                    movesHistory.setText(s);
                    myRef.child("Lobbies").child(host_id).child("allMoves").setValue(String.valueOf(s));
                } catch (Exception e) {
                    Log.e("Artemp", String.valueOf(e));
                }


            }
        });

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1";


        boards.loadFromFen(fen);
        drawBoard(boards.getFen());


    }

}


