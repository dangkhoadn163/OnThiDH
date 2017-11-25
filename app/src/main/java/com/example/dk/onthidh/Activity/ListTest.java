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
//        loadOld();
        Log.d("abc", "truong thpt tran hung dao tphcm 2017 lan 2".contains("lan 1") + "");
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
                        Intent intent= new Intent(ListTest.this,Test.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        intent.putExtra("tende", model.text);
                        ListTest.this.startActivity(intent);
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
                        Intent intent= new Intent(ListTest.this,Test.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        intent.putExtra("tende", model.text);
                        Log.d("tende", model.text);
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

    public static String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c > '\u007F')
            {
                sb.append(c);
                Log.d("char", c + "");
            }
        }
        return sb.toString();
    }
    private void loadAll(final String nameTest, final ArrayList<MyFile> newList)
    {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref2;
        ref2 = ref1.child("monhoc").child(monhoc);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                    List <String> accentTemp2Arr = new ArrayList<String>();
                    List <String> accentTempArr = new ArrayList<String>();
                    List<String> temp2Arr = new ArrayList<String>();
                    List<String> tempArr = new ArrayList<String>();

                    final MyFile model = new MyFile();
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
                    replaceD2.contains(" ");

                    if(replaceD2.contains(" ") && (!replaceD.contains(replaceD2) && !temp.contains(temp2)))
                    {
                        int posStartTemp2 = 0;
                        int posStartTemp = 0;
                        int lengthTemp2 = temp2.length();
                        int lengthTemp = temp.length();
                        //Temp2
                        for (int i = 0; i < lengthTemp2; i++)
                        {
                            String subStrTemp2;
                            if (temp2.charAt(i) == (char) 32) {
                                subStrTemp2 = temp2.substring(posStartTemp2, i);
                                temp2Arr.add(subStrTemp2);
                                accentTemp2Arr.add(flattenToAscii(temp2Arr.get(temp2Arr.size() - 1)).toString());
                                posStartTemp2 = i + 1;
                                Log.d("posStart", posStartTemp2 + "");
                                i = posStartTemp2;
                            }
                            if (i == lengthTemp2 - 1) {
                                subStrTemp2 = temp2.substring(posStartTemp2);
                                temp2Arr.add(subStrTemp2);
                                accentTemp2Arr.add(flattenToAscii(temp2Arr.get(temp2Arr.size() - 1)).toString());
                                break;
                            }
                        }
                        //Temp
                        for(int i = 0; i < lengthTemp; i++)
                        {
                            String subStrTemp;
                            if(temp.charAt(i) == (char)32)
                            {
                                subStrTemp = temp.substring(posStartTemp, i);
                                tempArr.add(subStrTemp);
                                accentTempArr.add(flattenToAscii(tempArr.get(tempArr.size() - 1)).toString());
                                posStartTemp = i + 1;
                                Log.d("posStart", posStartTemp + "");
                                i = posStartTemp;
                            }
                            if(i == lengthTemp - 1)
                            {
                                subStrTemp = temp.substring(posStartTemp);
                                tempArr.add(subStrTemp);
                                accentTempArr.add(flattenToAscii(tempArr.get(tempArr.size() - 1)).toString());
                                break;
                            }
                        }
                        Log.d("temp2DArr", temp2Arr + "");
                        Log.d("tempArr", tempArr + "");
                        Log.d("accentTemp2Arr", accentTemp2Arr + "");
                        Log.d("accentTempArr", accentTempArr + "");
                        int lengthTemp2tArr = temp2Arr.size();
                        Log.d("lengthTemp2tArr", lengthTemp2tArr + "");
                        int countContains = 0;
                        for(int i = 0; i < lengthTemp2tArr; i++)
                        {

                           // String compareArrTemp = removeDiacriticalMarks(tempArr.get(i));
                            String compareArrTemp2 = removeDiacriticalMarks(temp2Arr.get(i));
                            String accentArrTemp2 = flattenToAscii(temp2Arr.get(i)).toString();
                            int nTemp2Arr = accentArrTemp2.length();
                            Log.d("replaceD", replaceD + "");
                            //String replaceDArr = compareArrTemp.replaceAll("đ", "d");
                            String replaceD2Arr = compareArrTemp2.replaceAll("đ", "d");


                            if ((temp.contains(temp2Arr.get(i)) || (replaceD.contains(replaceD2Arr)
                                    && temp2Arr.get(i).contains(replaceD2Arr))))
                            {
                               countContains++;
                            }
                            if(countContains == lengthTemp2tArr)
                            {
                                if ((temp.contains(temp2Arr.get(i)) || (replaceD.contains(replaceD2Arr)
                                        && temp2Arr.get(i).contains(replaceD2Arr))))
                                {
                                    Log.d("temp2ArrItem", temp2Arr.get(i) + "");
                                    Log.d("temp2ArrList", temp2Arr + "");
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
                                            intent.putExtra("tende", model.text);
                                            ListTest.this.startActivity(intent);
                                        }
                                    });

                                }
                            }
                        }
                    }
                    if(replaceD.contains(replaceD2) && nTemp2 != 0 && !check)
                    {
                        int pos = replaceD.indexOf(replaceD2);
                        String sb = temp.substring(pos, temp2.length() + pos);
                        String accentTemp = flattenToAscii(sb).toString();
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
                                    Intent intent= new Intent(ListTest.this,Test.class);
                                    intent.putExtra("keyt",dsp.getRef().getKey());
                                    intent.putExtra("Uid2", uid);
                                    intent.putExtra("monhoc",monhoc);
                                    intent.putExtra("tende", model.text);
                                    ListTest.this.startActivity(intent);
                                }
                            });
                        }
                        else if(!accentTemp.contains(accentTemp2) && !check)
                        {
                            accentTemp = accentTemp.replaceAll("̉đ", "đ");
                            int nTemp = accentTemp.length();
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
                                                Intent intent = new Intent(ListTest.this, Test.class);
                                                intent.putExtra("keyt", dsp.getRef().getKey());
                                                intent.putExtra("Uid2", uid);
                                                intent.putExtra("monhoc", monhoc);
                                                intent.putExtra("tende", model.text);
                                                ListTest.this.startActivity(intent);
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
                                Intent intent= new Intent(ListTest.this,Test.class);
                                intent.putExtra("keyt",dsp.getRef().getKey());
                                intent.putExtra("Uid2", uid);
                                intent.putExtra("monhoc",monhoc);
                                intent.putExtra("tende", model.text);
                                ListTest.this.startActivity(intent);
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
                loadAll(newText, newList);
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
