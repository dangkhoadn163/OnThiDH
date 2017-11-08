package com.example.dk.onthidh.CustomDialog;

/**
 * Created by DK on 11/8/2017.
 */

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dk.onthidh.R;

public class DialogStart {

    public View view;
    public AlertDialog.Builder builder;
    public AlertDialog dialog;
    public Context context;
    public ProgressBar pbstart;
    public TextView txvTitle, txvThoat, txvThi;

    public DialogStart(final Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_start, null);
        this.builder = new AlertDialog.Builder(context);
        this.txvTitle = (TextView) view.findViewById(R.id.dialog_start);
        this.pbstart= (ProgressBar) view.findViewById(R.id.progressload);
        this.txvThoat = (TextView) view.findViewById(R.id.dialog_start_thoat);
        this.txvThi = (TextView) view.findViewById(R.id.dialog__start_thi);
    }



    public void show() {
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public void dismiss() {
        dialog.dismiss();
    }
}
