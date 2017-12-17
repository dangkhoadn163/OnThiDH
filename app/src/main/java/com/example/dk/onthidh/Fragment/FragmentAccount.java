package com.example.dk.onthidh.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dk.onthidh.Activity.RegisterActivity;
import com.example.dk.onthidh.Class.Uid;
import com.example.dk.onthidh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DK on 11/1/2017.
 */

public class FragmentAccount extends Fragment {
    EditText edtName,editEmail,editSchool,edtClass,edtAddress,edtPhone;
    ImageButton imgbName,imgbEmail,imgSchool,imgbClass,imgbAddress,imgbPhone;
    Button btnCancel,btnSave;
    String uid;
    String nameuser="";
    String classuser="";
    String schooluser="";
    String addressuser="";
    String phoneuser="";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentacount,container,false);
        edtName= (EditText) view.findViewById(R.id.edt_name);
        editEmail= (EditText) view.findViewById(R.id.edt_email);
        editSchool= (EditText) view.findViewById(R.id.edt_school);
        edtClass= (EditText) view.findViewById(R.id.edt_class);
        edtAddress= (EditText) view.findViewById(R.id.edt_address);
        edtPhone= (EditText) view.findViewById(R.id.edt_phone);
        btnSave=(Button)view.findViewById(R.id.btn_save);
        btnCancel=(Button)view.findViewById(R.id.btn_cancel);
        imgbName=(ImageButton)view.findViewById(R.id.btn_editname);
        imgbEmail=(ImageButton)view.findViewById(R.id.btn_editemail);
        imgSchool=(ImageButton)view.findViewById(R.id.btn_editschool);
        imgbClass=(ImageButton)view.findViewById(R.id.btn_editclass);
        imgbAddress=(ImageButton)view.findViewById(R.id.btn_editaddress);
        imgbPhone=(ImageButton)view.findViewById(R.id.btn_editphone);

        uid = getActivity().getIntent().getExtras().getString("Uid");
        mAuth = FirebaseAuth.getInstance();
        // Lay duong dan cua note goc tren database:
        mDatabase = FirebaseDatabase.getInstance().getReference();
        click();
        btnSave();
        btnCancel();
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }

    public void click(){
        imgbName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameuser = edtName.getText().toString();
                edtName.setEnabled(true);
            }
        });
        imgbEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail.setEnabled(true);
            }
        });
        imgSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSchool.setEnabled(true);
            }
        });
        imgbClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtClass.setEnabled(true);
            }
        });
        imgbAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtAddress.setEnabled(true);
            }
        });
        imgbPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhone.setEnabled(true);
            }
        });
    }
    public void datauser(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameuser= edtName.getText().toString();
                mDatabase.child("account").child(uid).child("name").setValue(nameuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void btnSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datauser();
            }
        });
    }
    public void btnCancel(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("name")) {
                            nameuser = dataSnapshot.child("name").getValue().toString();
                            if(nameuser!=null){
                                edtName.setText(nameuser);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}

