package com.example.dk.onthidh.Class;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dk.onthidh.Activity.Score;
import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
        adapter.setOnItemClickListener(new MyFileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position < 0 || position >= files.size()) {
                    return;
                }

                MyFile model = files.get(position);
                Intent intent = new Intent(context, Score.class);
                intent.putExtra("keyt222", model.key);
                intent.putExtra("Uid222", uid);
                intent.putExtra("monhoc2", monhoc);
                intent.putExtra("tende", model.text);
                context.startActivity(intent);
            }
        });

        rootDatabase.child("account").child(uid).child(monhoc).child("de")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        files.clear();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (!child.hasChild("nametest") || !child.hasChild("dapandalam")) {
                                continue;
                            }

                            MyFile model = child.getValue(MyFile.class);
                            if (model == null) {
                                model = new MyFile();
                            }

                            model.key = child.getKey();

                            if (child.hasChild("nametest")) {
                                model.text = String.valueOf(child.child("nametest").getValue());
                            }

                            files.add(model);
                        }

                        adapter.setfilter(files);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        rcvData.setAdapter(adapter);
    }
}
