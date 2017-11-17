package com.example.dk.onthidh.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dk.onthidh.Class.Uid;
import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.example.dk.onthidh.MyFile.MyFileViewHolder;
import com.example.dk.onthidh.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

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
    ArrayList<String> keystest;
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
       loadList();
        search();
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
                files.add(model);
                Log.d("loadlist", files.size() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                adapter.notifyDataSetChanged();
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
        rcvData.setAdapter(myAdapterTest);

    }
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    private void loadall(final String nameTest, final ArrayList<MyFile> newList)
    {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref2;
                ref2 = ref1.child("monhoc").child(monhoc);

                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Result will be holded Here
                        for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                            String temp = dsp.child("text").getValue().toString().toLowerCase();
                            String temp2 = nameTest.toLowerCase();
                            String comparetemp = removeDiacriticalMarks(temp);
                            String comparetemp2 = removeDiacriticalMarks(temp2);
                            Log.d("huyllll", temp2 + "");
                            MyFile model = new MyFile();
                            if (temp.contains(temp2) || comparetemp.contains(comparetemp2))
                            {
                                Log.d("huy", model.text + "");
                                Log.d("huyabc", newList.size() + "");
                                Log.d("huyxyz", model.text + "");
                                Log.d("getkey", dsp.getKey());
                                model.text = dsp.child("text").getValue().toString();
                                newList.add(model);
                                adapter.notifyDataSetChanged();
                                adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent= new Intent(ListTest.this,Test.class);
                                        intent.putExtra("keyt",dsp.getRef().getKey());
                                        intent.putExtra("Uid2", uid);
                                        intent.putExtra("monhoc",monhoc);
                                        ListTest.this.startActivity(intent);
                                    }
                                });

                            }
                            adapter.setfilter(newList);
                            rcvData.setAdapter(adapter);
                            rcvData.invalidate();
                            Log.d("count", rcvData.getChildCount() + "");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
    public void anhXa() {
        searchviewww = (MaterialSearchView) findViewById(R.id.materialsearchview);
        keys = new ArrayList<>();
        rcvData = (RecyclerView) findViewById(R.id.recyclerViewImage);
        files = new ArrayList<>();
        adapter = new MyFileAdapter(ListTest.this, files);
       // rcvData.setHasFixedSize(true);
        //Linear
        rcvData.setLayoutManager(new LinearLayoutManager(ListTest.this));
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
                loadall(newText, newList);
                return true;
            }
        });
    }
    private void Nav() {
        //set toolbar thay the cho actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
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
