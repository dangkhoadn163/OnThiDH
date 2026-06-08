package com.example.dk.onthidh.CustomDialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    public EditText edtcurrentpass, edtnewpass, edtconfirm;

    public ChangepassDiaglog(final Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_changepass, null);
        this.builder = new AlertDialog.Builder(context);
        this.txvTitle = (TextView) view.findViewById(R.id.dialog_change_title);
        this.edtcurrentpass = (EditText) view.findViewById(R.id.edt_currentpass);
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
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentPassword = edtcurrentpass.getText().toString().trim();
                final String newPassword = edtnewpass.getText().toString().trim();
                String confirmPassword = edtconfirm.getText().toString().trim();

                if (user == null || TextUtils.isEmpty(user.getEmail())) {
                    Toast.makeText(context, "Phiên đăng nhập đã hết. Hãy đăng nhập lại.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(currentPassword)) {
                    edtcurrentpass.setError("Nhập mật khẩu hiện tại");
                    edtcurrentpass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(newPassword)) {
                    edtnewpass.setError("Nhập mật khẩu mới");
                    edtnewpass.requestFocus();
                    return;
                }
                if (newPassword.length() < 6) {
                    edtnewpass.setError("Mật khẩu phải có ít nhất 6 ký tự");
                    edtnewpass.requestFocus();
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    edtconfirm.setError("Mật khẩu xác nhận không khớp");
                    edtconfirm.requestFocus();
                    return;
                }
                if (currentPassword.equals(newPassword)) {
                    edtnewpass.setError("Mật khẩu mới phải khác mật khẩu cũ");
                    edtnewpass.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, getErrorMessage(task, "Xác thực lại thất bại"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("thành coooooong", "Password updated");
                                Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Log.d("thất baaaaaaaaaai", "Error password not updated");
                                Toast.makeText(context, getErrorMessage(task, "Không thể đổi mật khẩu"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    }
                });
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

    private String getErrorMessage(Task<?> task, String fallback) {
        if (task.getException() != null && !TextUtils.isEmpty(task.getException().getMessage())) {
            return task.getException().getMessage();
        }
        return fallback;
    }
}
