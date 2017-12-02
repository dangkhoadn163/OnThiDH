package com.example.dk.onthidh.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.dk.onthidh.Activity.MainActivity;
import com.example.dk.onthidh.CustomDialog.ChangepassDiaglog;
import com.example.dk.onthidh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by DK on 11/1/2017.
 */

public class FragmentChangepass extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference rootDatabase;
    String uid;
    String monhoc;
    private Button btnmath, btnenglish, btnbiology, btnchemistry, btnphysic, btnedu, btnhistoty, btngeography;
    private Button btnconfirm;
    private EditText edtoldpass,edtnewpass,edtconfirm;
    private ChangepassDiaglog changepassDiaglog;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentmain,container,false);
        uid = getActivity().getIntent().getExtras().getString("Uid");
        btnconfirm = (Button)view.findViewById(R.id.btn_confirm);
        edtoldpass = (EditText) view.findViewById(R.id.edt_oldpass);
        edtnewpass = (EditText) view.findViewById(R.id.edt_newpass);
        edtconfirm = (EditText) view.findViewById(R.id.edt_confirm);


/*       btnconfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(edtnewpass.getText().toString().equals(edtconfirm.getText().toString()) ) {
                   user.updatePassword(edtnewpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               Log.d("thành coooooong", "Password updated");
                               Toast.makeText(getActivity(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(getActivity(), ChooseActivity.class);
                               intent.putExtra("Uid", uid);
                               getActivity().startActivity(intent);
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
       });*/
        openChangeDialog();
        uid = getActivity().getIntent().getExtras().getString("Uid");
        btnenglish = (Button)view.findViewById(R.id.btn_english);
        btnphysic= (Button)view.findViewById(R.id.btn_physic);
        btnchemistry=(Button)view.findViewById(R.id.btn_chemistry);
        anhvan();
        vatly();
        hoahoc();
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }
    private void openChangeDialog() {
        changepassDiaglog = new ChangepassDiaglog(getActivity());
        changepassDiaglog.show();
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
    public void edu(){

    }
}

