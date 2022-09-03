package com.example.fighting.Walk;

import static com.example.fighting.Util.isPicStorageUrl;
import static com.example.fighting.Util.showToast;
import static com.example.fighting.Util.storageUrlToName;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FirebaseSupporter {
    private Activity activity;
    private OnTraceListener onTraceListener;
    private int successCount;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private String datedetail;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public FirebaseSupporter(Activity activity) {
        this.activity = activity;
    }

    public void setOnTraceListener(OnTraceListener onTraceListener){
        this.onTraceListener = onTraceListener;
    }

    public void storageDelete(final TraceInfo traceInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Log.e("로그", "삭제 실행2");
        datedetail = new SimpleDateFormat("yyyyMMddhhmmss").format(traceInfo.getCreatedAt());
        Log.e("로그", "datedetail " + datedetail);

        ArrayList<String> picturesList = traceInfo.getPictures();
        for (int i = 0; i < picturesList.size(); i++) {
            String pictures = picturesList.get(i);
            if (isPicStorageUrl(pictures)) { // Util에서 선언한 메소드 사용
                successCount++;
                StorageReference desertRef = storageRef.child("uploads/" + user.getUid() + "/"+datedetail+"/"+ storageUrlToName(pictures)); // 사진 이름 0.jpg 메소드 사용
                Log.e("로그", "삭제 위치 "+"uploads/" + user.getUid() + "/"+datedetail+"/"+ storageUrlToName(pictures));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        successCount--;
                        storeDelete(datedetail);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
        // 이미지가 없다면
        storeDelete(datedetail);
    }

    private void storeDelete(String date){
        db = FirebaseFirestore.getInstance();

        if(successCount==0){
            // 이거 날짜로 문서 지워버리게게 수정수정 별별
            db.collection("traces").document(user.getUid()).collection("date").document(datedetail)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity, "db 문서 삭제 성공");
                            if(onTraceListener!=null)
                                onTraceListener.onDelete();
                            //traceUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

//    public void change(int position){
//        onTraceListener.onModify(position);
//    }
}