package com.example.dk.onthidh.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dk.onthidh.Activity.LaunchActivity;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment6 extends Fragment {
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment6,container,false);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LaunchActivity.class);
        startActivity(intent);
        return view;
    }
}

