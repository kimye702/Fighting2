package com.moo.fighting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MainViewHolder>{
    private ArrayList<y_postInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private ArrayList playerArrayListArrayList = new ArrayList();
    private final int MORE_INDEX = 2;
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("Fighting2");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    int i=0;
    int flag=0;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cardView;
        MainViewHolder(LinearLayout v) {
            super(v);
            cardView = v;
        }
    }

    public ListAdapter(Activity activity, ArrayList<y_postInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }


    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public ListAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout cardView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_item, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, y_post.class);
                intent.putExtra("postInfo", mDataset.get(mainViewHolder.getAdapterPosition()));
                /*intent.putExtra("postInfo", mDataset.get(mainViewHolder.getAdapterPosition()).getClass());
                intent.putExtra("postId", mDataset.get(mainViewHolder.getAdapterPosition()).getPostId());
                intent.putExtra("writeId", mDataset.get(mainViewHolder.getAdapterPosition()).getWriteId());
                intent.putExtra("title", mDataset.get(mainViewHolder.getAdapterPosition()).getContentTitle());
                intent.putExtra("content", mDataset.get(mainViewHolder.getAdapterPosition()).getText());
                intent.putExtra("createAt", mDataset.get(mainViewHolder.getAdapterPosition()).getCreateAt());
                intent.putExtra("commentNum", Integer.toString(mDataset.get(mainViewHolder.getAdapterPosition()).getCommentnum()));
                intent.putExtra("imgList", mDataset.get(mainViewHolder.getAdapterPosition()).getImgList());
                intent.putExtra("heartNum", Integer.toString(mDataset.get(mainViewHolder.getAdapterPosition()).getHeartnum()));
                intent.putExtra("likeList", mDataset.get(mainViewHolder.getAdapterPosition()).getLikeList());
                intent.putExtra("profile", mDataset.get(mainViewHolder.getAdapterPosition()).getProfile());*/
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.itemmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        LinearLayout cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.tv_title);

        y_postInfo postInfo = mDataset.get(position);
        titleTextView.setText(postInfo.getContentTitle());

        TextView name=cardView.findViewById(R.id.tv_name);
        name.setText(postInfo.getWriteId());

        TextView date=cardView.findViewById(R.id.tv_date);
        date.setText(postInfo.getCreateAt());

        TextView text=cardView.findViewById(R.id.tv_content);
        text.setText(postInfo.getText());

        TextView heartNum=cardView.findViewById(R.id.tv_heartnum);
        heartNum.setText(Integer.toString(postInfo.getHeartnum()));

        TextView commentNum=cardView.findViewById(R.id.tv_commentnum);
        commentNum.setText(Integer.toString(postInfo.getCommentnum()));

        CircleImageView image=cardView.findViewById(R.id.iv_profile);
        storageRef.child("users/"+postInfo.getWriteId()+"/"+"profileImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(activity).load(uri).circleCrop().into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                image.setImageResource(R.drawable.dog);
            }
        });

        TextView imageNum=cardView.findViewById(R.id.imageNum);
        imageNum.setText(Integer.toString(postInfo.getImgList().size()));

        ImageView mainImage= cardView.findViewById(R.id.mainImage);
        storageRef.child("posts/"+postInfo.getPostId()+"/"+"0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(!activity.isDestroyed()){
                    Glide.with(activity).load(uri).centerCrop().into(mainImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                cardView.findViewById(R.id.list2).setVisibility(View.GONE);
                cardView.findViewById(R.id.mainImage).setVisibility(View.GONE);
                cardView.findViewById(R.id.imageNum).setVisibility(View.GONE);
            }
        });

        ImageView heart= cardView.findViewById(R.id.iv_heart);
        db.collection("posts").document(postInfo.getPostId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        });
        (heart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //데이터베이스 위치, images/하나의 게시물 key/starts,startCount ..
                onHeartClicked(db.collection("posts").document(postInfo.getPostId()));

            }
        });

        /*db.collection("posts").document(postInfo.getPostId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    if (((ArrayList<String>) snapshot.getData().get("likeList")).contains(user.getUid())) {
                        heart.setImageResource(R.drawable.redheart);
                    } else {
                        //Toast.makeText(activity, "z", Toast.LENGTH_SHORT).show();
                        heart.setImageResource(R.drawable.heart);
                    }
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_modify:
                        if(user.getDisplayName().equals(mDataset.get(position).getWriteId())){
                            myStartActivity(y_modifyPost.class, mDataset.get(position));
                            return true;
                        }
                        else{

                        }
                    case R.id.action_delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
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
        Intent intent = new Intent(activity, c);
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }

    public void onHeartClicked(DocumentReference postRef) {
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //Toast.makeText(activity, String.valueOf(((ArrayList<String>) document.getData().get("likeList")).size()), Toast.LENGTH_SHORT).show();
                    if((ArrayList<String>) (document.getData().get("likeList"))!=null){
                        if (((ArrayList<String>) (document.getData().get("likeList"))).contains(user.getUid())) {
                            postRef
                                    .update("heartnum", Integer.valueOf(document.get("heartnum").toString())-1)
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
                                    .update("heartnum", Integer.valueOf(document.get("heartnum").toString())+1)
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
                    }
                    else{
                        postRef
                                .update("heartnum", Integer.valueOf(document.get("heartnum").toString())+1)
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
    }
}