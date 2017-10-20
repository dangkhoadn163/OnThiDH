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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Score extends AppCompatActivity {
    private DatabaseReference rootDatabase;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    RadioButton cau1a;
    String keyt;
    String userid;
    String useranswer = "";
    String quizanswers = "";
    Test s= new Test();
    RadioButton[][] rdbtn = new RadioButton[50][4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keyt = getIntent().getExtras().getString("keyt111");
        userid = getIntent().getExtras().getString("Uid111");
        anhxa();
        Nav();
       // Demo();
        loaduseranswer(keyt,userid);
        loadquizanswer(keyt);
        youranswers();
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
                Log.d("arr[]", rdbtn[i][j].getResources().getResourceEntryName(rdbtn[i][j].getId()));

            }
        }
    }
    public void loaduseranswer(String keyt,String userid) {

        rootDatabase.child("account").child(userid).child("de").child(keyt).child("dapandalam").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("answer"))
                    useranswer = dataSnapshot.child("answer").getValue().toString();
                Toast.makeText(Score.this, useranswer+"   dap an cua user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void loadquizanswer(String keyt) {
        rootDatabase.child("anhvan").child(keyt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("answer"))
                    quizanswers = dataSnapshot.child("answer").getValue().toString();
                Toast.makeText(Score.this, quizanswers+"     8=D---   dap an cua de", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    public void youranswers()
    {
        String useranswerreplace = useranswer.replace("cau", "");
       // Log.d("user", useranswerreplace);
        int index = 0;
        int length = useranswerreplace.length();

        char c;
        char cafter;

        char cquizans;
        char cquizansafter;

        for(int i = 0; i < length; i++)
        {
            String indexString = "";
            String indexStringquiz = "";
            c = useranswerreplace.charAt(i);
            cquizans = quizanswers.charAt(i);
            if(c >= '0' && c <= '9' && cquizans >= '0' && cquizans <= '9')
            {
                indexString += c + "";
                indexStringquiz += cquizans + "";
                cafter = useranswerreplace.charAt(i + 1);
                cquizansafter = quizanswers.charAt(i + 1);
                if (cafter >= '0' && cafter <= '9' && cquizansafter >= '0' && cquizansafter <= '9')
                {
                    indexString += cafter + "";
                    indexStringquiz += cquizansafter + "";
                    i++;
                }
                index = (Integer.valueOf(indexString)) - 1;
               // Log.d("index", indexString + "");
            }
            else if(c >= 'a' && c <= 'd' && useranswerreplace.charAt(i - 1) >= '0' && useranswerreplace.charAt(i -1) <= '9'
                    && cquizans >= 'A' && cquizans <= 'D' && quizanswers.charAt(i - 1) >= '0' && quizanswers.charAt(i -1) <= '9')
            {
                rdbtn[index][(int)c - 97].setChecked(true);
                if(!rdbtn[index][(int)cquizans - 65].isChecked())
                {
                    rdbtn[index][(int)cquizans - 65].setButtonTintList(ColorStateList.valueOf(Color.RED));
                    rdbtn[index][(int)cquizans - 65].setChecked(true);
                }
                for (int j = 0; j < 4; j++)
                {
                    if((int)c - 97 == j || (int)cquizans - 65 == j)
                        continue;
                    rdbtn[index][j].setEnabled(false);
                }
            }
        }
    }
}


