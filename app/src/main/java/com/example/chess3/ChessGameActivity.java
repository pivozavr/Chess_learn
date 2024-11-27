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

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ChessGameActivity extends AppCompatActivity{
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


    public void lightPossibleMovesFromSquare(CoolerSquare square) {
        for (Square squaree : coolerBoard.getPossibleMoves(square.getSquare())) {
            CoolerSquare coolerSquare = new CoolerSquare(squaree);
            lightUpChessBoardCell(coolerSquare.getSquareRow(), coolerSquare.getSquareCol());
        }
        lightChessBoardCell(square.getSquareRow(), square.getSquareCol());
    }




    private void drawBoard(String[][] fen) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int resId = map.get(fen[i][j]);
                updateChessBoardCell(i, j, resId);
            }
        }

    }


    GameModel coolerBoard;
    private ChessBoardAdapter adapter;
    Square squre_from;
    Square squareTo;
    CoolerSquare coolerSquareTo = new CoolerSquare(squareTo);
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
                    coolerBoard.getBoard().doMove(move);
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

                drawBoard(coolerBoard.getBoardArray());
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
                Move newMove = new Move(squre_from, coolerSquareTo.getSquare(), promotionPieces.get(charSequence[which] + coolerBoard.getBoard().getSideToMove().flip()));
                coolerBoard.getBoard().undoMove();
                coolerBoard.getBoard().doMove(newMove);
                myRef.child("Lobbies").child(host_id).child("lastMove").setValue(String.valueOf(newMove));
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        boardGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    int row = adapter.getItemRow(i);
                    int col = adapter.getItemCol(i);
                    coolerSquareTo.parseRowCol(row, col);
                    adapter.clearBoard();

                    if (!coolerBoard.getBoard().isMated()
                           // && coolerBoard.getBoard().getSideToMove().equals(side)
                    ) {

                        if (coolerBoard.getPossibleMoves(squre_from).contains(coolerSquareTo.getSquare())) {
                            Move newMove = new Move(squre_from, coolerSquareTo.getSquare());
                            coolerBoard.getBoard().doMove(newMove);
                            moves.add(String.valueOf(newMove));
                            myRef.child("Lobbies").child(host_id).child("lastMove").setValue(String.valueOf(newMove));
                        } else {
                            lightPossibleMovesFromSquare(coolerSquareTo);
                            squre_from = coolerSquareTo.getSquare();
                        }
                    } else {

                    }
                    if (coolerBoard.isPromotionRequired()) {
                        dialog.show();
                    }
                    drawBoard(coolerBoard.getBoardArray());
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
                catch (Exception e){
                    Log.e("artq", e.toString());
                }
            }

        });
        coolerBoard = new GameModel();
        MoveList moveList = new MoveList();
        Move move = new Move(Square.E2, Square.E4);
        moveList.add(move);
        coolerBoard.loadFromMovesList(moveList);
        drawBoard(coolerBoard.getBoardArray());

    }

}


