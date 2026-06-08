package com.example.dk.onthidh.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dk.onthidh.Class.User;
import com.example.dk.onthidh.CustomDialog.ChangepassDiaglog;
import com.example.dk.onthidh.CustomDialog.LogoutDialog;
import com.example.dk.onthidh.CustomDialog.PhotoDialog;
import com.example.dk.onthidh.Fragment.FragmentAccount;
import com.example.dk.onthidh.Fragment.FragmentMain;
import com.example.dk.onthidh.Fragment.FragmentRate;
import com.example.dk.onthidh.Fragment.FragmentReview;
import com.example.dk.onthidh.Fragment.FragmentSupport;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dk.onthidh.CustomDialog.Constant.IMAGE_REQUEST_CODE;
import static com.example.dk.onthidh.CustomDialog.FirebaseController.uploadAvatar;

public class ChooseActivity extends AppCompatActivity {
    String uid;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigation;
    String name, email;
    private AlertDialog.Builder defaultDialog;
    private PhotoDialog customDialog;
    private ChangepassDiaglog changepassDiaglog;
    private LogoutDialog logoutDialog;
    private Context context;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference rootDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //Header-Nav
    private CircleImageView iv_picture;
    private TextView tv_name;
    private TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mAuth = FirebaseAuth.getInstance();
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        context = this;
        Bundle extras = getIntent().getExtras();
        if (extras == null || TextUtils.isEmpty(extras.getString("Uid"))) {
            finish();
            return;
        }
        uid = extras.getString("Uid");
        Log.d("uuuuuuuuu",""+uid);
        anhxa();
        nav();
        loadnameuser(uid);
    }
    private void anhxa() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        navigation = (NavigationView) findViewById(R.id.navview);
        View header = navigation.getHeaderView(0);
        iv_picture = (CircleImageView) header.findViewById(R.id.iv_picture);
        tv_name = (TextView) header.findViewById(R.id.tv_name);
        tv_email = (TextView) header.findViewById(R.id.tv_email);

    }

    public void loadnameuser(String uid) {
        rootDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    name = dataSnapshot.child("name").getValue().toString();
                }
                info();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                info();
            }
        });
    }

    public void info() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // email address, and profile photo Url
            email = user.getEmail();
        }
        updateHeader();
    }


    // Khỏi tạo và mở dialog tùy chỉnh
    private void openCustomDialog() {
        customDialog = new PhotoDialog(ChooseActivity.this);
        customDialog.setTitle("Select action");
        customDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                customDialog.dismiss();
                uploadAvatar(context, uid, imageUri);
            } else if (customDialog != null) {
                customDialog.dismiss();
            }
        }
    }

    public void nav(){
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_profile);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
        updateHeader();

        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomDialog();
            }
        });
        rootDatabase.child("account").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && !TextUtils.isEmpty(user.avatar)) {
                    Picasso.with(ChooseActivity.this).load(user.avatar).into(iv_picture);
                } else {
                    iv_picture.setImageResource(R.mipmap.ic_launcher);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        if(id == R.id.nav_first_fragment)
            classfragment=FragmentMain.class;
        if(id == R.id.nav_second_fragment)
            classfragment=FragmentAccount.class;
        if(id == R.id.nav_third_fragment)
            classfragment=FragmentSupport.class;
        if(id == R.id.nav_four_fragment)
            classfragment=FragmentRate.class;
        if(id == R.id.nav_five_fragment)
        {
            openChangeDialogChangePass();
            drawer.closeDrawers();
        }
        if(id == R.id.nav_six_fragment)
        {
            openLogoutDialog();
            drawer.closeDrawers();
        }
        if(id==R.id.nav_seven_fragment)
            classfragment=FragmentReview.class;
        if (classfragment == null) {
            return;
        }
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
            Log.e("ChooseActivity", "Failed to open menu fragment", e);
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
/*            case R.id.mnLogout:
                mAuth.signOut();
                startActivity(new Intent(ChooseActivity.this, LaunchActivity.class));
                break;*/
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void openChangeDialogChangePass() {
        changepassDiaglog = new ChangepassDiaglog(this);
        changepassDiaglog.show();
    }
    private void openLogoutDialog() {
        logoutDialog = new LogoutDialog(this);
        logoutDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*restart*/
//        finish();
//        startActivity(getIntent());
    }

    private void updateHeader() {
        if (!TextUtils.isEmpty(email)) {
            tv_email.setText(email);
        }
        if (!TextUtils.isEmpty(name)) {
            tv_name.setText(name);
        }
    }
}
