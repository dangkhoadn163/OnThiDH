package com.example.dk.onthidh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.R;

public class MainActivity extends AppCompatActivity {
    String uid;
    String monhoc;
    Toolbar toolbar;
    private Button btndethi,btnbaitap,btntailieu;
    private TextView luyenthimon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uid = getIntent().getExtras().getString("Uid");
        monhoc = getIntent().getExtras().getString("monhoc");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Anhxa ();
        setActionClick();
        title();
        Nav();
    }
    private  void title(){
        if(monhoc.equals("anhvan")){
            luyenthimon.setText("Luyện thi môn Anh Văn");
        }
        else if(monhoc.equals("vatly")){
            luyenthimon.setText("Luyện thi môn Vật Lý");
        }
        else if(monhoc.equals("toanhoc")){
            luyenthimon.setText("Luyện thi môn Toán");
        }
        else if(monhoc.equals("hoahoc")){
            luyenthimon.setText("Luyện thi môn Hóa Học");
        }
        else if(monhoc.equals("dialy")){
            luyenthimon.setText("Luyện thi môn Địa Lý");
        }
        else if(monhoc.equals("lichsu")){
            luyenthimon.setText("Luyện thi môn Lịch Sử");
        }
        else if(monhoc.equals("gdcd")){
            luyenthimon.setText("Luyện thi môn GDCD");
        }
        else if(monhoc.equals("sinhhoc")){
            luyenthimon.setText("Luyện thi môn Sinh Học");
        }
    }
    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private  void Anhxa(){

        btndethi = (Button) findViewById(R.id.dethi);
        btntailieu = (Button) findViewById(R.id.tailieu);
        btnbaitap = (Button) findViewById(R.id.baitap);
//        btndedathi= (Button)findViewById(R.id.dedathi);
        luyenthimon= (TextView)findViewById(R.id.tv_title);
    }
    public void setActionClick(){
        btndethi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,ListTest.class);
                intent.putExtra("monhoc",monhoc);
                intent.putExtra("Uid", uid);
                MainActivity.this.startActivity(intent);
            }
        });
        btnbaitap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        btntailieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

