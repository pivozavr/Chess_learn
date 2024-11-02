package com.example.chess3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class HostJoin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SharedPreferences spref;
    Button join;
    Button host;
    EditText hostId;

    User currentUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String id="";
    String alb = "abcdefghijklmnopqrstvuwxyz";
    char[] lets = alb.toCharArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_join);
        mAuth = FirebaseAuth.getInstance();
        hostId = findViewById(R.id.host_id);
        join =findViewById(R.id.join);
        host=findViewById(R.id.host);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HostJoin.this, Chat.class);
                intent.putExtra("host_id", hostId.getText().toString());
                startActivity(intent);
            }
        });
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                for(int i=0; i<4; i++) {
                    char let = lets[rnd.nextInt(lets.length)];
                    int num = rnd.nextInt(10);
                    boolean iff = rnd.nextBoolean();
                    if(iff){
                        id += String.valueOf(num);
                    }
                    else{
                        id += String.valueOf(let).toUpperCase();
                    }

                }
                Log.d("ART", id);
                Toast.makeText(HostJoin.this, id, Toast.LENGTH_LONG).show();
                myRef.child(id).child("lastMessage").setValue("");
                Intent intent=new Intent(HostJoin.this, GameModel.class);
                intent.putExtra("host_id", id);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login_men) {
                Intent intent=new Intent(HostJoin.this, SignUp.class);
                intent.putExtra("out", false);
                startActivity(intent);
                return true;}
        else if (id == R.id.host_men) {
                Toast.makeText(this, "You are already here", Toast.LENGTH_LONG).show();
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }
}
