package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LaunchActivity extends AppCompatActivity {
    private EditText Edtemail, Edtpass;
    private Button login, register,forget;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String userID;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Anhxa();
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFirebase();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendemail();
            }
        });
        remember();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Anh xa cac widget
    private void Anhxa() {
        Edtemail = (EditText) findViewById(R.id.nameuser);
        Edtpass = (EditText) findViewById(R.id.passuser);
        login = (Button) findViewById(R.id.loginuser);
        register = (Button) findViewById(R.id.registeruser);
        forget= (Button)findViewById(R.id.forgetpass);
    }
    private  void remember() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                    // Authenticated successfully with authData
                    Intent intent = new Intent(LaunchActivity.this, ChooseActivity.class);
                    intent.putExtra("Uid", userID);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Ham dang nhap firebase:
    private void loginFirebase() {
        String email = Edtemail.getText().toString();
        String password = Edtpass.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Dang nhap thanh cong
                        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        startActivity(new Intent(LaunchActivity.this,ChooseActivity.class).putExtra("Uid",userID));

                    } else {
                        // Dang nhap that bai
                        Toast.makeText(LaunchActivity.this, "Login Failure !", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendemail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = Edtemail.getText().toString();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("sụt sét", "Email sent ");
                        }
                    }
                });
    }
}