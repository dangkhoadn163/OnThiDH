package com.example.dk.onthidh.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dk.onthidh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText Edtname, Edtemail, Edtpass;
    private Button confirm;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();

        // Lay duong dan cua note goc tren database:
        mDatabase = FirebaseDatabase.getInstance().getReference();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFirebase();
            }
        });


    }

    private void registerFirebase() {

        final String name = Edtname.getText().toString().trim();
        final String email = Edtemail.getText().toString().trim();
        String password = Edtpass.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Edtname.setError("Please enter your name");
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            Edtname.requestFocus();
            return;
        }
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
        if (password.length() < 6) {
            Edtpass.setError("Password must be at least 6 characters");
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            Edtpass.requestFocus();
            return;
        }

        setRegisterLoading(true);
        Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    setRegisterLoading(false);
                    if (task.isSuccessful()) {
                        // Lấy id của user vừa đăng kí:
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            Toast.makeText(RegisterActivity.this, "Register succeeded but user session is missing", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String userID = currentUser.getUid();
                        Log.d("uid", "onComplete: uid=" + userID);
                        // Upload dữ liệu lên firebase database:
                        // Trước khi làm cái này nhớ nhúng thư viện:
                        // compile 'com.google.firebase:firebase-database:11.0.2'
                        DatabaseReference accountRef = mDatabase.child("account").child(userID);
                        accountRef.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    accountRef.child("email").setValue(email);
                                    Toast.makeText(RegisterActivity.this, "Register Success !", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, getAuthErrorMessage(task), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        // Dang ki that bai
                        Toast.makeText(RegisterActivity.this, getAuthErrorMessage(task), Toast.LENGTH_LONG).show();
                        Log.e("RegisterActivity", "createUserWithEmailAndPassword failed", task.getException());
                    }
                }
            });
    }

    private void Anhxa() {
        Edtname = (EditText) findViewById(R.id.nameuser);
        Edtemail = (EditText) findViewById(R.id.emailuser);
        Edtpass = (EditText) findViewById(R.id.passuser);
        confirm = (Button) findViewById(R.id.confirmuser);
    }

    private String getAuthErrorMessage(Task<?> task) {
        if (task.getException() != null && !TextUtils.isEmpty(task.getException().getMessage())) {
            return task.getException().getMessage();
        }
        return "Authentication failed";
    }

    private void setRegisterLoading(boolean isLoading) {
        confirm.setEnabled(!isLoading);
        confirm.setText(isLoading ? "creating..." : "confirm");
    }
}
