package com.example.dk.onthidh;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Score extends AppCompatActivity {
    private static final String TAG = "Test";
    DrawerLayout drawer;
    String keyt;
    String userid;
    String useranswer;
    String quizanswers;
    Toolbar toolbar;
    NavigationView navigation;
    ArrayList<String> mois;
    MoiAdapter adapter_moi;
    RadioButton[][] rdbtn = new RadioButton[50][4];
    private DatabaseReference rootDatabase;
    private RecyclerView rcvDataMoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        mois = new ArrayList<>();
        adapter_moi = new MoiAdapter(Score.this, mois);
        rcvDataMoi = (RecyclerView) findViewById(R.id.recyclerViewTest);
        rcvDataMoi.setHasFixedSize(true);
        rcvDataMoi.setLayoutManager(new LinearLayoutManager(this));
        rcvDataMoi.setAdapter(adapter_moi);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keyt = getIntent().getExtras().getString("keyt111");
        userid = getIntent().getExtras().getString("Uid111");
        anhxa();
        load(keyt);
        loadnameuser(userid);
        loaduseranswer(keyt, userid);
        Nav();

    }
    public void load(String keyt) {
        rootDatabase.child("anhvan").child(keyt).child("test").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String link = dataSnapshot.getValue().toString();
                if(link!=null){
                    mois.add(link);
                    adapter_moi.notifyDataSetChanged();
                }else{
                    Log.d(TAG,"KHONG CO DU LIEU");
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void anhxa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 4; j++) {
                String quizid = "cau" + (i + 1) + (char) (j + 97);
                int resID = getResources().getIdentifier(quizid, "id", getPackageName());
                rdbtn[i][j] = ((RadioButton) findViewById(resID));
                Log.d("arr[]", rdbtn[i][j].getResources().getResourceEntryName(rdbtn[i][j].getId()));
            }
        }
    }

    public void loaduseranswer(final String keyt, String userid) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("answer")) {
                    useranswer = dataSnapshot.child("answer").getValue().toString();
                   loadquizanswer(keyt);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Score.this, useranswer+"", Toast.LENGTH_SHORT).show();
            }
        };
        rootDatabase.child("account")
                .child(userid)
                .child("de")
                .child(keyt)
                .child("dapandalam")
                .addValueEventListener(valueEventListener);

    }

    public void loadquizanswer(String keyt) {
        rootDatabase.child("monhoc").child("anhvan").child(keyt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("answer")) {
                    quizanswers = dataSnapshot.child("answer").getValue().toString();
                    youranswers();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadnameuser(String userid) {
        rootDatabase.child("account").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    getSupportActionBar().setTitle(dataSnapshot.child("name").getValue().toString());
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
    public void youranswers() {
        Log.d("answer", useranswer + "");
        int index = 0;
        int length = useranswer.length();
        char c;
        char cafter;
        char cquizans;
        char cquizansafter;
        for (int i = 0; i < length; i++) {
            String indexString = "";
            //String indexStringquiz = "";
            c = useranswer.charAt(i);
            cquizans = quizanswers.charAt(i);
            if (c >= '0' && c <= '9' && cquizans >= '0' && cquizans <= '9') {
                indexString += c + "";
                // indexStringquiz += cquizans + "";
                cafter = useranswer.charAt(i + 1);
                cquizansafter = quizanswers.charAt(i + 1);
                if (cafter >= '0' && cafter <= '9' && cquizansafter >= '0' && cquizansafter <= '9') {
                    indexString += cafter + "";
                    //  indexStringquiz += cquizansafter + "";
                    i++;
                }
                index = (Integer.valueOf(indexString)) - 1;
                // Log.d("index", indexString + "");
            } else if (c >= 'a' && c <= 'd' && cquizans >= 'A' && cquizans <= 'D') {
                rdbtn[index][(int) c - 97].setChecked(true);
                if (!rdbtn[index][(int) cquizans - 65].isChecked()) {
                    rdbtn[index][(int) cquizans - 65].setButtonTintList(ColorStateList.valueOf(Color.RED));
                    rdbtn[index][(int) cquizans - 65].setChecked(true);
                }
                for (int j = 0; j < 4; j++) {
                    if ((int) c - 97 == j || (int) cquizans - 65 == j)
                        continue;
                    rdbtn[index][j].setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent mhchinh = new Intent(Score.this, MainActivity.class);
//                        startActivity(mhchinh);
//                        onPause();
                        Toast.makeText(Score.this, "Main", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }
}


