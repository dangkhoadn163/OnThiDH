package com.example.dk.onthidh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dk.onthidh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LaunchActivity extends AppCompatActivity {
    private EditText Edtemail, Edtpass;
    private Button login, register, forget;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String userID;
    private boolean hasOpenedChooseActivity;

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
        forget = (Button) findViewById(R.id.forgetpass);
    }

    private void remember() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d("keyyyyy", "" + user.getUid());
                    openChooseActivity(user.getUid());
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Ham dang nhap firebase:
    private void loginFirebase() {
        String email = Edtemail.getText().toString().trim();
        String password = Edtpass.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Edtemail.setError("Please enter your email");
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            Edtemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Edtemail.setError("Invalid email format");
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            Edtemail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Edtpass.setError("Please enter your password");
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            Edtpass.requestFocus();
            return;
        }

        setLoginLoading(true);
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    setLoginLoading(false);
                    if (task.isSuccessful()) {
                        // Dang nhap thanh cong
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            userID = currentUser.getUid();
                            Toast.makeText(LaunchActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            openChooseActivity(userID);
                        } else {
                            Toast.makeText(LaunchActivity.this, "Login succeeded but no active user session was found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Dang nhap that bai
                        Toast.makeText(LaunchActivity.this, getAuthErrorMessage(task), Toast.LENGTH_LONG).show();
                        Log.e("LaunchActivity", "signInWithEmailAndPassword failed", task.getException());
                    }
                }
            });
    }

    private void openChooseActivity(String uid) {
        if (hasOpenedChooseActivity) {
            return;
        }

        hasOpenedChooseActivity = true;
        Intent intent = new Intent(LaunchActivity.this, ChooseActivity.class);
        intent.putExtra("Uid", uid);
        startActivity(intent);
        finish();
    }

    private void sendemail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = Edtemail.getText().toString().trim();
        if (TextUtils.isEmpty(emailAddress)) {
            Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LaunchActivity.this, "Reset email sent", Toast.LENGTH_LONG).show();
                            Log.d("sụt sét", "Email sent ");
                        } else {
                            Toast.makeText(LaunchActivity.this, getAuthErrorMessage(task), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private String getAuthErrorMessage(Task<?> task) {
        if (task.getException() != null && !TextUtils.isEmpty(task.getException().getMessage())) {
            return task.getException().getMessage();
        }
        return "Authentication failed";
    }

    private void setLoginLoading(boolean isLoading) {
        login.setEnabled(!isLoading);
        register.setEnabled(!isLoading);
        forget.setEnabled(!isLoading);
        login.setText(isLoading ? "logging in..." : "login");
    }
}
