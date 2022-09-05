package com.example.fighting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class makeprofileactivity extends AppCompatActivity{

    private static final String TAG = "makeprofileactivity";
    Button profile_button, profile_image_button, check;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");

    public StorageReference storageRef=storage.getReference();


    private final int GALLERY_CODE = 10;
    private ImageView profile_image;
    Uri file, filePath;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_profile);

        profile_button = (Button) findViewById(R.id.profile_button);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_image_button = (Button) findViewById(R.id.profile_image_button);
        check=(Button)findViewById(R.id.check);


        profile_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAlbum();
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==0){
                    Toast.makeText(makeprofileactivity.this, "프로필 사진을 등록해주세요", Toast.LENGTH_SHORT);
                }
                else if(flag==1){
                    profileUpdate();
                }

            }
        });

    }

    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            file = data.getData();

            StorageReference ref = storageRef.child("users/"+user.getDisplayName()+"/profileImage.jpg");
            UploadTask uploadTask = ref.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageurl = downloadUri.toString();
                        Glide.with(makeprofileactivity.this).load(imageurl).circleCrop().into(profile_image);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                        }
                                    }
                                });
                        myRef.child("UserAccount").child(user.getDisplayName()).child("image").setValue(imageurl);
                        flag=1;
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }


    private void profileUpdate() {
        if(user!=null){
            UserInfo userinfo = new UserInfo(user.getDisplayName());

            db.collection("users").document(user.getUid()).set(userinfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(makeprofileactivity.this, "회원정보 등록을 성공했습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(makeprofileactivity.this, loginactivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            Toast.makeText(makeprofileactivity.this, "회원정보 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
