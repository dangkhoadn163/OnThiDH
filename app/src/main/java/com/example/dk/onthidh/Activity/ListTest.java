package com.example.dk.onthidh.Activity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.example.dk.onthidh.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListTest extends AppCompatActivity {
    private static final String listTest = "ListTest";
    private SearchView searchviewww;
    ArrayList<MyFile> files;
    MyFileAdapter adapter;
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
        loadList2();

    }
    public  static final String TAG = ListTest.class.getSimpleName();
    private void loadList2()
    {
        adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MyFile model = adapter.getItem(position);
                if (model == null) {
                    return;
                }

                Intent intent= new Intent(ListTest.this,Test.class);
                intent.putExtra("keyt", model.key);
                intent.putExtra("Uid2", uid);
                intent.putExtra("monhoc",monhoc);
                intent.putExtra("tende", model.text);
                ListTest.this.startActivity(intent);
            }
        });

        rootDatabase.child("account").child(uid).child(monhoc).child("de")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot accountSnapshot) {
                        final Set<String> doneKeys = new HashSet<>();
                        for (DataSnapshot child : accountSnapshot.getChildren()) {
                            doneKeys.add(child.getKey());
                        }

                        rootDatabase.child("monhoc").child(monhoc).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                files.clear();

                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                    String key = dsp.getKey();
                                    if (doneKeys.contains(key) || !dsp.hasChild("text")) {
                                        continue;
                                    }

                                    MyFile model = new MyFile();
                                    model.key = key;
                                    model.text = String.valueOf(dsp.child("text").getValue());
                                    files.add(model);
                                }

                                adapter.setfilter(files);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static String normalizeSearchText(String string) {
        String normalized = Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.replace("đ", "d");
    }

    private void loadSearch(final String nameTest, final ArrayList<MyFile> newList)
    {
        String normalizedQuery = normalizeSearchText(nameTest);
        for (MyFile file : files) {
            if (file == null || file.text == null) {
                continue;
            }
            if (normalizeSearchText(file.text).contains(normalizedQuery)) {
                newList.add(file);
            }
        }
        adapter.setfilter(newList);
    }

    public void anhXa() {
        rcvData = (RecyclerView) findViewById(R.id.recyclerViewImage);
        files = new ArrayList<>();
        adapter = new MyFileAdapter(ListTest.this, files);
        // rcvData.setHasFixedSize(true);
        //Linear
        rcvData.setLayoutManager(new LinearLayoutManager(ListTest.this));
        /*Grid
        rcvData.setLayoutManager(new GridLayoutManager(this,2));*/
        rcvData.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }
    private void search() {
        if (searchviewww == null) {
            return;
        }

        searchviewww.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<MyFile> newList = new ArrayList<MyFile>();
                if(newText.length() != 0)
                {
                    loadSearch(newText, newList);
                }
                else
                {
                    adapter.setfilter(files);
                }
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
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.Search);
        searchviewww = (SearchView) item.getActionView();
        search();
        return true;
    }
}
