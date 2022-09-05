package com.example.fighting;

import static com.example.fighting.Util.showToast;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");

    public FirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void storageDelete(final y_postInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getPostId();
        ArrayList<String> contentsList = postInfo.getImgList();

        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            //if (isStorageUrl(contents)) {
            successCount++;
            StorageReference desertRef = storageRef.child("posts/" + id + "/" + i);
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
            //}
        }
        storeDelete(id, postInfo);
    }

    private void storeDelete(final String id, final y_postInfo postInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            if(user.getDisplayName().equals(postInfo.getWriteId())){
                firebaseFirestore.collection("posts").document(postInfo.getPostId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myRef.child(postInfo.getPostId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showToast(activity, "게시글을 삭제하였습니다");
                                        onPostListener.onDelete(postInfo);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("error: "+e.getMessage());
                                        showToast(activity, "게시글을 삭제하지 못하였습니다");
                                    }
                                });
                                //postsUpdate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(activity, "게시글을 삭제하지 못하였습니다");
                            }
                        });
            }

            else{
                showToast(activity, "작성자만 글을 삭제하거나 수정할 수 있습니다");
            }
        }
    }
}