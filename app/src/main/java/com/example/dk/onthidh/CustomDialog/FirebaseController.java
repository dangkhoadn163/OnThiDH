package com.example.dk.onthidh.CustomDialog;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

/**
 * Created by DK on 9/23/2017.
 */

public class FirebaseController {
    // Trước khi làm cái này nhớ nhúng thư viện:
    // compile 'com.google.firebase:firebase-storage:11.0.2'
    public static void uploadAvatar(Context context, final String userID, Uri imageUri) {


        // upload hình Avatar lên storage và lấy đường dẫn (lúc này đã là url chứ không phải uri ):
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference()
                .child("account")
                .child(userID);
        //Nhung library java.util.calendar
        Calendar c = Calendar.getInstance();
        String s = c.getTimeInMillis() + "";

//        FirebaseStorage mStorage = FirebaseStorage.getInstance();
//        StorageReference storageReference = mStorage.getReference();
//        storageReference.child(s).putFile(imageUri);
        // up hinh len database
        FirebaseStorage.getInstance().getReference().child(s).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = taskSnapshot.getDownloadUrl().toString();
                UploadDatabase(url,userID);
            }
        });


        // upload storage xong thì upload đường dẫn vào database:

        //... chưa làm cái mẹ gì hết

    }
    // gán hình cho user
    private static void UploadDatabase(String url, String userID) {
        FirebaseDatabase.getInstance().getReference().child("account").child(userID).child("avatar").setValue(url);
    }
}
