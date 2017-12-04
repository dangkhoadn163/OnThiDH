package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dk.onthidh.Class.LoadDataOld;
import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.example.dk.onthidh.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by DK on 12/2/2017.
 */

public class ChemistryFragment extends Fragment{
    LoadDataOld loadChemisty;
    String uid;
    ArrayList<MyFile> files;
    MyFileAdapter adapter;
    private RecyclerView rcvData;
    DatabaseReference rootDatabase;
    public ChemistryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_english, container, false);
        uid = getActivity().getIntent().getExtras().getString("Uid");
        rootDatabase = FirebaseDatabase.getInstance().getReference();
        rcvData = (RecyclerView) view.findViewById(R.id.recyclerViewImage);
        files = new ArrayList<>();
        adapter = new MyFileAdapter(getActivity(), files);
        rcvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvData.setAdapter(adapter);

        loadChemisty = new LoadDataOld();
        loadChemisty.loadOld(uid, "hoahoc", getActivity(),files, adapter, rcvData, rootDatabase);
        return view;
    }
}
