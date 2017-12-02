package com.example.dk.onthidh.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MathFragment extends Fragment {
    String uid;
    ArrayList<MyFile> files;
    MyFileAdapter adapter;
    ArrayList<String> keys;
    private RecyclerView rcvData;
    DatabaseReference rootDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_math,container,false);
        uid = getActivity().getIntent().getExtras().getString("Uid");
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keys = new ArrayList<>();
        rcvData = (RecyclerView)view.findViewById(R.id.recyclerViewImage);
        files = new ArrayList<>();
        adapter = new MyFileAdapter(getActivity(), files);
        // rcvData.setHasFixedSize(true);
        //Linear
        rcvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*Grid
        rcvData.setLayoutManager(new GridLayoutManager(this,2));*/
        rcvData.setAdapter(adapter);
        keys = new ArrayList<>();
        loadOld();
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }
    private void loadOld() {
        FirebaseRecyclerAdapter<MyFile, MyFileViewHolder> myAdapterTest = new FirebaseRecyclerAdapter<MyFile, MyFileViewHolder>(
                MyFile.class, R.layout.item, MyFileViewHolder.class, rootDatabase.child("account").child(uid).child("anhvan").child("de")
        ) {
            @Override
            protected void populateViewHolder(final MyFileViewHolder viewHolder, final MyFile model, int position) {
                final String t = getRef(position).getKey().toString();
                viewHolder.txvKey.setText(t);
                //viewHolder.setActionClick(model.text);
                rootDatabase.child("account").child(uid).child("anhvan").child("de").child(t).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("nametest")) {
                            model.text = dataSnapshot.child("nametest").getValue().toString();
                            viewHolder.setActionClick(model.text);
                            viewHolder.txvTenFile.setText(model.text);
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent= new Intent(getActivity(),Score.class);
                                    intent.putExtra("keyt222",t);
                                    intent.putExtra("Uid222", uid);
                                    intent.putExtra("monhoc2","anhvan");
                                    intent.putExtra("tende", model.text);
                                    getActivity().startActivity(intent);
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
                        Intent intent= new Intent(getActivity(),Score.class);
                        intent.putExtra("keyt",t);
                        intent.putExtra("Uid2", uid);
                        intent.putExtra("monhoc","anhvan");
                        intent.putExtra("tende", model.text);
                        Log.d("tende", model.text);
                        getActivity().startActivity(intent);
                    }
                });
//                Toast.makeText(ListTest.this, t+"", Toast.LENGTH_SHORT).show();
            }
        };
        rcvData.setAdapter(myAdapterTest);

    }
}