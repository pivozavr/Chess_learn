package com.example.chess3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class ChessGameActivity extends AppCompatActivity {

    private GridView boardGV;
    private final HashMap<String, Integer> map = new HashMap<>();
    private final HashMap<String, Piece> promotionPieces = new HashMap<>();
    private GameModel coolerBoard;
    private ChessBoardAdapter adapter;
    private Square squareFrom;
    private CoolerSquare coolerSquareTo;
    private RecyclerView movesRecycler;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private List<Move> moves = new MoveList();
    private ArrayList<MoveItem> movesList;
    private GameModel newGameModel;
    private AlertDialog promotionDialog;
    private AlertDialog mateDialog;
    private String hostId;
    private Side side;
    private MovesAdapter movesAdapter;
    private int row;
    private int col;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        initHashMap();
        setupBoardGridView();
        initFields();
        initPickPromotionPieceDialogue();
        initMateDialog();
        setupFirebaseListener();
    }

    private void initFields() {
        Intent intent = getIntent();
        hostId = intent.getStringExtra("host_id");
        side = Side.valueOf(intent.getStringExtra("side"));


        coolerBoard = new GameModel();
        newGameModel = new GameModel();
        drawBoard(coolerBoard.getBoardArray());
    }

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

        promotionPieces.put("KnightBLACK", Piece.BLACK_KNIGHT);
        promotionPieces.put("KnightWHITE", Piece.WHITE_KNIGHT);
        promotionPieces.put("BishopBLACK", Piece.BLACK_BISHOP);
        promotionPieces.put("BishopWHITE", Piece.WHITE_BISHOP);
        promotionPieces.put("RookBLACK", Piece.BLACK_ROOK);
        promotionPieces.put("RookWHITE", Piece.WHITE_ROOK);
        promotionPieces.put("QueenBLACK", Piece.BLACK_QUEEN);
        promotionPieces.put("QueenWHITE", Piece.WHITE_QUEEN);
    }

    private void initPickPromotionPieceDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose promotion figure");
        final String[] options = {"Knight", "Bishop", "Rook", "Queen"};
        builder.setCancelable(false);
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            Move promotionMove = new Move(squareFrom, coolerSquareTo.getSquare(), promotionPieces.get(options[which] + coolerBoard.getBoard().getSideToMove().flip()));
            coolerBoard.getBoard().undoMove();
            coolerBoard.getBoard().doMove(promotionMove);
            moves.remove(moves.size()-1);
            moves.add(promotionMove);
            updateMoveList();
            drawBoard(coolerBoard.getBoardArray());
            updateLastMove(moves.get(moves.size()-1));
            dialog.dismiss();
        });
        promotionDialog = builder.create();
    }

    private void initMateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(side == coolerBoard.getBoard().getSideToMove().flip())
            builder.setTitle("You lost by mate");
        else
            builder.setTitle("You won by mate");
        builder.setCancelable(false);
        builder.setPositiveButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ChessGameActivity.this, HostJoinActivity.class);
                startActivity(intent);
                mateDialog.dismiss();
                finish();
            }
        });
        mateDialog = builder.create();

    }

    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS отправлено успешно.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка отправки SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFirebaseListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    handleFirebaseMoveUpdate(dataSnapshot);
                    //sendSms("0539638101", "Твой ход");
                } catch (Exception e) {
                    Log.e("Firebase", "Error updating move", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void setupBoardGridView() {
        boardGV = findViewById(R.id.chessBoardGridView);
        movesRecycler = findViewById(R.id.movesRecycler);
        movesRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChessBoardAdapter(this);
        boardGV.setAdapter(adapter);

        boardGV.setOnItemClickListener((adapterView, view, position, id) -> handleBoardClick(position));
    }

    private void handleFirebaseMoveUpdate(DataSnapshot dataSnapshot) {
        String moveStr = dataSnapshot.child("Lobbies").child(hostId).child("lastMove").getValue(String.class);
        Move databaseMove = new Move(moveStr, coolerBoard.getBoard().getSideToMove());

        coolerBoard.getBoard().doMove(databaseMove);
        moves.add(databaseMove);

        updateMoveList();
        drawBoard(coolerBoard.getBoardArray());
        newGameModel = coolerBoard;
        myRef.child("Lobbies").child(hostId).child("allMoves").setValue(moves.toString());
        try {
            movesRecycler.scrollToPosition(movesAdapter.getItemCount() - 1);
        }
        catch (Exception e){}
    }

    private void handleBoardClick(int position) {
        if(side == Side.WHITE) {
            row = adapter.getItemRow(position);
            col = adapter.getItemCol(position);
        }
        else{
            row = 7 - adapter.getItemRow(position);
            col = 7 - adapter.getItemCol(position);
        }
        coolerSquareTo = new CoolerSquare(Square.A1);
        coolerSquareTo.parseRowCol(row, col);

        adapter.clearBoard();


        if (coolerBoard.getFen().equals(newGameModel.getFen())) {
            if (!coolerBoard.getBoard().isMated()) {
                handlePieceMove(row, col);
            } else {
                mateDialog.show();
            }

            if (coolerBoard.isPromotionRequired()) {
                promotionDialog.show();
            }
            else if (moves.size()!=0)
                updateLastMove(moves.get(moves.size()-1));



            drawBoard(coolerBoard.getBoardArray());
            newGameModel = coolerBoard;
        }


        updateMoveList();
    }

    private void handlePieceMove(int row, int col) {
        if (coolerBoard.getPossibleMoves(squareFrom).contains(coolerSquareTo.getSquare())) {
            Move newMove = new Move(squareFrom, coolerSquareTo.getSquare());
            coolerBoard.getBoard().doMove(newMove);
            moves.add(newMove);

        } else {
            lightPossibleMovesFromSquare(coolerSquareTo);
            squareFrom = coolerSquareTo.getSquare();
        }
        try {
            movesRecycler.scrollToPosition(movesAdapter.getItemCount() - 1);
        }
        catch (Exception e){}

    }

    private void updateMoveList() {
        movesList = new ArrayList<>();
        for (int i = 0; i < moves.size(); i += 2) {
            if (i == moves.size() - 1) {
                movesList.add(new MoveItem(String.valueOf((i + 2) / 2), moves.get(i), null, moves.subList(0, i + 1)));
            } else {
                movesList.add(new MoveItem(String.valueOf((i + 2) / 2), moves.get(i), moves.get(i + 1), moves.subList(0, i + 2)));
            }
        }
        movesAdapter = new MovesAdapter(getApplicationContext(), movesList);
        movesRecycler.setAdapter(movesAdapter);

        movesAdapter.setOnClickListener((position, moveList) -> {
            newGameModel = new GameModel();
            if(moveList.size()%2==0 && position == 1)
                newGameModel.loadFromMovesList(moveList.subList(0, moveList.size()-1));
            else
                newGameModel.loadFromMovesList(moveList);
            drawBoard(newGameModel.getBoardArray());
        });
    }

    private void updateLastMove(Move move) {
        myRef.child("Lobbies").child(hostId).child("lastMove").setValue(move.toString());
        myRef.child("Lobbies").child(hostId).child("allMoves").setValue(moves.toString());
    }

    private void drawBoard(String[][] boardArray) {
        if(side == Side.WHITE){
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    updateChessBoardCell(i, j, map.get(boardArray[i][j]));
                }
            }
        }
        else if(side == Side.BLACK){
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    updateChessBoardCell(i, j, map.get(boardArray[7-i][7-j]));
                }
            }
        }
    }

    private void updateChessBoardCell(int row, int col, int drawableResId) {
        if (adapter != null) {
            adapter.updateCellImage(row, col, drawableResId);
        }
    }

    private void lightPossibleMovesFromSquare(CoolerSquare square) {
        if(side == Side.WHITE) {
            for (Square targetSquare : coolerBoard.getPossibleMoves(square.getSquare())) {
                CoolerSquare coolerTargetSquare = new CoolerSquare(targetSquare);
                adapter.lightUpCell(coolerTargetSquare.getSquareRow(), coolerTargetSquare.getSquareCol());
            }
            adapter.lightCell(square.getSquareRow(), square.getSquareCol());
        }
        else{
            for (Square targetSquare : coolerBoard.getPossibleMoves(square.getSquare())) {
                CoolerSquare coolerTargetSquare = new CoolerSquare(targetSquare);
                adapter.lightUpCell(7-coolerTargetSquare.getSquareRow(), 7-coolerTargetSquare.getSquareCol());
            }
            adapter.lightCell(7-square.getSquareRow(), 7-square.getSquareCol());
        }
    }
}
