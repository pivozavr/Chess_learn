package com.example.chess3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelsActivity extends AppCompatActivity {

    RecyclerView levelsRecycler;
    LevelsAdapter levelsAdapter;
    TextView pointCounterTextView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;
    SharedPreferencesHelper sprefHelper;
    private String total_points;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lvl_layout);

        try {
            sprefHelper = new SharedPreferencesHelper(this);
            total_points = sprefHelper.getDataString("points");
            levelsRecycler = findViewById(R.id.menu_section);
            pointCounterTextView = findViewById(R.id.point_counter);
            pointCounterTextView.setText(total_points);
            levelsRecycler.setLayoutManager(new LinearLayoutManager(this));
            List<String> buttons = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                buttons.add(String.valueOf(i+1));
            }

            levelsAdapter = new LevelsAdapter(buttons, text ->{
                if((Integer.parseInt(text)-1)*3000<=Integer.parseInt(total_points)){
                    Intent intent = new Intent(LevelsActivity.this, ComputerChessGameActivity.class);
                    intent.putExtra("puz_num", text);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this, "You have not yet acess to this level", Toast.LENGTH_LONG).show();
                }
            });

            levelsRecycler.setAdapter(levelsAdapter);
            mAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Log.d("Errr", String.valueOf(e));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login_men) {
            FirebaseUser user = mAuth.getCurrentUser();
            myRef.child("users").child(user.getUid()).child("status").setValue("offline");
            Intent intent = new Intent(LevelsActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.host_men) {
            Intent intent = new Intent(LevelsActivity.this, HostJoinActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id == R.id.level_men){
            Toast.makeText(this, "You are already here", Toast.LENGTH_LONG).show();
            return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }
}
