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

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.example.dk.onthidh.Activity.RegisterActivity;
import com.example.dk.onthidh.Class.Uid;
import com.example.dk.onthidh.R;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DK on 11/1/2017.
 */

public class FragmentAccount extends Fragment {
    EditText editName,editSchool,editClass,editAddress,editPhone,editGrade,editBirth;
    ImageButton imgbName,imgSchool,imgbClass,imgbAddress,imgbPhone,imgbGrade,imgbBirth;
    Button btnCancel,btnSave;
    String uid;
    String nameuser="";
    String classuser="";
    String schooluser="";
    String addressuser="";
    String phoneuser="";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    private Button mButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentacount,container,false);
        editName= (EditText) view.findViewById(R.id.edt_name);
        editSchool= (EditText) view.findViewById(R.id.edt_school);
        editClass= (EditText) view.findViewById(R.id.edt_class);
        editAddress= (EditText) view.findViewById(R.id.edt_address);
        editPhone= (EditText) view.findViewById(R.id.edt_phone);
        editGrade= (EditText) view.findViewById(R.id.edt_grade);
        editBirth= (EditText) view.findViewById(R.id.edt_birth);
        btnSave=(Button)view.findViewById(R.id.btn_save);
        btnCancel=(Button)view.findViewById(R.id.btn_cancel);
        imgbName=(ImageButton)view.findViewById(R.id.btn_editname);
        imgSchool=(ImageButton)view.findViewById(R.id.btn_editschool);
        imgbClass=(ImageButton)view.findViewById(R.id.btn_editclass);
        imgbAddress=(ImageButton)view.findViewById(R.id.btn_editaddress);
        imgbPhone=(ImageButton)view.findViewById(R.id.btn_editphone);
        imgbGrade=(ImageButton)view.findViewById(R.id.btn_editgrade);
        imgbBirth=(ImageButton)view.findViewById(R.id.btn_editbirth);


        uid = getActivity().getIntent().getExtras().getString("Uid");
        mAuth = FirebaseAuth.getInstance();
        // Lay duong dan cua note goc tren database:
        mDatabase = FirebaseDatabase.getInstance().getReference();
        load();
        click();
        btnSave();
        btnCancel();
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }

    public void click(){
        imgbName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName.setEnabled(true);
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
                editClass.setEnabled(true);
            }
        });
        imgbAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAddress.setEnabled(true);
            }
        });
        imgbPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPhone.setEnabled(true);
            }
        });
        imgbGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGrade.setEnabled(true);
            }
        });
        imgbBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        //.setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        });

    }
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(getActivity(), mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(getActivity(),
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    public void datauser(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameuser= editName.getText().toString();
                schooluser= editSchool.getText().toString();
                classuser= editClass.getText().toString();
                addressuser=editAddress.getText().toString();
                phoneuser=editPhone.getText().toString();
                mDatabase.child("account").child(uid).child("name").setValue(nameuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                mDatabase.child("account").child(uid).child("school").setValue(schooluser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                mDatabase.child("account").child(uid).child("class").setValue(classuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                mDatabase.child("account").child(uid).child("address").setValue(addressuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                mDatabase.child("account").child(uid).child("phone").setValue(phoneuser).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public void load(){
        mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    nameuser = dataSnapshot.child("name").getValue().toString();
                    if(nameuser!=null){
                        editName.setText(nameuser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("school")) {
                    schooluser = dataSnapshot.child("school").getValue().toString();
                    if(schooluser!=null){
                        editSchool.setText(schooluser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("class")) {
                    classuser = dataSnapshot.child("class").getValue().toString();
                    if(classuser!=null){
                        editClass.setText(classuser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("address")) {
                    addressuser = dataSnapshot.child("address").getValue().toString();
                    if(addressuser!=null){
                        editAddress.setText(addressuser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.child("account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("phone")) {
                    phoneuser = dataSnapshot.child("phone").getValue().toString();
                    if(phoneuser!=null){
                        editPhone.setText(phoneuser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void btnCancel(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName.setEnabled(false);
                editSchool.setEnabled(false);
                editPhone.setEnabled(false);
                editAddress.setEnabled(false);
                editClass.setEnabled(false);
                load();
            }
        });
    }
}

