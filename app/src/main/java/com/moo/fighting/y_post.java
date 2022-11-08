package com.moo.fighting;

import static com.moo.fighting.Util.showToast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class y_post extends AppCompatActivity {
    private String postid, writeid, createAt, title, content, commentnum, heartNum, profile;
    private ArrayList<String> imglist;
    private ArrayList<String> likelist;

    private CircleImageView postprofile;
    private TextView postname, postdate, posttitle, postcontent, postheartnum, postcommentnum;
    private ImageView send, postmenu;
    private EditText et_comment;

    private ArrayList<y_image> ImgList = new ArrayList<>();
    private RecyclerView mVerticalView;
    private y_imageAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    DocumentReference ref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");
    FirebaseHelper firebaseHelper;

    private static final String TAG = "ListActivity";
    private FirebaseFirestore firebaseFirestore;
    private y_commentAdapter listAdapter;
    private ArrayList<y_commentInfo> postList;
    private boolean updating;
    private boolean topScrolled;

    Activity activity;

    private y_commentInfo data;
    private y_postInfo post;
    String comment;
    int flag=0;

    public y_post() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.y_post);
        Intent intent = getIntent();


        postprofile=(CircleImageView)findViewById(R.id.postprofile);
        postname=(TextView)findViewById(R.id.postname);
        postdate=(TextView)findViewById(R.id.postdate);
        posttitle=(TextView)findViewById(R.id.posttitle);
        postcontent=(TextView)findViewById(R.id.postcontent);
        postheartnum=(TextView)findViewById(R.id.postheartnum);
        postcommentnum=(TextView)findViewById(R.id.postcommentnum);
        send=(ImageView)findViewById(R.id.send);
        et_comment=(EditText)findViewById(R.id.et_comment);
        postmenu=(ImageView)findViewById(R.id.postmenu);

        postmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        post=(y_postInfo) intent.getExtras().getParcelable("postInfo");
        postid=post.getPostId();
        writeid=post.getWriteId();
        createAt=post.getCreateAt();
        imglist=post.getImgList();
        title=post.getContentTitle();
        content=post.getText();
        commentnum=String.valueOf(post.getCommentnum());
        likelist=post.getLikeList();
        profile=post.getProfile();
        /*postid=intent.getStringExtra("postId");
        writeid=intent.getStringExtra("writeId");
        createAt=intent.getStringExtra("createAt");
        imglist=intent.getStringArrayListExtra("imgList");
        title=intent.getStringExtra("title");
        content=intent.getStringExtra("content");
        commentnum=intent.getStringExtra("commentNum");*/
        heartNum=String.valueOf(post.getHeartnum());

        //Toast.makeText(y_post.this, heartNum, Toast.LENGTH_SHORT).show();


        comment=commentnum;

        Glide.with(y_post.this).load(profile).circleCrop().into(postprofile);
        postname.setText(writeid);
        postcontent.setText(content);
        postdate.setText(createAt);
        postheartnum.setText(heartNum);
        postcommentnum.setText(commentnum);
        posttitle.setText(title);

        ImageView heart= (ImageView) findViewById(R.id.postheart);


        db.collection("posts").document(postid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    if((ArrayList<String>)snapshot.getData().get("likeList")!=null){
                        boolean check=((ArrayList<String>)snapshot.getData().get("likeList")).contains(user.getUid());
                        if (check) {
                            //Toast.makeText(activity, state.toString(), Toast.LENGTH_SHORT).show();
                            (heart).setImageResource(R.drawable.redheart);
                            (postheartnum).setText(snapshot.getData().get("heartnum").toString());
                        } else {
                            //Toast.makeText(activity, "z", Toast.LENGTH_SHORT).show();
                            (heart).setImageResource(R.drawable.heart);
                            (postheartnum).setText(snapshot.getData().get("heartnum").toString());
                        }
                    }
                    else{
                        (heart).setImageResource(R.drawable.heart);
                    }
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });

        db.collection("posts").document(postid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                            (postcommentnum).setText(snapshot.getData().get("commentnum").toString());
                        }
                    //Log.d(TAG, "Current data: " + snapshot.getData());
            }
        });

        /*db.collection("posts").document(postid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if((ArrayList<String>)document.getData().get("likeList")!=null){
                        boolean check=((ArrayList<String>)document.getData().get("likeList")).contains(user.getUid());
                        if (check) {
                            //Toast.makeText(activity, state.toString(), Toast.LENGTH_SHORT).show();
                            (heart).setImageResource(R.drawable.redheart);
                        } else {
                            //Toast.makeText(activity, "z", Toast.LENGTH_SHORT).show();
                            (heart).setImageResource(R.drawable.heart);
                        }
                    }
                    else{
                        (heart).setImageResource(R.drawable.heart);
                    }
                    //Toast.makeText(activity, state.toString(), Toast.LENGTH_SHORT).show();

                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/

        (heart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeartClicked(db.collection("posts").document(postid));
            }
        });

        ref = db.collection("posts").document(postid);

        mVerticalView = (RecyclerView) findViewById(R.id.imagelist);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                Random rd=new Random();
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
                String commentId = dateFormat2.format(date)+rd.nextInt(174638);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
                String getTime1 = dateFormat1.format(date);
                data=new y_commentInfo(user.getPhotoUrl().toString(), commentId, et_comment.getText().toString()
                        , user.getDisplayName(), getTime1);

                    myRef.child(postid).child("comment").child(commentId).setValue(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    flag=0;
                                    onSendClicked(db.collection("posts").document(postid));
                                    Toast.makeText(y_post.this, "댓글 등록이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    // ...
                                    Toast.makeText(y_post.this, "댓글 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
            }
        });

        if(imglist!=null){
            int size=imglist.size();
            for(int i=0;i<size;i++){
                ImgList.add(new y_image(imglist.get(i)));
                //Toast.makeText(y_post.this, imglist.get(i), Toast.LENGTH_SHORT).show();
            }
        }

        mLayoutManager = new LinearLayoutManager(y_post.this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new y_imageAdapter(y_post.this);
        mAdapter.setImages(ImgList);
        mVerticalView.setAdapter(mAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        listAdapter = new y_commentAdapter(y_post.this, postList);
        listAdapter.setOnPostListener(onPostListener);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commentList);
        //findViewById(R.id.floatingButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(y_post.this));
        recyclerView.setAdapter(listAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        postsUpdate(false);
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(y_postInfo postInfo) {
            Log.e("로그: ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그: ","수정 성공");
        }
    };

    private void postsUpdate(final boolean clear) {
        updating = true;
        String date = postList.size() == 0 || clear ? new Date().toString() : postList.get(postList.size() - 1).getCreateAt().toString();

        Query myTopPostsQuery = myRef.child(postid).child("comment")
                .orderByChild("createAt");
        myRef.child(postid).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(flag==0){
                    if(dataSnapshot.exists()){
                        postList.clear();
                        if(clear){
                            postList.clear();
                        }
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // TODO: handle the post
                            y_commentInfo info=new y_commentInfo(postSnapshot.child("profile").getValue().toString(),
                                    postSnapshot.child("commentId").getValue().toString(),
                                    postSnapshot.child("text").getValue().toString(),
                                    postSnapshot.child("writeId").getValue().toString(),
                                    postSnapshot.child("createAt").getValue().toString());
                            postList.add(info);
                        }
                        listAdapter.notifyDataSetChanged();

                    }
                    else{
                    }
                    updating = false;
                    flag=1;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }


    public void onHeartClicked(DocumentReference postRef) {
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //Toast.makeText(activity, String.valueOf(((ArrayList<String>) document.getData().get("likeList")).size()), Toast.LENGTH_SHORT).show();
                    if ((ArrayList<String>) (document.getData().get("likeList")) != null) {
                        if (((ArrayList<String>) (document.getData().get("likeList"))).contains(user.getUid())) {
                            postRef
                                    .update("heartnum", Integer.valueOf(document.get("heartnum").toString()) - 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            postRef.update("likeList", FieldValue.arrayRemove(user.getUid()));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                        } else {
                            postRef
                                    .update("heartnum", Integer.valueOf(document.get("heartnum").toString()) + 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            postRef.update("likeList", FieldValue.arrayUnion(user.getUid()));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w(TAG, "Error updating document", e);
                                        }
                                    });

                        }
                    } else {
                        postRef
                                .update("heartnum", Integer.valueOf(document.get("heartnum").toString()) + 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        postRef.update("likeList", FieldValue.arrayUnion(user.getUid()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }

                } else {

                }
            }
        });



    /*private void myStartActivity(Class c) {
        Intent intent = new Intent(y_postInfo.this, c);
        startActivityForResult(intent, 0);
    }*/

    }

    public void onSendClicked(DocumentReference postRef) {
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //Toast.makeText(activity, String.valueOf(((ArrayList<String>) document.getData().get("likeList")).size()), Toast.LENGTH_SHORT).show();
                    postRef
                            .update("commentnum", Integer.valueOf(document.get("commentnum").toString()) + 1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Log.w(TAG, "Error updating document", e);
                                }
                            });
                } else {

                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu a_menu, View a_view, ContextMenu.ContextMenuInfo a_menuInfo) {
        ((Activity) a_view.getContext()).getMenuInflater().inflate(R.menu.y_menu, a_menu);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(y_post.this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_modify:
                        if(user.getDisplayName().equals(post.getWriteId())){
                            myStartActivity(y_modifyPost.class, post);
                            //Toast.makeText(y_post.this, "잠옴", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else{

                        }
                    case R.id.action_delete:
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            if(user.getDisplayName().equals(writeid)){
                                firebaseFirestore.collection("posts").document(postid)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                StorageReference storageRef = storage.getReference();

                                                final String id = postid;
                                                ArrayList<String> contentsList = post.getImgList();
                                                int size=contentsList.size();

                                                for (int i = 0; i < size; i++) {
                                                    String contents = contentsList.get(i);
                                                    //if (contents) {
                                                        StorageReference desertRef = storageRef.child("posts/" + id + "/" + i);
                                                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                myRef.child(postid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        showToast(y_post.this, "게시글을 삭제하였습니다");
                                                                        Intent intent = new Intent(y_post.this, y_communityMain.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        System.out.println("error: "+e.getMessage());
                                                                        showToast(y_post.this, "게시글을 삭제하지 못하였습니다");
                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                showToast(y_post.this, "Error");
                                                            }
                                                        });
                                                    //}
                                                }
                                                //onPostListener.onDelete(postInfo);
                                                //postsUpdate();
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showToast(y_post.this, "게시글을 삭제하지 못하였습니다");
                                            }
                                        });
                            }

                            else{
                                showToast(y_post.this, "작성자만 글을 삭제하거나 수정할 수 있습니다");
                            }


                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.y_menu, popup.getMenu());
        popup.show();
    }
    private void myStartActivity(Class c, y_postInfo postInfo) {
        Intent intent = new Intent(y_post.this, c);
        intent.putExtra("postInfo", postInfo);
        (y_post.this).startActivity(intent);
    }
}
