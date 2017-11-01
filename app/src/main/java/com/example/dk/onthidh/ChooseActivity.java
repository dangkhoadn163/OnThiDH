package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ChooseActivity extends AppCompatActivity {
    String uid;
    String monhoc;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    private FirebaseAuth mAuth;
    private Button btnmath, btnenglish, btnbiology, btnchemistry, btnphysic, btnedu, btnhistoty, btngeography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mAuth = FirebaseAuth.getInstance();
        uid = getIntent().getExtras().getString("Uid");
        anhxa();
//        anhvan();
//        vatly();
        nav();
    }

    private void anhxa() {
/*        btnenglish = (Button) findViewById(R.id.btn_english);
        btnmath = (Button) findViewById(R.id.btn_math);
        btnbiology = (Button) findViewById(R.id.btn_biology);
        btnchemistry = (Button) findViewById(R.id.btn_chemistry);
        btnphysic = (Button) findViewById(R.id.btn_physic);
        btnedu = (Button) findViewById(R.id.btn_education);
        btnhistoty = (Button) findViewById(R.id.btn_history);
        btngeography = (Button) findViewById(R.id.btn_geography);*/
        drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        toolbar=(Toolbar)findViewById(R.id.toolbar_main);
    }

    public void anhvan() {
        btnenglish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                monhoc = "anhvan";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                ChooseActivity.this.startActivity(intent);
            }
        });
    }

    public void vatly() {
        btnphysic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                monhoc = "vatly";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                ChooseActivity.this.startActivity(intent);
            }
        });
    }
    public void nav(){
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_profile);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        navigation=(NavigationView)findViewById(R.id.navview);
        navigation.setCheckedItem(R.id.nav_first_fragment);
        xulychonmenu(navigation.getMenu().getItem(0));
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                xulychonmenu(menuItem);
                return false;
            }
        });
    }
    void xulychonmenu(MenuItem menuItem)
    {
        int id=menuItem.getItemId();
        Fragment fragment=null;
        Class classfragment=null;
        if(id==R.id.nav_first_fragment)
            classfragment=Fragment1.class;
        if(id==R.id.nav_second_fragment)
            classfragment=Fragment2.class;
        if(id==R.id.nav_third_fragment)
            classfragment=Fragment3.class;
        if(id==R.id.nav_four_fragment)
            classfragment=Fragment4.class;
        try {
            fragment=(Fragment)classfragment.newInstance();

            FragmentManager fmanager= getSupportFragmentManager();
            fmanager.beginTransaction()
                    .replace(R.id.flContent,fragment)
                    .commit();
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            drawer.closeDrawer(GravityCompat.START);
        }catch(Exception e) {
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnLogout:
                mAuth.signOut();
                startActivity(new Intent(ChooseActivity.this, LaunchActivity.class));
                break;
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
