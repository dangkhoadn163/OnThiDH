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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.Normalizer;
import java.util.ArrayList;

public class ListOld extends AppCompatActivity {
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
        setContentView(R.layout.activity_list_old);
        uid = getIntent().getExtras().getString("Uid");
        monhoc = getIntent().getExtras().getString("monhoc");
        Log.d("Uid1", "onComplete: Uid=" + uid);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        anhXa();
        Nav();
//        loadList();
        loadOld();
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
                //viewHolder.txvKey.setText(t);
                viewHolder.setActionClick(model.text);
                viewHolder.txvTenFile.setText(model.text);
                files.add(model);
                Log.d("loadlist", files.size() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                adapter.notifyDataSetChanged();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(ListOld.this,Test.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        intent.putExtra("tende", model.text);
                        ListOld.this.startActivity(intent);
                    }
                });
//                Toast.makeText(ListTest.this, t+"", Toast.LENGTH_SHORT).show();
            }
        };
        rcvData.setAdapter(myAdapterTest);

    }
    private void loadOld() {
        FirebaseRecyclerAdapter<MyFile, MyFileViewHolder> myAdapterTest = new FirebaseRecyclerAdapter<MyFile, MyFileViewHolder>(
                MyFile.class, R.layout.item, MyFileViewHolder.class, rootDatabase.child("account").child(uid).child(monhoc).child("de")
        ) {
            @Override
            protected void populateViewHolder(final MyFileViewHolder viewHolder, final MyFile model, int position) {
                final String t = getRef(position).getKey().toString();
                viewHolder.txvKey.setText(t);
                //viewHolder.setActionClick(model.text);
                rootDatabase.child("account").child(uid).child(monhoc).child("de").child(t).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("nametest")) {
                            model.text = dataSnapshot.child("nametest").getValue().toString();
                            viewHolder.setActionClick(model.text);
                            viewHolder.txvTenFile.setText(model.text);
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent= new Intent(ListOld.this,Score.class);
                                    intent.putExtra("keyt222",t);
                                    intent.putExtra("Uid222", uid);
                                    intent.putExtra("monhoc2",monhoc);
                                    intent.putExtra("tende", model.text);
                                    ListOld.this.startActivity(intent);
                                }
                            });
                            Log.d("huy", model.text);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                files.add(model);
                Log.d("loadOld", t + "");
                adapter.notifyDataSetChanged();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(ListOld.this,Score.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        intent.putExtra("tende", model.text);
                        Log.d("tende", model.text);
                        ListOld.this.startActivity(intent);
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

    public static String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c > '\u007F')
            {
                sb.append(c);
                Log.d("char", c + "");
            }
            //sb.append(c);

        }
        return sb.toString();
    }
    private void loadAll(final String nameTest, final ArrayList<MyFile> newList)
    {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref2;
        ref2 = ref1.child("account").child(uid).child(monhoc).child("de");

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                    MyFile model = new MyFile();
                    String temp = dsp.child("text").getValue().toString().toLowerCase();
                    String temp2 = nameTest.toLowerCase();
                    String comparetemp = removeDiacriticalMarks(temp);
                    String comparetemp2 = removeDiacriticalMarks(temp2);

                    String accentTemp2 = flattenToAscii(temp2).toString();
                    int nTemp2 = accentTemp2.length();
                    boolean check = false;

                    String replaceD = comparetemp.replaceAll("đ", "d");
                    String replaceD2 = comparetemp2.replaceAll("đ", "d");
                    Log.d("huy", replaceD + "");
                    if(replaceD.contains(replaceD2) && nTemp2 != 0 && !check)
                    {
                        int pos = replaceD.indexOf(replaceD2);
                        String sb = temp.substring(pos, temp2.length() + pos);
                        String accentTemp = flattenToAscii(sb).toString();
                        int nTemp = accentTemp.length();
                        Log.d("sb", sb);
                        Log.d("accentTemp", accentTemp);
                        Log.d("accentTemp2", accentTemp2);
                        if(accentTemp.contains(accentTemp2) && !check)
                        {
                            check = true;
                            model.text = dsp.child("text").getValue().toString();
                            newList.add(model);
                            adapter.notifyDataSetChanged();
                            adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent= new Intent(ListOld.this,Score.class);
                                    intent.putExtra("keyt",dsp.getRef().getKey());
                                    intent.putExtra("Uid2", uid);
                                    intent.putExtra("monhoc",monhoc);
                                    ListOld.this.startActivity(intent);
                                }
                            });
                        }
                        else if(!accentTemp.contains(accentTemp2) && !check)
                        {
                            //int length = nTemp2 < nTemp ? nTemp2 : nTemp;
                            accentTemp = accentTemp.replaceAll("̉đ", "đ");
                            nTemp = accentTemp.length();
//                            nTemp2 = accentTemp2.length();
                            for(int i = 0; i < nTemp2; i++)
                            {
                                for(int j = 0; j < nTemp; j++)
                                {
                                    if (accentTemp2.charAt(i) == accentTemp.charAt(j) && !check)
                                    {
                                        check = true;
                                        model.text = dsp.child("text").getValue().toString();
                                        newList.add(model);
                                        adapter.notifyDataSetChanged();
                                        adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent intent = new Intent(ListOld.this, Score.class);
                                                intent.putExtra("keyt", dsp.getRef().getKey());
                                                intent.putExtra("Uid2", uid);
                                                intent.putExtra("monhoc", monhoc);
                                                ListOld.this.startActivity(intent);
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if ((temp.contains(temp2) || (replaceD.contains(replaceD2) && temp2.contains(replaceD2))) && !check)
                    {
                        model.text = dsp.child("text").getValue().toString();
                        newList.add(model);
                        adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent= new Intent(ListOld.this,Score.class);
                                intent.putExtra("keyt",dsp.getRef().getKey());
                                intent.putExtra("Uid2", uid);
                                intent.putExtra("monhoc",monhoc);
                                ListOld.this.startActivity(intent);
                            }
                        });
                    }
                    adapter.setfilter(newList);
                    rcvData.setAdapter(adapter);
                    rcvData.invalidate();
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
        adapter = new MyFileAdapter(ListOld.this, files);
        // rcvData.setHasFixedSize(true);
        //Linear
        rcvData.setLayoutManager(new LinearLayoutManager(ListOld.this));
        /*Grid
        rcvData.setLayoutManager(new GridLayoutManager(this,2));*/
        rcvData.setAdapter(adapter);
        keys = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
//                loadAll(newText, newList);
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
        searchviewww.setMenuItem(item);
        return true;
    }
}
