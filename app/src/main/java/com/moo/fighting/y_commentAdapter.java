package com.moo.fighting;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class y_commentAdapter extends RecyclerView.Adapter<y_commentAdapter.MainViewHolder> {
    private ArrayList<y_commentInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private ArrayList playerArrayListArrayList = new ArrayList();
    private final int MORE_INDEX = 2;
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mFirebaseAuth=FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    int i=0;
    int flag=0;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cardView;
        MainViewHolder(LinearLayout v) {
            super(v);
            cardView = v;
        }
    }

    public y_commentAdapter(Activity activity, ArrayList<y_commentInfo> myDataset) {
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
    public y_commentAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout cardView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_comment, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        /*
        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });*/

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        LinearLayout cardView = holder.cardView;
        TextView name = cardView.findViewById(R.id.commentname);

        y_commentInfo postInfo = mDataset.get(position);
        name.setText(postInfo.getWriteId());

        TextView content=cardView.findViewById(R.id.commentcontent);
        content.setText(postInfo.getText());

        TextView date=cardView.findViewById(R.id.commentdate);
        date.setText(postInfo.getCreateAt());

        CircleImageView image=cardView.findViewById(R.id.commentprofile);
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

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*
    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        if(user.getDisplayName().equals(mDataset.get(position).getPublisher())){
                            myStartActivity(WritePictureActivity.class, mDataset.get(position));
                            return true;
                        }
                        else{

                        }
                    case R.id.delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }
*/
    /*
    private void myStartActivity(Class c, y_postInfo postInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }*/


}