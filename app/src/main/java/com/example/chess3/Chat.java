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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Chat extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView mess;
    EditText your_mess;
    TextView chat_id;

    Button send;
    User currentUser;
    SharedPreferences spref;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        mAuth = FirebaseAuth.getInstance();
        your_mess=findViewById(R.id.your_mess);
        mess = findViewById(R.id.mess);
        send =findViewById(R.id.send);
        chat_id=findViewById(R.id.chat_id);
        Intent intent = getIntent();
        String host_id=intent.getStringExtra("host_id");

        chat_id.setText(host_id);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                myRef.child(host_id).child("lastMessage").setValue(your_mess.getText().toString());
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                String last_message = dataSnapshot.child(host_id).child("lastMessage").getValue(String.class);
                mess.setText(last_message);
                Log.w("ARTem", last_message );


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ART", "Failed to read value.", error.toException());
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
            Intent intent = new Intent(Chat.this, SignUp.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.host_men) {
            Intent intent = new Intent(Chat.this, HostJoin.class);
            startActivity(intent);
            return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
    }
}
