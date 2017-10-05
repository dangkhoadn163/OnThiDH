package com.example.dk.onthidh;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListTest extends AppCompatActivity {
    ListView lvTest;
    List<MyData> arrMyData = new ArrayList<>();
    MyDataAdapter adapter = null;
    private TextView txvLog;
    DatabaseReference rootDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        rootDatabase = FirebaseDatabase.getInstance().getReference();

        anhXa();

        adapter = new MyDataAdapter(this, arrMyData);
        lvTest.setAdapter(adapter);
        load();
        Click();

    }
    public  static final String TAG = ListTest.class.getSimpleName();

    private void Click(){
        txvLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListTest.this, Test.class));
            }
        });
    }
    private void load(){
        rootDatabase.child("mydata").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyData mydata = dataSnapshot.getValue(MyData.class);
                arrMyData.add(mydata);
                adapter.notifyDataSetChanged();
//                Toast.makeText(ListTest.this, dataSnapshot.getKey() + "", Toast.LENGTH_SHORT).show();
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
    public void anhXa() {
        txvLog = (TextView)findViewById(R.id.txvLog);
        lvTest=(ListView)findViewById(R.id.listviewTest);
    }
}
