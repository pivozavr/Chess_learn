package com.example.chess3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
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


public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
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

    User currentUser;
    SharedPreferences spref;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        spref = this.getPreferences(Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        title=findViewById(R.id.tvTitle);
        email=findViewById(R.id.email);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        signup =findViewById(R.id.signup);
        checkBox=findViewById(R.id.checkbox);
        seekBar=findViewById(R.id.lvl);
        pawn=findViewById(R.id.pawn);
        knight=findViewById(R.id.knight);
        rook=findViewById(R.id.rook);
        queen=findViewById(R.id.queen);
        king=findViewById(R.id.king);
        signup.setOnClickListener(this);
        RotateAnimation anim =new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        anim.setDuration(700);
        RotateAnimation anim2 =new RotateAnimation(0.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(0); //Repeat animation indefinitely
        anim2.setDuration(700);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Log.e("arteeem", String.valueOf(seekBar.getProgress()));
                if(seekBar.getProgress()==0){
                    pawn.startAnimation(anim);
                    knight.startAnimation(anim2);
                    rook.startAnimation(anim2);
                    queen.startAnimation(anim2);
                    king.startAnimation(anim2);
                }
                else if(seekBar.getProgress()==1){
                    pawn.startAnimation(anim2);
                    knight.startAnimation(anim);
                    rook.startAnimation(anim2);
                    queen.startAnimation(anim2);
                    king.startAnimation(anim2);
                }
                else if(seekBar.getProgress()==2){
                    pawn.startAnimation(anim2);
                    knight.startAnimation(anim2);
                    rook.startAnimation(anim);
                    queen.startAnimation(anim2);
                    king.startAnimation(anim2);
                }
                else if(seekBar.getProgress()==3){
                    pawn.startAnimation(anim2);
                    knight.startAnimation(anim2);
                    rook.startAnimation(anim2);
                    queen.startAnimation(anim);
                    king.startAnimation(anim2);
                }
                else if(seekBar.getProgress()==4){
                    pawn.startAnimation(anim2);
                    knight.startAnimation(anim2);
                    rook.startAnimation(anim2);
                    queen.startAnimation(anim2);
                    king.startAnimation(anim);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view== signup){
            signUp(email.getText().toString(), password.getText().toString());
            Intent intent = new Intent(Register.this, SignUp.class);
            startActivity(intent);
        }

    }

    public void writeNewUser(String email, String password, String username, int lvl) {
        User user = new User(email, password, username, lvl);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        myRef.child("users").child(firebaseUser.getUid()).setValue(user);
    }


    private void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Art", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Authentication succeed.",
                                    Toast.LENGTH_SHORT).show();
                            writeNewUser(email, password, username.getText().toString(), seekBar.getProgress());
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
                            Log.w("Art", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
