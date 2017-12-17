package com.example.dk.onthidh.CustomDialog;

/**
 * Created by DK on 11/8/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.Activity.Test;
import com.example.dk.onthidh.R;

public class DialogStart {

    public View view;
    public AlertDialog.Builder builder;
    public AlertDialog dialog;
    public Context context;
    public ProgressBar pbstart;
    public TextView txvTitle, txvThoat, txvThi;
    private boolean check = true;

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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }
    public boolean isShowing()
    {
        return dialog.isShowing();
    }
    public void dismiss() {
        dialog.dismiss();
    }
    public void dialogListener(final CountDownTimer timerup, final CountDownTimer timerdown)
    {
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(!pbstart.isShown())
                    {
                        check = true;
                        timerup.cancel();
                        timerdown.start();
                        dismiss();
                    }
                    else
                    {
                        check = false;
                    }
                }
                return true;
            }
        });
    }
    public boolean getCheckBack()
    {
        return check;
    }
    public void setCheckBack(boolean check)
    {
        this.check = check;
    }

}
