package com.example.dk.onthidh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment1 extends Fragment {
    String uid;
    String monhoc;
    private Button btnmath, btnenglish, btnbiology, btnchemistry, btnphysic, btnedu, btnhistoty, btngeography;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment1,container,false);
        uid = getActivity().getIntent().getExtras().getString("Uid");
        btnenglish = (Button)view.findViewById(R.id.btn_english);
        btnphysic= (Button)view.findViewById(R.id.btn_physic);
        anhvan();
        vatly();
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }
    public void anhvan() {
        btnenglish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "anhvan";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }

    public void vatly() {
        btnphysic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "vatly";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
}

