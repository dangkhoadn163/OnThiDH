package com.example.dk.onthidh.CustomDialog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.dk.onthidh.Activity.LaunchActivity;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by DK on 11/26/2017.
 */

public class LogoutDialog {
    public View view;
    private FirebaseAuth mAuth;
    public AlertDialog.Builder builder;
    public AlertDialog dialog;
    public Context context;
    public TextView txvTitle, txvClose, txvConfirm;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public LogoutDialog(final Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null);
        this.builder = new AlertDialog.Builder(context);
        this.txvTitle = (TextView) view.findViewById(R.id.dialog_logout_title);
        this.txvClose = (TextView) view.findViewById(R.id.dialog_logout_cancel);
        this.txvConfirm=(TextView)view.findViewById(R.id.dialog_logout_confirm);
        this.txvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(context, LaunchActivity.class);
                context.startActivity(intent);

            }
        });
    }

    public void setTitle(CharSequence title) {
        if (title != null)
            txvTitle.setText(title);
    }

    public void show() {
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    public void dismiss() {
        dialog.dismiss();
    }
}
