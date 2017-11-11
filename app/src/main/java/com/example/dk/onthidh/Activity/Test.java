package com.example.dk.onthidh.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.Class.Uid;
import com.example.dk.onthidh.CustomDialog.DialogStart;
import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.example.dk.onthidh.R;
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
    private DialogStart dialognew;
    ProgressDialog dialogstart;
    Toolbar toolbar;
    NavigationView navigation;
    private RadioGroup[] rdg = new RadioGroup[50];
    String answers;
    String keyt;
    String scored;
    String userid;
    String monhoc;
    int clock;
    private String mstr;
    private String sstr;
    private int countMinute;
    private int countSecond;
    private byte countquiz;
    private int time;
    private String saveanswers = "";
    private boolean checkClickDialog = false;
    ArrayList<String> mois;
    MoiAdapter adapter_moi;
    private TextView tvMinute, tvSecond;
    private Handler handler;
    private Button btnSave;
    private Runnable runnable;
    private DatabaseReference rootDatabase;
    private CountDownTimer countDownTimer;
    private ImageButton imgClock, imgPen;
    private RecyclerView rcvDataMoi;
    private BigDecimal score = new BigDecimal("0.0");
    private BigDecimal scoreperanswer = new BigDecimal("0.0");
    ScrollView scrollView;
    ProgressBar progressBar;
    CountDownTimer timercheck,timerstart;
    AlertDialog.Builder dialogBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        keyt = getIntent().getExtras().getString("keyt");
        userid = getIntent().getExtras().getString("Uid2");
        monhoc = getIntent().getExtras().getString("monhoc");
        clock=1;
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        dialognew= new DialogStart(Test.this);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        anhxa();
        initquiztimescore(monhoc);
        countup();
        CDTimer();
        openCustomDialog();
        radiogroup();
        mois = new ArrayList<>();
        adapter_moi = new MoiAdapter(Test.this, mois);
        rcvDataMoi = (RecyclerView) findViewById(R.id.recyclerViewTest);
        rcvDataMoi.setHasFixedSize(true);
        rcvDataMoi.setLayoutManager(new LinearLayoutManager(this));
        rcvDataMoi.setAdapter(adapter_moi);
        loadnameuser(userid);
        load(keyt);
        loadanswer(keyt);
        autocheck();
        Nav();
        ClickClock();
    }

    public void load(String keyt) {
        rootDatabase.child("monhoc").child(monhoc).child(keyt).child("test").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String link = dataSnapshot.getValue().toString();
                if(link!=null){
                    mois.add(link);
                    adapter_moi.notifyDataSetChanged();
                    Log.d("abc", adapter_moi.getItemId(0) + "");
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

        rootDatabase.child("monhoc").child(monhoc).child(keyt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("answer"))
                    answers = dataSnapshot.child("answer").getValue().toString();

//                Toast.makeText(Test.this, answer+"", Toast.LENGTH_SHORT).show();
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
                Uid uid = new Uid(saveanswers,scored);
                rootDatabase.child("account").child(userid).child(monhoc).child("de").child(keyt).child("dapandalam").setValue(uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void autocheck()
    {
        for(int i = 0; i < countquiz; i++)
        {
            Random rand = new Random();
            int r = rand.nextInt(4) + 97;
            String quizid = "cau" + (i + 1) +(char)r;
            int resID = getResources().getIdentifier(quizid, "id", getPackageName());
            rdg[i].check(resID);
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
    private void ClickClock(){
        imgClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock= clock*-1;
                if (clock==1){
                    tvMinute.setVisibility(View.INVISIBLE);
                    tvSecond.setVisibility(View.INVISIBLE);
                }
                else if(clock==-1) {
                    tvMinute.setVisibility(View.VISIBLE);
                    tvSecond.setVisibility(View.VISIBLE);
                }
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
    private void dialog(){
        new AlertDialog.Builder(this)
                .setTitle("Nộp bài thi")
                .setMessage("Bạn có chắc chắn muốn nộp bài thi không?")
                .setNegativeButton("Không", null)
                .setPositiveButton("Nộp bài", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Test.this, "Điểm của bạn là: " + score, Toast.LENGTH_SHORT).show();
//                        Log.d("Score", score + "");
                        loadsaveanswers();
                        Intent intent= new Intent(Test.this,Score.class);
                        intent.putExtra("keyt111",keyt);
                        intent.putExtra("Uid111", userid);
                        intent.putExtra("monhoc",monhoc);
                        Test.this.startActivity(intent);
                    }
                }).create().show();
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
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        navigation = (NavigationView) findViewById(R.id.nvView);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idRdb = "";
                for (int j = 0; j < countquiz; j++) {
                    if ((rdg[j].getCheckedRadioButtonId()) == -1)
                    {
                        Toast.makeText(Test.this, "Bạn chưa đánh câu " + (j + 1),
                                Toast.LENGTH_SHORT).show();
                        scrollView.smoothScrollTo(0, rdg[j].getTop());
                        return;
                    }
                }
                if(countMinute <= 4){
                    Toast.makeText(Test.this, "Còn 5 phút hết giờ làm bài!!! \n" +
                            "Bạn sẽ không được nộp bài kể từ thời gian này !", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog();
                Log.d("ID", idRdb );
                int lengthresult = answers.length();
                String temp = "";
                int index = 0;
                for(int j = 0; j < lengthresult; j++)
                {
                    char c = answers.charAt(j);
                    temp = temp.concat(c + "");
                    if(c >= 'A' && c <= 'D')
                    {
                        Log.d("Temp", temp);
                        boolean checkresult = rdg[index]
                                .getResources()
                                .getResourceEntryName(rdg[index]
                                        .getCheckedRadioButtonId()).toLowerCase().contains(temp.toLowerCase());
                        Log.d("Result", temp + ":" + checkresult + "");
                        saveanswers = saveanswers.concat(rdg[index].getResources()
                                .getResourceEntryName(rdg[index].getCheckedRadioButtonId()) + "");
                        if(checkresult)
                        {
                            score = score.add(scoreperanswer);
                            Log.d("Scorestep", score + "");
                        }
                        temp = "";
                        index++;
                    }
                }
                saveanswers = saveanswers.replace("cau", "");
                scored= score + "";
                drawer.closeDrawer(GravityCompat.START);
            }
        });
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
    private void timeup(){
        Toast.makeText(Test.this, "Đã hết giờ làm bài", Toast.LENGTH_SHORT).show();
        int lengthresult = answers.length();
        String temp = "";
        int index = 0;
        for(int j = 0; j < lengthresult; j++)
        {
            char c = answers.charAt(j);
            temp = temp.concat(c + "");
            if(c >= 'A' && c <= 'D')
            {
                Log.d("Temp", temp);
                boolean checkresult = false;
                //Log.d("Result", temp + ":" + checkresult + "");
                if(rdg[index].getCheckedRadioButtonId() == -1)
                {
                    saveanswers = saveanswers.concat("cau" + (index + 1) + "e");
                    Log.d("timeup1", saveanswers);
                    checkresult = false;
                }
                else
                {
                    saveanswers = saveanswers.concat(rdg[index].getResources()
                            .getResourceEntryName(rdg[index].getCheckedRadioButtonId()) + "");
                    checkresult = rdg[index]
                            .getResources()
                            .getResourceEntryName(rdg[index]
                                    .getCheckedRadioButtonId()).toLowerCase().contains(temp.toLowerCase());
                }

                if(checkresult)
                {
                    score = score.add(scoreperanswer);
                    Log.d("Scorestep", score + "");
                }
                temp = "";
                index++;
            }
        }
        saveanswers = saveanswers.replace("cau", "");
        Log.d("timeup", saveanswers);
        scored= score+"";
        Log.d("score0", scored);
        loadsaveanswers();
    }
    private void initquiztimescore(String monhoc)
    {
        if(monhoc.equals("anhvan"))
        {
            time = 3600000;
            countquiz = 50;
            scoreperanswer = new BigDecimal("0.2");
        }
        else if(monhoc.equals("vatly") || monhoc.equals("hoahoc"))
        {
            time = 3000000;
            countquiz = 40;
            scoreperanswer = new BigDecimal("0.25");
        }
        for(int i = countquiz; i < 50; i++)
        {
            for(int j = 0; j <= 4; j++)
            {
                rdg[i].getChildAt(j).setEnabled(false);
            }
        }
        mstr = getString(R.string.minute, (time / 1000) / 60);
        sstr = getString(R.string.second, (time / 1000) % 60);
        tvMinute.setText("" + mstr);
        tvSecond.setText(":" + sstr);

    }
    private void CDTimer() {
        timerstart = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set dethi to edittext
                int temp = (int) millisUntilFinished / 1000;
                countMinute = temp / 60;
                countSecond = temp % 60;
                mstr = getString(R.string.minute, countMinute);
                sstr = getString(R.string.second, countSecond);
                tvMinute.setText("" + mstr);
                tvSecond.setText(":" + sstr);
                if(countMinute == 5 && countSecond == 0){
                    Toast.makeText(Test.this, "Còn 5 phút hết giờ làm bài !", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFinish() {
                mstr = (getString(R.string.minute, 0));
                sstr = (getString(R.string.second, 0));
                tvMinute.setText("" + mstr);
                tvSecond.setText(":" + sstr);
                timeup();
                Toast.makeText(Test.this, "Điểm của bạn là: " + score, Toast.LENGTH_SHORT).show();
//                        Log.d("Score", score + "");
                Intent intent= new Intent(Test.this,Score.class);
                intent.putExtra("keyt111",keyt);
                intent.putExtra("Uid111", userid);
                intent.putExtra("monhoc",monhoc);
                Test.this.startActivity(intent);
            }
        };
    }
    private void countup() {
        timercheck = new CountDownTimer(time,  200) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set dethi to edittext
                if (adapter_moi.getter()) {
                    dialognew.pbstart.setVisibility(View.GONE);
                    dialognew.txvTitle.setText("Bạn đã sẵn sàng thi chưa?");
                    dialognew.txvThoat.setVisibility(View.VISIBLE);
                    dialognew.txvThi.setVisibility(View.VISIBLE);

                    if(checkClickDialog)
                    {
                        timercheck.cancel();
                    }
                    if(!dialognew.isShowing() && !checkClickDialog)
                    {
                        timercheck.cancel();
                        timerstart.start();
                    }

                    Log.d("cd", millisUntilFinished + "");
                }
                else
                {
                    if(!dialognew.getCheckBack() && dialognew.pbstart.isShown())
                    {

                        Toast.makeText(Test.this, "         Đang tải dữ liệu.\n " +
                                "Vui lòng chờ trong giây lát!", Toast.LENGTH_SHORT).show();
                        dialognew.setCheckBack(true);
                        dialognew.txvTitle.setText("Vui lòng chờ trong giây lát!!!");
                    }
                }
            }
            public void onFinish() {

            }
        }.start() ;
    }
    // Khỏi tạo và mở dialog tùy chỉnh
    private void openCustomDialog() {
         dialognew.txvThoat.setVisibility(View.INVISIBLE);
         dialognew.txvThi.setVisibility(View.INVISIBLE);
         dialognew.txvThi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    checkClickDialog = true;
                    dialognew.dismiss();
                    timerstart.start();
            }
        });
        dialognew.txvThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAffinity(Test.this);
            }
        });
        dialognew.show();
        dialognew.dialogListener();
    }

    @Override
    public void onBackPressed()
    {
        dialogBack = new AlertDialog.Builder(this);
        dialogBack.setTitle("Thoát ứng dụng?") ;
        dialogBack.setMessage("Bạn phải nộp bài hay đợi hết thời gian mới được thoát nhé !");
        dialogBack.setNegativeButton(":)", null);
        dialogBack.create().show();
    }
}



