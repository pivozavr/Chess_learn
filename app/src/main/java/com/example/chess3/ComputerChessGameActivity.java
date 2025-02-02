package com.example.chess3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ComputerChessGameActivity extends AppCompatActivity {

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
    private AlertDialog mateDialog;
    private Side side;
    private int row;
    private int col;
    private String puzzleFen;
    private MoveList puzzleMoves = new MoveList();
    private int moveNum;
    private String puzzleNumber;
    private int lvl_lives;
    private SharedPreferencesHelper spref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        initFirebaseData();
        myRef.child("Bebra").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                initHashMap();
                setupBoardGridView();
                initFields();
            }});
    }

    private void initFields() {
        coolerBoard = new GameModel(puzzleFen);
        side = coolerBoard.getBoard().getSideToMove();
        drawBoard(coolerBoard.getBoardArray());
        lvl_lives = 3;
        moveNum = 0;
        spref = new SharedPreferencesHelper(this);
    }


    private void initFirebaseData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Intent intent = getIntent();
                    puzzleNumber = "Puzzle" + intent.getStringExtra("puz_num");
                    puzzleFen = String.valueOf(snapshot.child("Puzzles").child(puzzleNumber).child("PuzzleFen").getValue());
                    List<String> flArray = Arrays.asList(String.valueOf(snapshot.child("Puzzles").child(puzzleNumber).child("PuzzleMoves").getValue()).split(", "));
                    Board flBoard = new Board();
                    flBoard.loadFromFen(puzzleFen);
                    Side puzzleSide = flBoard.getSideToMove();
                    for (int i = 0; i < flArray.size(); i++) {
                        puzzleMoves.add(new Move(flArray.get(i), puzzleSide));
                        puzzleSide.flip();
                    }

                }
                catch (Exception e){
                    Log.d("CompChess6", String.valueOf(snapshot.child("Puzzles").child("Puzzle1").child("PuzzleFen").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


    private void initMateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("The Puzzle is solved!");
        if (lvl_lives <=0) {
            builder.setTitle("Try better!");
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(com.example.chess3.ComputerChessGameActivity.this, LevelsActivity.class);
                if(!spref.getDataBool("is_solved")) {
                    spref.putDataString("points", String.valueOf(Integer.parseInt(spref.getDataString("points"))+lvl_lives*1000));
                    spref.putDataBool("is_solved", true);
                }
                startActivity(intent);
                mateDialog.dismiss();
                finish();
            }
        });
        mateDialog = builder.create();

    }



    private void setupBoardGridView() {
        boardGV = findViewById(R.id.chessBoardGridView);
        movesRecycler = findViewById(R.id.movesRecycler);
        movesRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChessBoardAdapter(this);
        boardGV.setAdapter(adapter);

        boardGV.setOnItemClickListener((adapterView, view, position, id) -> handleBoardClick(position));
    }


    private void handleBoardClick(int position) {
        if (side == Side.WHITE) {
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



        if (!coolerBoard.getBoard().isMated()) {
            handlePieceMove(row, col);
        } else {
            initMateDialog();
            mateDialog.show();

        }


        drawBoard(coolerBoard.getBoardArray());
    }

    private void handlePieceMove(int row, int col) {
        if (coolerBoard.getPossibleMoves(squareFrom).contains(coolerSquareTo.getSquare())) {
            Move newMove = new Move(squareFrom, coolerSquareTo.getSquare());
            if(newMove.equals(puzzleMoves.get(moveNum))){
                coolerBoard.getBoard().doMove(newMove);
                moves.add(newMove);
                if(coolerBoard.getBoard().isMated()){
                    initMateDialog();
                    mateDialog.show();
                }
                else{
                    moveNum++;
                    coolerBoard.getBoard().doMove(puzzleMoves.get(moveNum));
                    moveNum++;
                }

            }
            else{
                Toast.makeText(this, "Your move is wrong!", Toast.LENGTH_LONG).show();
                lvl_lives -=1;
                if (lvl_lives ==0){
                    initMateDialog();
                    mateDialog.show();
                }
            }

        } else {
            lightPossibleMovesFromSquare(coolerSquareTo);
            squareFrom = coolerSquareTo.getSquare();
        }


    }




    private void drawBoard(String[][] boardArray) {
        if (side == Side.WHITE) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    updateChessBoardCell(i, j, map.get(boardArray[i][j]));
                }
            }
        } else if (side == Side.BLACK) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    updateChessBoardCell(i, j, map.get(boardArray[7 - i][7 - j]));
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
        if (side == Side.WHITE) {
            for (Square targetSquare : coolerBoard.getPossibleMoves(square.getSquare())) {
                CoolerSquare coolerTargetSquare = new CoolerSquare(targetSquare);
                adapter.lightUpCell(coolerTargetSquare.getSquareRow(), coolerTargetSquare.getSquareCol());
            }
            adapter.lightCell(square.getSquareRow(), square.getSquareCol());
        } else {
            for (Square targetSquare : coolerBoard.getPossibleMoves(square.getSquare())) {
                CoolerSquare coolerTargetSquare = new CoolerSquare(targetSquare);
                adapter.lightUpCell(7 - coolerTargetSquare.getSquareRow(), 7 - coolerTargetSquare.getSquareCol());
            }
            adapter.lightCell(7 - square.getSquareRow(), 7 - square.getSquareCol());
        }
    }
}
