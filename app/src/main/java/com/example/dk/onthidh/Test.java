package com.example.dk.onthidh;

import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class Test extends AppCompatActivity {
    private static final String TAG = "Test";
    DrawerLayout drawer;
    LinearLayout lnl;
    Toolbar toolbar;
    NavigationView navigation;
    private RadioGroup[] rdg = new RadioGroup[50];
    String answer;
    String keyt;
    String scored;
    private String saveanswers = "";
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_test);
        keyt = getIntent().getExtras().getString("keyt");
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
        autocheck();
        Click();

//        lnl = (LinearLayout) findViewById(R.id.linearlayout);
//        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), );
//        Bitmap blurredBitmap = BlurBuilder.blur(this, originalBitmap);
//        lnl.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
//        lnl.getBackground().setAlpha(100);
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
    public String getterscored(){
        return scored;
    }

    public void loadanswer(String keyt) {

        rootDatabase.child("anhvan").child(keyt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("answer"))
                    answer = dataSnapshot.child("answer").getValue().toString();
                Toast.makeText(Test.this, answer+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void loadsaveanswers(){
        rootDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               People people = new People(saveanswers,scored);
               rootDatabase.child("People").child("de").child(keyt).child("dapandalam").setValue(people);
                Toast.makeText(Test.this, saveanswers+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    void autocheck()
//    {
//        for(int i = 0; i < 50; i++)
//        {
//            for (int j = 0; j < 3; j++) {
//                String quizid = "cau" + (i + 1) +(char)(97 + j);
//                int resID = getResources().getIdentifier(quizid, "id", getPackageName());
//                rdg[i].check(resID);
////            Toast.makeText(Test.this, quizid,Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }
        void autocheck()
        {
            for(int i = 0; i < 50; i++)
            {
                Random rand = new Random();
                int r = rand.nextInt(4) + 97;

                String quizid = "cau" + (i + 1) +(char)r;
                int resID = getResources().getIdentifier(quizid, "id", getPackageName());
                rdg[i].check(resID);
        //            Toast.makeText(Test.this, quizid,Toast.LENGTH_SHORT).show();


            }
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
    private void Click(){
        imgClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Test.this,Score.class));
            }
        });
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
                String idRdb = "";
                for (int j = 0; j < 50; j++) {
                    if ((rdg[j].getCheckedRadioButtonId()) == -1) {
                        Toast.makeText(Test.this, "Bạn chưa đánh câu " + (j + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.d("ID", idRdb );
                int lengthresult = answer.length();
                String temp = "";
                int index = 0;
                for(int j = 0; j < lengthresult; j++)
                {
                    char c = answer.charAt(j);
                    temp = temp.concat(c + "");
                    if(c >= 'A' && c <= 'D')
                    {
                        Log.d("Temp", temp);
                        boolean checkresult = rdg[index]
                                .getResources()
                                .getResourceEntryName(rdg[index]
                                        .getCheckedRadioButtonId()).toLowerCase().contains(temp.toLowerCase());
                        Log.d("Result", temp + ":" + checkresult + "");
                        saveanswers = saveanswers.concat(rdg[index].getResources().getResourceEntryName(rdg[index].getCheckedRadioButtonId()) + "");

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
                scored= score+"";
                loadsaveanswers();
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


