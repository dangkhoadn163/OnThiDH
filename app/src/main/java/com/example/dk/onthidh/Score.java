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

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    private DatabaseReference rootDatabase;
    String keyt;
    String userid;
    String nameuser;
    String useranswer ;
    String quizanswers;

    RadioButton[][] rdbtn = new RadioButton[50][4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keyt = getIntent().getExtras().getString("keyt111");
        userid = getIntent().getExtras().getString("Uid111");
        anhxa();
        loaduseranswer(keyt,userid);
        loadquizanswer(keyt);
        loadnameuser(userid);
        Nav();
//        youranswers();
    }
    private void anhxa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                if(dataSnapshot.hasChild("answer")) {
                    String ua = dataSnapshot.child("answer").getValue().toString();
                    useranswer=ua;
                    Log.d("uuuaaa",ua);
                    Log.d("uuusss", useranswer);
                }

//                Log.d("useranswer", useranswer + "");
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
                if(dataSnapshot.hasChild("answer")) {
                    String qa = dataSnapshot.child("answer").getValue().toString();
                    quizanswers=qa;
//                Toast.makeText(Score.this, quizanswers+"     dap an cua de", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void loadnameuser(String uid) {
        rootDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("name")) {
                    String nu = dataSnapshot.child("name").getValue().toString();
                    nameuser=nu;
                    Toast.makeText(Score.this, nameuser + "", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_tracnghiem);
        ab.setTitle(nameuser);
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
        //Đây là câu trả lời của người dùng lấy từ firebase
//        String useranswer = "cau1acau2dcau3acau4acau5acau6acau7acau8ccau9acau10dcau11dcau12dcau13c" +
//            "cau14bcau15bcau16acau17ccau18dcau19ccau20bcau21acau22acau23bcau24dcau25bcau26acau27d" +
//            "cau28ccau29bcau30acau31ccau32ccau33dcau34dcau35dcau36dcau37dcau38ccau39bcau40bcau41a" +
//            "cau42dcau43dcau44acau45acau46bcau47dcau48bcau49dcau50a";

        Log.d("useranswer", useranswer + "");
        Log.d("quizanswer", quizanswers + "");
        String useranswerreplace = useranswer.replace("cau", "");
        Log.d("answer", useranswerreplace + "");

        // Đây là đáp án của bài thi lấy từ firebase
//        String quizanswers = "1A2C3A4B5B6D7D8C9A10B11A12D13D14C15C16C17A18A19C20C21D22B23A24C25C26B" +
//                "27D28C29D30B31C32A33B34A35D36D37A38C39A40A41A42D43C44A45B46D47C48C49D50D";

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
            //String indexStringquiz = "";
            c = useranswerreplace.charAt(i);
            cquizans = quizanswers.charAt(i);
            if(c >= '0' && c <= '9' && cquizans >= '0' && cquizans <= '9')
            {
                indexString += c + "";
                // indexStringquiz += cquizans + "";
                cafter = useranswerreplace.charAt(i + 1);
                cquizansafter = quizanswers.charAt(i + 1);
                if (cafter >= '0' && cafter <= '9' && cquizansafter >= '0' && cquizansafter <= '9')
                {
                    indexString += cafter + "";
                    //  indexStringquiz += cquizansafter + "";
                    i++;
                }
                index = (Integer.valueOf(indexString)) - 1;
                // Log.d("index", indexString + "");
            }
            else if(c >= 'a' && c <= 'd' && cquizans >= 'A' && cquizans <= 'D' )
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


