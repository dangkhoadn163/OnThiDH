package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dk.onthidh.CustomDialog.LogoutDialog;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment6 extends Fragment {
    private FirebaseAuth mAuth;
    private LogoutDialog logoutDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment6,container,false);
        openLogoutDialog();
        return view;
    }
    private void openLogoutDialog() {
        logoutDialog = new LogoutDialog(getActivity());
        logoutDialog.show();
    }
}

