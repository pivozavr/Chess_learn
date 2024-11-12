package com.example.chess3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    EditText email;
    EditText password;
    Button signup;
    Button login;
    CheckBox checkBox;


    SharedPreferencesHelper sprefHelper;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFireBase();
        initSharedPreferencesHelper();
        initViews();
        getLogInData();
    }

    private void initSharedPreferencesHelper() {
        sprefHelper = new SharedPreferencesHelper(this);
    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        checkBox = findViewById(R.id.checkbox);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    private void getLogInData() {
        if (sprefHelper.getData("out").equals("true")) {
            String email = sprefHelper.getData("email");
            String password = sprefHelper.getData("password");
            if (email != "" && password != "") {
                signIn(email, password);
                Intent intent = new Intent(SignUpActivity.this, HostJoinActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view == signup) {
            Intent intent = new Intent(SignUpActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if (view == login) {
            signIn(email.getText().toString(), password.getText().toString());
            Intent intent = new Intent(SignUpActivity.this, HostJoinActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Authentication succeed.", Toast.LENGTH_SHORT).show();
                            sprefHelper.putData("out", "true");
                            myRef.child("users").child(user.getUid()).child("status").setValue("online");
                            if (checkBox.isChecked()) {
                                sprefHelper.putData("email", email);
                                sprefHelper.putData("password", password);
                            } else {
                                sprefHelper.putData("email", "");
                                sprefHelper.putData("password", "");
                            }

                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}


