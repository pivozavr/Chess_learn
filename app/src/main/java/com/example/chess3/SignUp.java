package com.example.chess3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText email;
    EditText password;

    Button signup;


    Button login;
    CheckBox checkBox;

    User currentUser;
    SharedPreferences spref;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        spref = this.getPreferences(Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signup =findViewById(R.id.signup);
        login =findViewById(R.id.login);
        checkBox=findViewById(R.id.checkbox);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("out", true)){
        String email = spref.getString("email", "");
        String password = spref.getString("password", "");
        if (email != "" && password != ""){
            intent=new Intent(SignUp.this, HostJoin.class);
            startActivity(intent);
        }}

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String eemail = ds.child(firebaseUser.getUid()).child("email").getValue(String.class);
//                    String ppassword = ds.child(firebaseUser.getUid()).child("password").getValue(String.class);
//                    Log.d("ART", eemail + " / " + ppassword);
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ART", "Failed to read value.", error.toException());
            }
        });





    }


    @Override
    public void onClick(View view) {
        if(view==signup){
            Intent intent = new Intent(SignUp.this, Register.class);
            startActivity(intent);
        }
        if (view== login){
            signIn(email.getText().toString(), password.getText().toString());
            Intent intent=new Intent(SignUp.this, HostJoin.class);
            startActivity(intent);
        }
    }


    private void signIn(String email, String password) {
        // [START sign_in_with_email]

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Art", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUp.this, "Authentication succeed.",
                                    Toast.LENGTH_SHORT).show();
                            myRef.child("users").child(user.getUid()).child("status").setValue("online");
                            if (checkBox.isChecked()) {
                                spref.edit().putString("email", email).apply();
                                spref.edit().putString("password", password).apply();

                            }
                            else{
                                spref.edit().putString("email", "").apply();
                                spref.edit().putString("password", "").apply();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Art", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


        // [END sign_in_with_email]
    }

}


