package com.example.dk.onthidh.Activity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
    ArrayList<String> keyAccount;
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
        keyAccount = new ArrayList<String>();
        Log.d("Uid1", "onComplete: Uid=" + uid);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        anhXa();
        Nav();
        //loadList();
//        loadOld();
        loadList2();
        Log.d("abc", "truong thpt tran hung dao tphcm 2017 lan 2".contains("lan 1") + "");
        search();

    }
    public  static final String TAG = ListTest.class.getSimpleName();
    private void loadList2()
    {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref2;
        ref2 = ref1.child("monhoc").child(monhoc);
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref4;
        ref4 = ref3.child("account").child(uid).child(monhoc).child("de");

        ref4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (final DataSnapshot dsp : dataSnapshot.getChildren())
                {
                      Log.d("keyAccount", dsp.getRef().getKey() + "");
                      keyAccount.add(dsp.getRef().getKey());


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (final DataSnapshot dsp : dataSnapshot.getChildren()) {

                    if(!keyAccount.contains(dsp.getRef().getKey()))
                    {
                        final MyFile model = new MyFile();
                        model.text = dsp.child("text").getValue().toString();
                        files.add(0,model);
                        //adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent= new Intent(ListTest.this,Test.class);
                                Log.d("postion", position + "/" + dsp.getRef().getKey() + "/" + model.text);
                                intent.putExtra("keyt",dsp.getRef().getKey());
                                intent.putExtra("Uid2", uid);
                                intent.putExtra("monhoc",monhoc);
                                intent.putExtra("tende", model.text);
                                ListTest.this.startActivity(intent);
                            }
                        });
                    }
                    adapter.setfilter(files);
                    rcvData.setAdapter(adapter);
                    rcvData.invalidate();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadList() {
        FirebaseRecyclerAdapter<MyFile, MyFileViewHolder> myAdapterTest = new FirebaseRecyclerAdapter<MyFile, MyFileViewHolder>(
                MyFile.class, R.layout.item, MyFileViewHolder.class, rootDatabase.child("monhoc").child(monhoc)
        ) {
            @Override
            protected void populateViewHolder(final MyFileViewHolder viewHolder, final MyFile model, final int position) {
                final String t = getRef(position).getKey().toString();
                rootDatabase.child("account").child(uid).child(monhoc).child("de").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.txvTenFile.setText(model.text);
                        files.add(model);
                        //Log.d("itemCount", adapter.getItemCount() + "");;
                        Log.d("loadlist", files.size() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        adapter.notifyDataSetChanged();
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListTest.this, Test.class);
                                intent.putExtra("keyt", t);
                                intent.putExtra("Uid2", uid);
                                intent.putExtra("monhoc", monhoc);
                                intent.putExtra("tende", model.text);
                                ListTest.this.startActivity(intent);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
                    List<String> temp2Arr = new ArrayList<String>();
                    List<String> tempArr = new ArrayList<String>();

                    final MyFile model = new MyFile();
                    String temp = dsp.child("text").getValue().toString().toLowerCase();
                    String temp2 = nameTest.toLowerCase();
                    String comparetemp = removeDiacriticalMarks(temp);
                    String comparetemp2 = removeDiacriticalMarks(temp2);

                    String accentTemp2 = flattenToAscii(temp2).toString();
                    accentTemp2 = accentTemp2.replaceAll("̂", "̀̂");
                    int nTemp2 = accentTemp2.length();
                    boolean check = false;

                    String replaceD = comparetemp.replaceAll("đ", "d");
                    String replaceD2 = comparetemp2.replaceAll("đ", "d");
                    if ((temp.contains(temp2) || (replaceD.contains(replaceD2) && temp2.contains(replaceD2))) && !check)
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
                    if(replaceD.contains(replaceD2) && nTemp2 != 0 && !check)
                    {
                        int pos = replaceD.indexOf(replaceD2);
                        String sb = temp.substring(pos, temp2.length() + pos);
                        String accentTemp = flattenToAscii(sb).toString();
                        accentTemp = accentTemp.replaceAll("̂", "̀̂");
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
                            check = true;
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

                    if(replaceD2.contains(" ") && !replaceD.contains(replaceD2))
                    {
                        int posStartTemp2 = 0;
                        int lengthTemp2 = temp2.length();
                        //Temp2
                        for (int i = 0; i < lengthTemp2; i++)
                        {
                            String subStrTemp2;
                            if (temp2.charAt(i) == (char) 32) {
                                subStrTemp2 = temp2.substring(posStartTemp2, i);
                                temp2Arr.add(subStrTemp2);
                                posStartTemp2 = i + 1;
                                Log.d("posStart", posStartTemp2 + "");
                                i = posStartTemp2;
                            }
                            if (i == lengthTemp2 - 1) {
                                subStrTemp2 = temp2.substring(posStartTemp2);
                                temp2Arr.add(subStrTemp2);
                                break;
                            }
                        }

                        int lengthTemp2tArr = temp2Arr.size();
                        int countContains = 0;
                        for(int i = 0; i < lengthTemp2tArr; i++)
                        {
                            if((i != 0 && temp2Arr.get(i).contains("1"))
                                    || (i != 0 && temp2Arr.get(i).contains("2"))
                                    || (i != 0 && temp2Arr.get(i).contains("3")))
                            {
                                if("lan".contains(removeDiacriticalMarks(temp2Arr.get(i - 1))))
                                {
                                    temp2Arr.set(i - 1, temp2Arr.get(i - 1)
                                            + " " + temp2Arr.get(i));
                                    temp2Arr.remove(i);
                                    lengthTemp2tArr = temp2Arr.size();
                                    i--;

                                }
                            }
                            Log.d("temp2DArr", temp2Arr + "");
                            String compareArrTemp2 = removeDiacriticalMarks(temp2Arr.get(i));
                            String accentArrTemp2 = flattenToAscii(temp2Arr.get(i)).toString();
                            int nTemp2Arr = accentArrTemp2.length();
                            Log.d("replaceD", replaceD + "");
                            String replaceD2Arr = compareArrTemp2.replaceAll("đ", "d");

                            if ((temp.contains(temp2Arr.get(i)) || (replaceD.contains(replaceD2Arr)
                                    && temp2Arr.get(i).contains(replaceD2Arr))
                                    || (replaceD.contains(replaceD2Arr)
                                    && (nTemp2Arr != 0)) ))
                            {
                                countContains++;
                            }

                            if(countContains == lengthTemp2tArr)
                            {
                                if(replaceD.contains(replaceD2Arr) && nTemp2Arr != 0)
                                {
                                    int pos = replaceD.indexOf(replaceD2Arr);
                                    String sb = temp.substring(pos, temp2Arr.get(i).length() + pos);
                                    String accentArrTemp = flattenToAscii(sb).toString();
                                    Log.d("sb", sb);
                                    Log.d("accentTemp", accentArrTemp);
                                    Log.d("accentTemp2", accentArrTemp2);
                                    if(accentArrTemp.contains(accentArrTemp2) && !check)
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
                                    else if(!accentArrTemp.contains(accentTemp2) && !check)
                                    {
                                        accentArrTemp = accentArrTemp.replaceAll("̉đ", "đ");
                                        int nTempArr = accentArrTemp.length();
                                        for(int h = 0; h < nTemp2Arr; h++)
                                        {
                                            for(int k = 0; k < nTempArr; k++)
                                            {
                                                if (accentArrTemp2.charAt(h) == accentArrTemp.charAt(k) && !check)
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
                                if ((temp.contains(temp2Arr.get(i)) || (replaceD.contains(replaceD2Arr)
                                        && temp2Arr.get(i).contains(replaceD2Arr))) && !check)
                                {
                                    check = true;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }
    private void search() {
        searchviewww.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {

                return true;
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
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.Search);
        searchviewww.setMenuItem(item);
        return true;
    }
}
