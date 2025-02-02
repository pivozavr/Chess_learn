package com.example.chess3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class HostJoinActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    Button join;
    Button host;
    EditText hostId;

    User currentUser;

    SharedPreferencesHelper sprefHelper;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_join_layout);

        initSharedPreferencesHelper();
        initViews();
        initFireBase();
    }



    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews(){
        hostId = findViewById(R.id.host_id);
        join = findViewById(R.id.join);
        host = findViewById(R.id.host);
        join.setOnClickListener(this);
        host.setOnClickListener(this);
    }

    private void initSharedPreferencesHelper() {
        sprefHelper = new SharedPreferencesHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login_men) {
            FirebaseUser user = mAuth.getCurrentUser();
            myRef.child("users").child(user.getUid()).child("status").setValue("offline");
            Intent intent = new Intent(HostJoinActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.host_men) {
            Toast.makeText(this, "You are already here", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.level_men) {
            Intent intent = new Intent(HostJoinActivity.this, LevelsActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    private String generateId(){
        String alb = "abcdefghijklmnopqrstvuwxyz";
        char[] lets = alb.toCharArray();
        String id = "";
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            char let = lets[rnd.nextInt(lets.length)];
            int num = rnd.nextInt(10);
            boolean iff = rnd.nextBoolean();
            if (iff) {
                id += String.valueOf(num);
            } else {
                id += String.valueOf(let).toUpperCase();
            }

        }
        return id;
    }



    @Override
    public void onClick(View view) {
        if (view == host){
            String id = generateId();
            Toast.makeText(HostJoinActivity.this, id, Toast.LENGTH_LONG).show();
            myRef.child("Lobbies").child(id).child("lastMove").setValue("");
            Intent intent = new Intent(HostJoinActivity.this, ChessGameActivity.class);
            intent.putExtra("host_id", id);
            intent.putExtra("side", "WHITE");
            startActivity(intent);
            finish();
        }
        else if(view == join){
            Intent intent = new Intent(HostJoinActivity.this, ComputerChessGameActivity.class);
            intent.putExtra("host_id", hostId.getText().toString());
            intent.putExtra("side", "BLACK");
            startActivity(intent);
            finish();
        }
    }
}
