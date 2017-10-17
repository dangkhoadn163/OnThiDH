package com.example.dk.onthidh;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class Score extends AppCompatActivity {
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    RadioButton cau1a;
    Test s= new Test();
    RadioButton[][] rdbtn = new RadioButton[50][4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        anhxa();
        Nav();
        Demo();
    }
    private void anhxa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        cau1a=(RadioButton)findViewById(R.id.cau1a);
        for (int i = 0; i < 50; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                String quizid = "cau" + (i + 1) + (char)(j + 97);
                int resID = getResources().getIdentifier(quizid, "id", getPackageName());
                rdbtn[i][j] = ((RadioButton) findViewById(resID));
                Log.d("arr", quizid);


            }



        }
    }
    private void Demo(){

////You can set the background color
//        cau1a.setBackgroundColor(Color.RED);
////Text color
//        cau1a.setTextColor(Color.RED);
//or highlight color
//        cau1a.setHighlightColor(Color.RED);

        cau1a.setButtonTintList(ColorStateList.valueOf(Color.RED));
    }
    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_tracnghiem);
        ab.setTitle("DANGKHOADN");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        navigation = (NavigationView) findViewById(R.id.nvcView);
        Button btnwatch = (Button) findViewById(R.id.btnWatch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            drawer.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

}
