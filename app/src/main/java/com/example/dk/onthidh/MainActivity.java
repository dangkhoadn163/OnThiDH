package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String uid;
    String monhoc;
    private Button btndethi,btnbaitap,btntailieu;
    private TextView luyenthimon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uid = getIntent().getExtras().getString("Uid");
        monhoc = getIntent().getExtras().getString("monhoc");
        Anhxa ();

        setActionClick();
        title();
    }
    private  void title(){
        Log.d("mmm",monhoc);
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
    private  void Anhxa(){

        btndethi = (Button) findViewById(R.id.dethi);
        btntailieu = (Button) findViewById(R.id.tailieu);
        btnbaitap = (Button) findViewById(R.id.baitap);
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
    }

}

