package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dk.onthidh.FolderMoi.MoiAdapter;
import com.example.dk.onthidh.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by DK on 12/3/2017.
 */

public class DetailResult_Fragment extends Fragment {

    private static final String TAG = "Test";
    DrawerLayout drawer;
    String keyt,key;
    String userid,uid;
    String monhoc,monhoc2,tende;
    ArrayList<String> mois;
    MoiAdapter adapter_moi;
    private DatabaseReference rootDatabase;
    private RecyclerView rcvDataMoi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.score_fragment,container,false);
        mois = new ArrayList<>();
        adapter_moi = new MoiAdapter(getActivity(), mois);
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        keyt = getActivity().getIntent().getExtras().getString("keyt111");
        if (keyt != null) {
            keyt = getActivity().getIntent().getExtras().getString("keyt111");
        }
        else {
            key = getActivity().getIntent().getExtras().getString("keyt222");
            keyt=key;
        }

        monhoc = getActivity().getIntent().getExtras().getString("monhoc");
        if(monhoc!=null){
            monhoc = getActivity().getIntent().getExtras().getString("monhoc");
        }
        else {
            monhoc2 = getActivity().getIntent().getExtras().getString("monhoc2");
            monhoc=monhoc2;
        }

        rcvDataMoi = (RecyclerView)view.findViewById(R.id.recyclerViewImage);
/*        rcvDataMoi.setHasFixedSize(true);*/
        rcvDataMoi.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvDataMoi.setAdapter(adapter_moi);
        loaddetailresult(keyt);
        return view;
    }
     public void loaddetailresult(String keyt) {
        rootDatabase.child("monhoc").child(monhoc).child(keyt).child("detailresult").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String link = dataSnapshot.getValue().toString();
                if(link!=null){
                    mois.add(link);
                    adapter_moi.notifyDataSetChanged();
                }else{
                    Log.d(TAG,"KHONG CO DU LIEU");
                }
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

}
