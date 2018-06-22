package com.example.dk.onthidh.Class;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.dk.onthidh.Activity.Score;
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

import java.util.ArrayList;

/**
 * Created by ADMIN on 12/3/2017.
 */

public class LoadDataOld {

    public LoadDataOld()
    {

    }

    public void loadOld(final String uid, final String monhoc, final Context context
            , final ArrayList<MyFile> files, final MyFileAdapter adapter, final RecyclerView rcvData
            , final DatabaseReference rootDatabase)
    {
        FirebaseRecyclerAdapter<MyFile, MyFileViewHolder> myAdapterTest = new FirebaseRecyclerAdapter<MyFile, MyFileViewHolder>(
                MyFile.class, R.layout.item, MyFileViewHolder.class, rootDatabase.child("account").child(uid).child(monhoc).child("de")
        ) {
            @Override
            protected void populateViewHolder(final MyFileViewHolder viewHolder, final MyFile model, int position) {
                final String t = getRef(position).getKey().toString();
                //Nếu cần hiển thị thì ghi
                viewHolder.txvKey.setText("");
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
                                    Intent intent= new Intent(context,Score.class);
                                    intent.putExtra("keyt222",t);
                                    intent.putExtra("Uid222", uid);
                                    intent.putExtra("monhoc2",monhoc);
                                    intent.putExtra("tende", model.text);
                                    context.startActivity(intent);
                                }
                            });
                            Log.d("tende", model.text);
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
                        Intent intent= new Intent(context,Score.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc",monhoc);
                        intent.putExtra("tende", model.text);
                        Log.d("tende", model.text);
                        context.startActivity(intent);
                    }
                });
//                Toast.makeText(ListTest.this, t+"", Toast.LENGTH_SHORT).show();
            }
        };
        rcvData.setAdapter(myAdapterTest);

    }
}

