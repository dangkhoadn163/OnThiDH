package com.example.dk.onthidh.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.example.dk.onthidh.MyFile.MyFileViewHolder;
import com.example.dk.onthidh.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class ListTest extends AppCompatActivity {
    private static final String listTest = "ListTest";
    private MaterialSearchView searchviewww;
    ArrayList<MyFile> files;
    MyFileAdapter adapter;
    ArrayList<String> keys;
    private RecyclerView rcvData;
    String uid,monhoc;
    Toolbar toolbar;
    DatabaseReference rootDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        uid = getIntent().getExtras().getString("Uid");
        monhoc = getIntent().getExtras().getString("monhoc");
        Log.d("Uid1", "onComplete: Uid=" + uid);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        anhXa();
        Nav();

        adapter = new MyFileAdapter(this, files);
//        load();
        loadList();
    }
    public  static final String TAG = ListTest.class.getSimpleName();

    private void loadList() {
        FirebaseRecyclerAdapter<MyFile, MyFileViewHolder> myAdapterTest = new FirebaseRecyclerAdapter<MyFile, MyFileViewHolder>(
                MyFile.class, R.layout.item, MyFileViewHolder.class, rootDatabase.child("monhoc").child(monhoc)
        ) {
            @Override
            protected void populateViewHolder(MyFileViewHolder viewHolder, final MyFile model, int position) {
                final String t = getRef(position).getKey().toString();
                viewHolder.txvKey.setText(t);
                viewHolder.setActionClick(model.text);
                viewHolder.txvTenFile.setText(model.text);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(ListTest.this,Test.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        ListTest.this.startActivity(intent);
                    }
                });
//                Toast.makeText(ListTest.this, t+"", Toast.LENGTH_SHORT).show();

            }
        };
        search();
        rcvData.setAdapter(myAdapterTest);
    }
    public void anhXa() {
        searchviewww = (MaterialSearchView) findViewById(R.id.materialsearchview);
        keys = new ArrayList<>();
        rcvData = (RecyclerView) findViewById(R.id.recyclerViewImage);
        files = new ArrayList<>();
        adapter = new MyFileAdapter(this, files);
        rcvData.setHasFixedSize(true);
        //Linear
        rcvData.setLayoutManager(new LinearLayoutManager(this));
        /*Grid
        rcvData.setLayoutManager(new GridLayoutManager(this,2));*/
        rcvData.setAdapter(adapter);
        keys = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
    }
    private void search() {
        searchviewww.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<MyFile> newList = new ArrayList<MyFile>();
                for (MyFile item : files) {
                    String name = item.text.toString().toLowerCase();
                    Log.d("texttttttttt",name+"ooooooooo");
                    if (name.contains(newText)) {
                        newList.add(item);
                    }
                }
                adapter.setfilter(newList);
                return true;
            }
        });
    }
    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_change);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        MenuItem item = menu.findItem(R.id.Search);
        searchviewww.setMenuItem(item);
        return true;
    }
}
