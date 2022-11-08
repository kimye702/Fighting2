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

import java.util.ArrayList;

public class y_modifyPost extends AppCompatActivity {

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
    private y_modifyImageAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<y_modifyImage> imageData = new ArrayList<>();
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
        setContentView(R.layout.y_modifypost);
        checkButton=(Button)findViewById(R.id.modifycheckButton);
        iv_album=(ImageView)findViewById(R.id.modifyiv_album);
        mVerticalView = (RecyclerView) findViewById(R.id.modifycontentsLayout);
        ib_delete=(ImageButton) findViewById(R.id.modifyib_delete);
        et_title=(EditText) findViewById(R.id.modifyet_title);
        et_content=(EditText) findViewById(R.id.modifyet_content);

        profile=user.getPhotoUrl().toString();
        //Toast.makeText(y_writePost.this, profile, Toast.LENGTH_SHORT).show();

        if(getIntent().getExtras()!=null){
            Intent intent = getIntent();
            post=(y_postInfo) intent.getExtras().getParcelable("postInfo");

            if(post!=null){
                et_title.setText(post.getContentTitle());
                et_content.setText(post.getText());
                imglist=post.getImgList();
                for(int i=0;i<imglist.size();i++){
                    imageData.add(new y_modifyImage(imglist.get(i)));
                }
                mLayoutManager = new LinearLayoutManager(y_modifyPost.this, LinearLayoutManager.HORIZONTAL, false);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mVerticalView.setLayoutManager(mLayoutManager);
                mAdapter = new y_modifyImageAdapter(y_modifyPost.this);
                mAdapter.setImage(imageData);
                mVerticalView.setAdapter(mAdapter);
                postid=post.getPostId();
                postdata.setPostId(postid);
            }
        }

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag==1){
                    Toast.makeText(y_modifyPost.this, "아직 이전 사진을 등록 중입니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(flag==0){
                    final String title=((EditText)findViewById(R.id.modifyet_title)).getText().toString();
                    final String content=((EditText)findViewById(R.id.modifyet_content)).getText().toString();
                    postdata.setContentTitle(title);
                    postdata.setText(content);
                    postdata.setImgList(imglist);

                    db.collection("posts").document(postid)
                            .update("contentTitle", title)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("posts").document(postid)
                                            .update("text", content)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("posts").document(postid)
                                                            .update("imgList", imglist)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(y_modifyPost.this, "게시글을 수정했습니다", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(y_modifyPost.this, "게시글을 수정하지 못했습니다", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(y_modifyPost.this, "게시글을 수정하지 못했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(y_modifyPost.this, "게시글을 수정하지 못했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });

                    Intent intent = new Intent(getApplicationContext(), y_communityMain.class);
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
                    Toast.makeText(y_modifyPost.this, "사진은 최대 10장만 등록할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
                else if(flag==1){
                    Toast.makeText(y_modifyPost.this, "아직 이전 사진을 등록 중입니다", Toast.LENGTH_SHORT).show();
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
                        imageData.add(new y_modifyImage(imageurl));
                        flag=0;
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
        mLayoutManager = new LinearLayoutManager(y_modifyPost.this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new y_modifyImageAdapter(y_modifyPost.this);
        mAdapter.setImage(imageData);
        mVerticalView.setAdapter(mAdapter);
    }
}
