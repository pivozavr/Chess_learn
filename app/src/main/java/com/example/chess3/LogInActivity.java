package com.example.chess3;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



public class LogInActivity extends AppCompatActivity implements View.OnClickListener{


    EditText emailEditText;
    EditText passwordEditText;
    Button signupButton;
    Button loginButton;
    CheckBox checkBox;



    SharedPreferencesHelper sprefHelper;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        initFireBase();
        initSharedPreferencesHelper();
        initViews();
        getLogInData();
    }

    private void initSharedPreferencesHelper(){
        sprefHelper = new SharedPreferencesHelper(this);
    }

    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews(){
        emailEditText =findViewById(R.id.email);
        passwordEditText =findViewById(R.id.password);
        signupButton =findViewById(R.id.signup);
        loginButton =findViewById(R.id.login);
        checkBox=findViewById(R.id.checkbox);
        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
    }

    private void getLogInData(){
        String email = sprefHelper.getDataString("email");
        String password = sprefHelper.getDataString("password");
        if (!email.isEmpty() && !password.isEmpty()){
            signIn(email, password);
        }

    }


    @Override
    public void onClick(View view) {
        if(view==signupButton){
            Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            }

        if (view== loginButton){
            if(checkData()) {
                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        }
    }

    private boolean checkData(){
        boolean result = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()){
            result = false;
            Toast.makeText(this, "Use valid E-mail", Toast.LENGTH_SHORT).show();
        }
        else if(passwordEditText.getText().toString().length()<6){
            result = false;
            Toast.makeText(this, "Password must be minimum 6 symbols", Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    user = mAuth.getCurrentUser();
                    Toast.makeText(LogInActivity.this, "Authentication succeed.", Toast.LENGTH_SHORT).show();
                    myRef.child("users").child(user.getUid()).child("status").setValue("online");
                    if (checkBox.isChecked()) {
                        sprefHelper.putDataString("email", email);
                        sprefHelper.putDataString("password", password);
                    }
                    Intent intent = new Intent(LogInActivity.this, HostJoinActivity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    Toast.makeText(LogInActivity.this, "Wrong password or E-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


