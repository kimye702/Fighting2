package com.example.fighting;

import static com.example.fighting.Util.isStorageUrl;
import static com.example.fighting.Util.showToast;
import static com.example.fighting.Util.storageUrlToName;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseHelper {
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCount;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public FirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void storageDelete(final PostInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUrl(contents)) {
                successCount++;
                StorageReference desertRef = storageRef.child("pictures" + id + "/" + storageUrlToName(contents));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id, postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity, "Error");
                    }
                });
            }
        }
        storeDelete(id, postInfo);
    }

    private void storeDelete(final String id, final PostInfo postInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            if(user.getDisplayName().equals(postInfo.getPublisher())){
                firebaseFirestore.collection("pictures").document(postInfo.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(activity, "???????????? ?????????????????????");
                                onPostListener.onDelete(postInfo);
                                //postsUpdate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(activity, "???????????? ???????????? ??????????????????");
                            }
                        });
            }

            else{
                showToast(activity, "???????????? ?????? ??????????????? ????????? ??? ????????????");
            }
        }
    }
}