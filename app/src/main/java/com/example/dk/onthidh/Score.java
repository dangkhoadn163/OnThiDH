package com.example.dk.onthidh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
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
    String monhoc;
    String scoreuser;
    private int countquiz = 0;
    Toolbar toolbar;
    NavigationView navigation;
    ArrayList<String> mois;
    MoiAdapter adapter_moi;
    RadioButton[][] rdbtn = new RadioButton[50][4];
    private DatabaseReference rootDatabase;
    private RecyclerView rcvDataMoi;
    private TextView tvscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        tvscore= (TextView)findViewById(R.id.tv_score);
        mois = new ArrayList<>();
        adapter_moi = new MoiAdapter(Score.this, mois);
        rcvDataMoi = (RecyclerView) findViewById(R.id.recyclerViewTest);
        rcvDataMoi.setHasFixedSize(true);
        rcvDataMoi.setLayoutManager(new LinearLayoutManager(this));
        rcvDataMoi.setAdapter(adapter_moi);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keyt = getIntent().getExtras().getString("keyt111");
        userid = getIntent().getExtras().getString("Uid111");
        monhoc = getIntent().getExtras().getString("monhoc");
        anhxa();
        inintquiz(monhoc);
        load(keyt);
       // loadnameuser(userid);
        loaduseranswer(keyt, userid);
        //Nav();

    }
    public void load(String keyt) {
        rootDatabase.child("monhoc").child(monhoc).child(keyt).child("test").addChildEventListener(new ChildEventListener() {
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
        for (int i = 0; i < 50; i++)
        {
            for (int j = 0; j < 4; j++)
            {
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
        rootDatabase.child("account").child(userid).child(monhoc).child("de").child(keyt).child("dapandalam").addValueEventListener(valueEventListener);

    }

    public void loadquizanswer(final String keyt) {
        rootDatabase.child("monhoc").child(monhoc).child(keyt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("answer")) {
                    quizanswers = dataSnapshot.child("answer").getValue().toString();
                    loadscore(keyt,userid);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void loadscore(String keyt, final String userid) {
        rootDatabase.child("account").child(userid).child(monhoc).child("de").child(keyt).child("dapandalam").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("score")) {
                    scoreuser = dataSnapshot.child("score").getValue().toString();
                    scoreformat();
                    loadnameuser(userid);
                    Nav();
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
        Button btnchitiet = (Button) findViewById(R.id.btnChitiet);
        TextView tvscore= (TextView) findViewById(R.id.txv_score);
        tvscore.setText(countrightanswer() + "/" + countquiz);
        youranswers();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnSetting:
                Intent intent= new Intent(Score.this,ChooseActivity.class);
//                intent.putExtra("monhoc",monhoc);
                intent.putExtra("Uid", userid);
                Score.this.startActivity(intent);
                break;
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void scoreformat()
    {
        if((int)(Float.valueOf(scoreuser) * 10) == (Float.valueOf(scoreuser) * 10))
        {
            tvscore.setText(String.format("%.1f", Float.valueOf(scoreuser)) + "/10");
        }
        else
        {
            tvscore.setText(String.format("%.2f", Float.valueOf(scoreuser)) + "/10");
        }
    }
    private void inintquiz(String monhoc)
    {
        if(monhoc.equals("anhvan"))
        {
            countquiz = 50;
        }
        else if(monhoc.equals("vatly") || monhoc.equals("hoahoc"))
        {
            countquiz = 40;
        }
        for(int i = countquiz; i < 50; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                rdbtn[i][j].setEnabled(false);
            }
        }
    }
    private int countrightanswer()
    {
        return (int)(Float.valueOf(scoreuser) / (10.0 / countquiz));
    }
    public void youranswers()
    {
        Log.d("answer", quizanswers + "");
        int index = 0;
        int length = useranswer.length();
        char c;
        char cafter;
        char cquizans;
        char cquizansafter;
        for (int i = 0; i < length; i++)
        {
            String indexString = "";
            //String indexStringquiz = "";
            c = useranswer.charAt(i);
            cquizans = quizanswers.charAt(i);
            if (c >= '0' && c <= '9' && cquizans >= '0' && cquizans <= '9')
            {
                indexString += c + "";
                cafter = useranswer.charAt(i + 1);
                cquizansafter = quizanswers.charAt(i + 1);
                if (cafter >= '0' && cafter <= '9' && cquizansafter >= '0' && cquizansafter <= '9')
                {
                    indexString += cafter + "";
                    i++;
                }
                index = (Integer.valueOf(indexString)) - 1;
            }
            else if (c >= 'a' && c <= 'd' && cquizans >= 'A' && cquizans <= 'D')
            {
                rdbtn[index][(int)c - 97].setChecked(true);
                if (!rdbtn[index][(int)cquizans - 65].isChecked())
                {
                    rdbtn[index][(int)cquizans - 65].setButtonTintList(ColorStateList.valueOf(Color.RED));
                    rdbtn[index][(int)cquizans - 65].setChecked(true);
                }
                for (int j = 0; j < 4; j++)
                {
                    if ((int)c - 97 == j || (int)cquizans - 65 == j)
                        continue;
                    rdbtn[index][j].setEnabled(false);
                }
            }
            else if(c == 'e')
            {
                for (int j = 0; j < 4; j++)
                {
                    rdbtn[index][j].setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng?")
                .setMessage("Bạn có chắc chắn muốn thoát ứng dụng?")
                .setNegativeButton("Không", null)
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.finishAffinity(Score.this);
                    }
                }).create().show();
    }
}


