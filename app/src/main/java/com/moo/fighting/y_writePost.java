package com.moo.fighting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class y_writePost extends AppCompatActivity {

    private Button checkButton;
    private ImageView iv_album;
    private ImageButton ib_delete;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageRef=storage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");

    private EditText et_title, et_content;

    y_postInfo post;

    private final int GALLERY_CODE = 10;
    Uri file;
    private RecyclerView mVerticalView;
    private y_writeImageAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<y_writeImage> imageData = new ArrayList<>();
    private final int MAX_NUM=10;
    private String postid;
    private String profile;
    private y_postInfo postdata=new y_postInfo();
    ArrayList<String> imglist=new ArrayList<String>();

    int flag=0;
    int flag1=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.y_write_post);
        checkButton=(Button)findViewById(R.id.checkButton);
        iv_album=(ImageView)findViewById(R.id.iv_album);
        mVerticalView = (RecyclerView) findViewById(R.id.contentsLayout);
        ib_delete=(ImageButton) findViewById(R.id.ib_delete);
        et_title=(EditText) findViewById(R.id.et_title);
        et_content=(EditText) findViewById(R.id.et_content);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Random rd=new Random();
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        postid = dateFormat2.format(date)+rd.nextInt(174638);
        postdata.setPostId(postid);

        profile=user.getPhotoUrl().toString();
        //Toast.makeText(y_writePost.this, profile, Toast.LENGTH_SHORT).show();


        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag==1){
                    Toast.makeText(y_writePost.this, "아직 이전 사진을 등록 중입니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(flag==0){
                    final String title=((EditText)findViewById(R.id.et_title)).getText().toString();
                    final String content=((EditText)findViewById(R.id.et_content)).getText().toString();
                    long now1 = System.currentTimeMillis();
                    Date date1 = new Date(now1);
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
                    String getTime1 = dateFormat1.format(date1);
                    postdata.setContentTitle(title);
                    postdata.setWriteId(user.getDisplayName());
                    postdata.setText(content);
                    postdata.setCreateAt(getTime1);
                    postdata.setHeartnum(0);
                    postdata.setImgList(imglist);
                    postdata.setProfile(user.getPhotoUrl().toString());

                    db.collection("posts").document(postid)
                            .set(postdata)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    myRef.child(postid).child("commentNum").setValue(post.getCommentnum())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(y_writePost.this, "게시글을 등록했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(y_writePost.this, "게시글을 등록하지 못했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(y_writePost.this, "게시글을 등록하지 못했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });



                    Intent intent = new Intent(y_writePost.this, y_communityMain.class);
                    startActivity(intent);
                }

            }
        });

        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), y_communityMain.class);
                startActivity(intent);
            }
        });

        iv_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageData.size()<10 && flag==0){
                    loadAlbum();
                    flag=1;
                }

                else if(imageData.size()==10){
                    Toast.makeText(y_writePost.this, "사진은 최대 10장만 등록할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
                else if(flag==1){
                    Toast.makeText(y_writePost.this, "아직 이전 사진을 등록 중입니다", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE){
            file = data.getData();
            imageData.add(new y_writeImage(file));

            StorageReference ref = storageRef.child("posts/"+postid+"/"+(imageData.size()-1));
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
                        String imageurl=downloadUri.toString();
                        imglist.add(imageurl);
                        flag=0;
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
        mLayoutManager = new LinearLayoutManager(y_writePost.this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new y_writeImageAdapter();
        mAdapter.setImage(imageData);
        mVerticalView.setAdapter(mAdapter);
    }
}
