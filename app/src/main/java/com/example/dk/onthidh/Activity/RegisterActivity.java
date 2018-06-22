package com.example.dk.onthidh.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

final String name = Edtname.getText().toString();
        String email = Edtemail.getText().toString();
        String password = Edtpass.getText().toString();
        // Neu email & password & name trong 2 cai edit khac null:
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
        // Lấy id của user vừa đăng kí:
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid", "onComplete: uid=" + userID);
        // Upload dữ liệu lên firebase database:
        // Trước khi làm cái này nhớ nhúng thư viện:
        // compile 'com.google.firebase:firebase-database:11.0.2'
        mDatabase.child("account").child(userID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
        // Dang ki thanh cong
        Toast.makeText(RegisterActivity.this, "Register Success !", Toast.LENGTH_LONG).show();
        finish();
        }
        }).addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        Toast.makeText(RegisterActivity.this, "Disconnected !", Toast.LENGTH_LONG).show();
        }
        });

        } else {
        // Dang ki that bai
        Toast.makeText(RegisterActivity.this, "Register Failure !", Toast.LENGTH_LONG).show();
        }
        }
        });
        }
        }

private void Anhxa() {
        Edtname = (EditText) findViewById(R.id.nameuser);
        Edtemail = (EditText) findViewById(R.id.emailuser);
        Edtpass = (EditText) findViewById(R.id.passuser);
        confirm = (Button) findViewById(R.id.confirmuser);
        }
        }