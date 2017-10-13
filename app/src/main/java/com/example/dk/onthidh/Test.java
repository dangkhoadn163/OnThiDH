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

import com.example.dk.onthidh.FolderMoi.Moi;
import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Test extends AppCompatActivity {
    private static final String TAG = "Test";
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    private RadioGroup[] rdg = new RadioGroup[50];
    String keydatabase;
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
    private BigDecimal score = new BigDecimal("0.0");
    private BigDecimal scoreperanswer = new BigDecimal("0.2");
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
           /* keydatabase = getIntent().getStringExtra("khóa");
            Toast.makeText(this, keydatabase+"", Toast.LENGTH_SHORT).show();*/
        load(keyt);

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
                String result = "1A2C3A4B5B6D7D8C9A10B11A12D13D14C15C16C17A18A19C20C21D22B23A24C25C26B27D28C29D30B31C32A33B34A35D36D37A38C39A40A41A42D43C44A45B46D47C48C49D50D";
                String idRdb = "";
                for (int j = 0; j < 50; j++) {
                    if ((rdg[j].getCheckedRadioButtonId()) == -1) {
                        Toast.makeText(Test.this, "Bạn chưa đánh câu " + (j + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //Log.d("ID", idRdb );
                int lengthresult = result.length();
                String temp = "";
                int index = 0;
                for(int j = 0; j < lengthresult; j++)
                {
                    char c = result.charAt(j);
                    temp = temp.concat(c + "");
                    if(c >= 'A' && c <= 'D')
                    {
                        Log.d("Temp", temp);
                        boolean checkresult = rdg[index]
                                .getResources()
                                .getResourceEntryName(rdg[index]
                                        .getCheckedRadioButtonId()).toLowerCase().contains(temp.toLowerCase());
                        Log.d("Result", temp + ":" + checkresult + "");
                        if(checkresult)
                        {
                            score = score.add(scoreperanswer);
                            Log.d("Scorestep", score + "");
                        }
                        temp = "";
                        index++;
                    }
                }
                Toast.makeText(Test.this, "Điểm của bạn là: " + score, Toast.LENGTH_SHORT).show();
                Log.d("Score", score + "");
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


