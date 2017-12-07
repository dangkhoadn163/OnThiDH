package com.example.dk.onthidh.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dk.onthidh.Activity.MainActivity;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentMain extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    String monhoc;
    private Button btnmath, btnenglish, btnbiology, btnchemistry, btnphysic, btnedu, btnhistoty
            , btngeography;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentmain,container,false);
        uid = getActivity().getIntent().getExtras().getString("Uid");
        btnenglish = (Button)view.findViewById(R.id.btn_english);
        btnphysic= (Button)view.findViewById(R.id.btn_physic);
        btnchemistry=(Button)view.findViewById(R.id.btn_chemistry);
        btngeography = (Button)view.findViewById(R.id.btn_geography);
        btnhistoty = (Button)view.findViewById(R.id.btn_history);
        btnbiology = (Button)view.findViewById(R.id.btn_biology);
        btnedu= (Button)view.findViewById(R.id.btn_education);
        btnmath= (Button)view.findViewById(R.id.btn_math);
        anhvan();
        vatly();
        hoahoc();
        dialy();
        lichsu();
        sinhhoc();
        toanhoc();
        gdcd();
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
    public void hoahoc() {
        btnchemistry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "hoahoc";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
    public void dialy() {
        btngeography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "dialy";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
    public void lichsu() {
        btnhistoty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "lichsu";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
    public void sinhhoc() {
        btnbiology.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "sinhhoc";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
    public void gdcd(){
        btnedu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void toanhoc(){
        btnmath.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                monhoc = "toanhoc";
                intent.putExtra("monhoc", monhoc);
                intent.putExtra("Uid", uid);
                getActivity().startActivity(intent);
            }
        });
    }
}

