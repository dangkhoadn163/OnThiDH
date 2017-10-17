package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String uid;
    private Button btndethi,btnbaitap,btntailieu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uid = getIntent().getExtras().getString("Uid");
        Anhxa ();
        setActionClick();
    }
    private  void Anhxa(){

        btndethi = (Button) findViewById(R.id.dethi);
        btntailieu = (Button) findViewById(R.id.tailieu);
        btnbaitap = (Button) findViewById(R.id.baitap);
    }
    public void setActionClick(){
        btndethi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Test", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ListTest.class).putExtra("Uid",uid));
            }
        });
    }

}

