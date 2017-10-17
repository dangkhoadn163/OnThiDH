package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LaunchActivity extends AppCompatActivity {
    private EditText Edtemail, Edtpass;
    private Button login, register;

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
               //startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    // Anh xa cac widget
    private void Anhxa() {
        Edtemail = (EditText) findViewById(R.id.nameuser);
        Edtpass = (EditText) findViewById(R.id.passuser);
        login = (Button) findViewById(R.id.loginuser);
        register = (Button) findViewById(R.id.registeruser);
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
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        startActivity(new Intent(LaunchActivity.this,MainActivity.class).putExtra("Uid",userID));

                    } else {
                        // Dang nhap that bai
                        Toast.makeText(LaunchActivity.this, "Login Failure !", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }
}