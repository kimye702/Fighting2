package com.moo.fighting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class y_community extends Fragment {
    private static final String TAG = "ListActivity";
    private FirebaseFirestore firebaseFirestore;
    private ListAdapter listAdapter;
    private ArrayList<y_postInfo> postList;
    private boolean updating;
    private boolean topScrolled;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Activity activity;
    StorageReference storageRef = storage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");

    public y_community() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.y_post_list, container, false);
        postList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        listAdapter = new ListAdapter(getActivity(), postList);
        listAdapter.setOnPostListener(onPostListener);


        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        view.findViewById(R.id.floatingButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.floatingButton:
                    myStartActivity(y_writePost.class);
                    break;
            }
        }
    };

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
        CollectionReference collectionReference = db.collection("posts");

        db.collection("posts").orderBy("createAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        postList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            y_postInfo info=new y_postInfo(document.getData().get("profile").toString(),
                                    document.getData().get("contentTitle").toString(),
                                    document.getData().get("text").toString(),
                                    document.getData().get("writeId").toString(),
                                    document.getData().get("createAt").toString(),
                                    Integer.valueOf(document.getData().get("heartnum").toString()),
                                    Integer.valueOf(document.getData().get("commentnum").toString()),
                                    document.getData().get("postId").toString());
                            //Toast.makeText(activity, document.getData().get("createAt").toString(), Toast.LENGTH_SHORT).show();
                            info.setImgList((ArrayList<String>) document.getData().get("imgList"));
                            info.setLikeList((ArrayList<String>) document.getData().get("likeList"));
                            postList.add(info);

                        }
                        listAdapter.notifyDataSetChanged();
                        updating = false;
                    }
                });

        /*collectionReference.orderBy("createAt", Query.Direction.DESCENDING).whereLessThan("createAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                postList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                y_postInfo info=new y_postInfo(document.getData().get("profile").toString(),
                                        document.getData().get("contentTitle").toString(),
                                        document.getData().get("text").toString(),
                                        document.getData().get("writeId").toString(),
                                        document.getData().get("createAt").toString(),
                                        Integer.valueOf(document.getData().get("heartnum").toString()),
                                        Integer.valueOf(document.getData().get("commentnum").toString()),
                                        document.getData().get("postId").toString());
                                info.setImgList((ArrayList<String>) document.getData().get("imgList"));
                                postList.add(info);
                            }
                            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            mVerticalView.setLayoutManager(mLayoutManager);
                            mAdapter = new y_imageAdapter(getActivity());
                            mAdapter.setImages(ImgList);
                            mVerticalView.setAdapter(mAdapter);
                            listAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });*/
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }
}