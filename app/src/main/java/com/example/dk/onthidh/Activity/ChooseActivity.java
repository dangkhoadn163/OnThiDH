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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dk.onthidh.Class.User;
import com.example.dk.onthidh.CustomDialog.ChangepassDiaglog;
import com.example.dk.onthidh.CustomDialog.LogoutDialog;
import com.example.dk.onthidh.CustomDialog.PhotoDialog;
import com.example.dk.onthidh.Fragment.Fragment1;
import com.example.dk.onthidh.Fragment.Fragment2;
import com.example.dk.onthidh.Fragment.Fragment3;
import com.example.dk.onthidh.Fragment.Fragment4;
import com.example.dk.onthidh.Fragment.Fragment7;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mAuth = FirebaseAuth.getInstance();
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        uid = getIntent().getExtras().getString("Uid");
        name = "";
        email = "";
        anhxa();
        loadnameuser(uid);


    }
    private void anhxa() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    public void loadnameuser(String uid) {
        rootDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    name = dataSnapshot.child("name").getValue().toString();
                    info();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void info() {
        if (user != null) {
            // email address, and profile photo Url
            email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            nav();
        }
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
        // Nếu nhận <ActivityResult> từ activity CHỌN ẢNH (không phân biệt từ thư viện hay chụp ảnh mới)
        // và kiểm tra có chọn ảnh hay không để xử lý hàm if này
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            imageUri = data.getData();
            // Lấy được tấm ảnh rồi thì mở activity cắt ảnh
            CropImage.activity(imageUri)// thảy cái uri vào intent crop image
                    .setAspectRatio(1, 1)// set tỉ lệ cắt (ở đây cắt avatar nên để tỉ lệ 1:1 cho nó thành hình vuông)
                    .setMaxCropResultSize(500,500) // kích thước cắt tối đa: 500x500 pixel
                    .setMinCropWindowSize(50, 50) // kích thước cắt tối thiểu: 50x50 pixel
                    .setBackgroundColor(R.color.colorWhite) // tự hiểu
                    .start(this); // tự hiểu
        }

        // Nếu nhận <ActivityResult> từ activity CẮT ẢNH
        // và nhận được 1 tấm ảnh đã cắt thì xử lý hàm if này:
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Lấy đường dẫn
                imageUri = result.getUri();
                // Đóng dialog mở lên lúc nãy
                customDialog.dismiss();
                // upload Avatar lên firebase
                uploadAvatar(context, uid, imageUri);// Xem bên class FirebaseController
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // nếu xảy ra lỗi thì cũng đóng dialog luôn:
                customDialog.dismiss();
            }
        }
    }

    public void nav(){
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_profile);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        TextView tv_name= (TextView) findViewById(R.id.tv_name);
        TextView tv_email= (TextView) findViewById(R.id.tv_email);
        final ImageView iv_picture= (ImageView)findViewById(R.id.iv_picture);
        tv_email.setText(email);
        tv_name.setText(name);
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomDialog();
            }
        });
        rootDatabase.child("account").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User();
                user = dataSnapshot.getValue(User.class);
                Picasso.with(ChooseActivity.this).load(user.avatar).into(iv_picture);
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
        if(id==R.id.nav_first_fragment)
            classfragment=Fragment1.class;
        if(id==R.id.nav_second_fragment)
            classfragment=Fragment2.class;
        if(id==R.id.nav_third_fragment)
            classfragment=Fragment3.class;
        if(id==R.id.nav_four_fragment)
            classfragment=Fragment4.class;
        if(id==R.id.nav_five_fragment)
        {
            openChangeDialogChangePass();
            drawer.closeDrawers();
        }
        if(id==R.id.nav_six_fragment)
        {
            openLogoutDialog();
            drawer.closeDrawers();
        }
        if(id==R.id.nav_seven_fragment)
            classfragment=Fragment7.class;
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
}
