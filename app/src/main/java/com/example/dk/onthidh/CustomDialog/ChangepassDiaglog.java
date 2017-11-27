package com.example.dk.onthidh.CustomDialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by DK on 11/26/2017.
 */

public class ChangepassDiaglog {
    public View view;
    public AlertDialog.Builder builder;
    public AlertDialog dialog;
    public Context context;
    public TextView txvTitle, txvClose, txvConfirm;
    public EditText edtnewpass,edtconfirm;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ChangepassDiaglog(final Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_changepass, null);
        this.builder = new AlertDialog.Builder(context);
        this.txvTitle = (TextView) view.findViewById(R.id.dialog_change_title);
        this.edtnewpass = (EditText) view.findViewById(R.id.edt_newpass);
        this.edtconfirm = (EditText) view.findViewById(R.id.edt_confirm);
        this.txvClose = (TextView) view.findViewById(R.id.dialog_change_cancel);
        this.txvConfirm=(TextView)view.findViewById(R.id.dialog_change_confirm);
        this.txvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtnewpass.getText().toString().equals(edtconfirm.getText().toString()) ) {
                    user.updatePassword(edtnewpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("thành coooooong", "Password updated");
                                Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("thất baaaaaaaaaai", "Error password not updated");
                            }
                        }
                    });
                }
                else {
                    Log.d("loooooooooooog","looooooooooi");
                }
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
