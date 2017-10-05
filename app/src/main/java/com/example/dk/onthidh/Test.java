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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.Moi.Moi;
import com.example.dk.onthidh.Moi.MoiAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Test extends AppCompatActivity {
    private TextView tvMinute,tvSecond,hello;
    private Handler handler;
    private Button btnSave;
    private Runnable runnable;
    private DatabaseReference rootDatabase;
    private CountDownTimer countDownTimer;
    private ImageButton imgClock,imgPen;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    RadioGroup[] rdg = new RadioGroup[50];

    private RecyclerView rcvData;


    private RecyclerView recyclerViewImage;
    private ArrayList<Moi> mois;
    private MoiAdapter moiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        anhxa();
        CDTimer();
        Nav();
        radiogroup();

        mois = new ArrayList<>();
//        mois.add(new Moi("https://drive.google.com/drive/u/0/my-drive"));
//        mois.add(new Moi("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png"));
//        mois.add(new Moi("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png"));
//        mois.add(new Moi("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png"));
//        mois.add(new Moi("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png"));
        mois.add(new Moi(R.drawable.test));
        mois.add(new Moi(R.drawable.thpta));
        mois.add(new Moi(R.drawable.thptb));
        mois.add(new Moi(R.drawable.thptc));
        mois.add(new Moi(R.drawable.thptd));
        mois.add(new Moi(R.drawable.thpte));
        moiAdapter = new MoiAdapter(this,mois);

        recyclerViewImage = (RecyclerView) findViewById(R.id.recyclerViewImage);
        recyclerViewImage.setHasFixedSize(true);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImage.setAdapter(moiAdapter);
    }
    private void anhxa(){
        imgClock=(ImageButton)findViewById(R.id.imageClock);
        imgPen=(ImageButton)findViewById(R.id.imagePencil);
        tvMinute = (TextView) findViewById(R.id.txtMinute);
        tvSecond = (TextView) findViewById(R.id.txtSecond);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
//        rdg1=(RadioGroup)findViewById(R.id.rdgcau1);
        for(int i=0; i < 50; i++) {
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
    private void Nav(){
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);


        ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_tracnghiem);
        ab.setTitle("DANGKHOADN");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        navigation=(NavigationView)findViewById(R.id.nvView);
        Button btnSave = (Button)findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < 50; j++) {
                    if((rdg[j].getCheckedRadioButtonId())==-1) {
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
        if(id==android.R.id.home)
            drawer.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }


    private void CDTimer(){
        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
                int temp = (int)millisUntilFinished / 1000;
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


