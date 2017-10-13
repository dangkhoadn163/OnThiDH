package com.example.dk.onthidh;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Test extends AppCompatActivity {
    private static final String TAG = "Test";
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    RadioGroup[] rdg = new RadioGroup[50];
    String answer;
    ArrayList<String> mois;
    MoiAdapter adapter_moi;
    private TextView tvMinute, tvSecond;
    private Handler handler;
    private Button btnSave;
    private Runnable runnable;
    private DatabaseReference rootDatabase;
    private CountDownTimer countDownTimer;
    private ImageButton imgClock, imgPen;
    //
    private RecyclerView rcvDataMoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String keyt = getIntent().getExtras().getString("keyt");
//        Toast.makeText(this, "" + keyt, Toast.LENGTH_SHORT).show();
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        anhxa();
        CDTimer();
        Nav();
        radiogroup();
        mois = new ArrayList<>();
        adapter_moi = new MoiAdapter(Test.this, mois);
        rcvDataMoi = (RecyclerView) findViewById(R.id.recyclerViewTest);
        rcvDataMoi.setHasFixedSize(true);
        rcvDataMoi.setLayoutManager(new LinearLayoutManager(this));
        rcvDataMoi.setAdapter(adapter_moi);
        load(keyt);
        loadanswer(keyt);


    }

    public void load(String keyt) {
        rootDatabase.child("anhvan").child(keyt).child("test").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String link = dataSnapshot.getValue().toString();
                if(link!=null){
                    mois.add(link);
                    adapter_moi.notifyDataSetChanged();
                    Log.d(TAG,link);
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
    public void loadanswer(String keyt) {
        rootDatabase.child("anhvan").child(keyt).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*String link = dataSnapshot.getValue().toString();
                if(link!=null){
                    mois.add(link);
                    adapter_moi.notifyDataSetChanged();
                    Log.d(TAG,link);
                }else{
                    Log.d(TAG,"KHONG CO DU LIEU");
                }*/
                Log.d(TAG,dataSnapshot.getValue().toString());
                answer=dataSnapshot.getValue().toString();
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
        imgClock = (ImageButton) findViewById(R.id.imageClock);
        imgPen = (ImageButton) findViewById(R.id.imagePencil);
        tvMinute = (TextView) findViewById(R.id.txtMinute);
        tvSecond = (TextView) findViewById(R.id.txtSecond);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        rdg1=(RadioGroup)findViewById(R.id.rdgcau1);
        for (int i = 0; i < 50; i++) {
            String quizid = "rdgcau" + (i + 1);
            int resID = getResources().getIdentifier(quizid, "id", getPackageName());
            rdg[i] = ((RadioGroup) findViewById(resID));
//            Toast.makeText(Test.this, quizid,Toast.LENGTH_SHORT).show();
        }
    }

    private void radiogroup() {
        for (int j = 0; j < 50; j++) {
            final int finalJ = j;
            rdg[j].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override

                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    //rdg[finalJ].getResources().getResourceEntryName(rdg[finalJ].getCheckedRadioButtonId());
//                            Toast.makeText(Test.this, rdg[finalJ]
//                                            .getResources()
//                                            .getResourceEntryName(rdg[finalJ]
//                                            .getCheckedRadioButtonId()) + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);


        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_tracnghiem);
        ab.setTitle("DANGKHOADN");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        navigation = (NavigationView) findViewById(R.id.nvView);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < 50; j++) {
                    if ((rdg[j].getCheckedRadioButtonId()) == -1) {
                        Toast.makeText(Test.this, "ban chua danh cau " + (j + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Toast.makeText(Test.this, rdg[j]
//                                    .getResources()
//                                    .getResourceEntryName(rdg[j]
//                                    .getCheckedRadioButtonId()) + "", Toast.LENGTH_SHORT).show();
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
//        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                xulychonmenu(menuItem);
//
//                return false;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//noinspectionSimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == android.R.id.home)
            drawer.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }


    private void CDTimer() {
        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set dethi to edittext
                int temp = (int) millisUntilFinished / 1000;
                int h = temp / 60;
                int s = temp % 60;
                tvMinute.setText(" " + h);
                tvSecond.setText(":" + s);
            }

            public void onFinish() {
                tvSecond.setText("done!");
            }

        }.start();
    }

}


