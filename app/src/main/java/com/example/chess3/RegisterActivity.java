package com.example.chess3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    TextView title;
    EditText email;
    EditText username;
    EditText password;
    Button signup;
    CheckBox checkBox;
    SeekBar seekBar;
    ImageView pawn;
    ImageView knight;
    ImageView rook;
    ImageView queen;
    ImageView king;

    RotateAnimation rotateAnimation;
    RotateAnimation anim2;


    SharedPreferencesHelper sprefHelper;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initFireBase();
        initSharedPreferencesHelper();
        initViews();
        initAnimations();
    }


    private void initSharedPreferencesHelper() {
        sprefHelper = new SharedPreferencesHelper(this);
    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        title = findViewById(R.id.tvTitle);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        checkBox = findViewById(R.id.checkbox);
        seekBar = findViewById(R.id.lvl);
        pawn = findViewById(R.id.pawn);
        knight = findViewById(R.id.knight);
        rook = findViewById(R.id.rook);
        queen = findViewById(R.id.queen);
        king = findViewById(R.id.king);
        signup.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void initAnimations() {
        rotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        rotateAnimation.setDuration(700);
        anim2 = new RotateAnimation(0.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(0); //Repeat animation indefinitely
        anim2.setDuration(700);
    }


    @Override
    public void onClick(View view) {
        if (view == signup) {
            signUp(email.getText().toString(), password.getText().toString());
            signup.setClickable(false);
        }

    }

    public void writeNewUser(String email, String password, String username, int lvl) {
        User user = new User(email, password, username, lvl);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        myRef.child("users").child(firebaseUser.getUid()).setValue(user);
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(RegisterActivity.this, "Accout created.", Toast.LENGTH_SHORT).show();
                    writeNewUser(email, password, username.getText().toString(), seekBar.getProgress());
                    if (checkBox.isChecked()) {
                        sprefHelper.putData("out", "true");
                        sprefHelper.putData("email", email);
                        sprefHelper.putData("password", password);
                        Log.d("AQrt", sprefHelper.getData("email"));

                    }
                    Intent intent = new Intent(RegisterActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Acoount creation failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        Log.e("arteeem", String.valueOf(seekBar.getProgress()));
        if (seekBar.getProgress() == 0) {
            pawn.startAnimation(rotateAnimation);
            knight.clearAnimation();
            rook.clearAnimation();
            queen.clearAnimation();
            king.clearAnimation();
        } else if (seekBar.getProgress() == 1) {
            pawn.clearAnimation();
            knight.startAnimation(rotateAnimation);
            rook.clearAnimation();
            queen.clearAnimation();
            king.clearAnimation();
        } else if (seekBar.getProgress() == 2) {
            pawn.clearAnimation();
            knight.clearAnimation();
            rook.startAnimation(rotateAnimation);
            queen.clearAnimation();
            king.clearAnimation();
        } else if (seekBar.getProgress() == 3) {
            pawn.clearAnimation();
            knight.clearAnimation();
            rook.clearAnimation();
            queen.startAnimation(rotateAnimation);
            king.clearAnimation();
        } else if (seekBar.getProgress() == 4) {
            pawn.clearAnimation();
            knight.clearAnimation();
            rook.clearAnimation();
            queen.clearAnimation();
            king.startAnimation(rotateAnimation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
