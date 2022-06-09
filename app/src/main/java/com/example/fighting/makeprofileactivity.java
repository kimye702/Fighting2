package com.example.fighting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class makeprofileactivity extends AppCompatActivity{

    private static final String TAG = "makeprofileactivity";
    Button profile_button, profile_image_button, check;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private final int GALLERY_CODE = 10;
    private ImageView profile_image;
    Uri file, filePath;

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
                profileUpdate();
            }
        });

    }

    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE){
            file = data.getData();
            StorageReference storageReference = storage.getReference();
            StorageReference mountainImagesRef = storageReference.child("users/"+user.getDisplayName()+"/profileImage.jpg");
            UploadTask uploadTask = mountainImagesRef.putFile(file);
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                profile_image.setImageBitmap(img);
            }catch (Exception e){
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(makeprofileactivity.this, "사진 등록 실패! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(makeprofileactivity.this, "사진 등록 성공", Toast.LENGTH_SHORT).show();
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
