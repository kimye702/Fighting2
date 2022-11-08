package com.moo.fighting.Walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moo.fighting.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ModifyRecordActivity extends AppCompatActivity {
    private String TAG = "로그";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private TextView tv_walkTime, tv_distance, tv_pace;
    private EditText et_contents;
    private TraceInfo traceInfo;
    private int successCount;
    private boolean success;

    private RecyclerView recyclerView;
    private RecordPictureAdapter walkPictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordpost_modify);

        // 내꺼 텍스트뷰, 에디트뷰
        tv_walkTime = findViewById(R.id.tv_walkTime);
        tv_distance = findViewById(R.id.tv_distance);
        tv_pace = findViewById(R.id.tv_pace);
        et_contents = findViewById(R.id.et_contents);

        findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.btn_confirm).setOnClickListener(onClickListener);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        traceInfo = (TraceInfo) getIntent().getSerializableExtra("traceInfo");
        ArrayList<String> pictureList = traceInfo.getPictures();

        // 리사이클러뷰 연결
        // https://blog.hexabrain.net/363 참고
        recyclerView = findViewById(R.id.rv_pictures);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        walkPictureAdapter = new RecordPictureAdapter(this, pictureList);

        recyclerView.setAdapter(walkPictureAdapter);
        //walkPictureAdapter.notifyDataSetChanged();

        postInit();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 0:
//                if(success){
//
//                }
//                break;
////            case 123:
////                if (resultCode == Activity.RESULT_OK) {
////                    String path = data.getStringExtra(INTENT_PATH);
////                    pathList.set(parent.indexOfChild((View) selectedImageVIew.getParent()) - 1, path);
////                    Glide.with(this).load(path).override(1000).into(selectedImageVIew);
////                }
////                break;
//        }
//    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.btn_confirm:
                    storageUpload();
                    break;
            }
        }
    };


    private void storageUpload() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dr;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String datedetail= sdf.format(traceInfo.getCreatedAt());
        if(traceInfo==null)
            dr = db.collection("traces").document(user.getUid()).collection("date").document();
        else
            dr = db.collection("traces").document(user.getUid()).collection("date").document(datedetail);
        final DocumentReference documentReference = dr;

        if(traceInfo!=null){
            final String contents = et_contents.getText().toString();
            final int walktime = traceInfo.getWalkTime();
            final double distance = traceInfo.getDistance();
            final double pace = traceInfo.getPace();
            final Date startRun = traceInfo.getStartRun();
            final Date endRun = traceInfo.getCreatedAt();
            //pathList = traceInfo.getPictures();

            ArrayList<String> contentList = new ArrayList<>();
            ArrayList<String> pictureList = traceInfo.getPictures();

            FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

            for(int i=0;i<pictureList.size();i++){
                contentList.add(pictureList.get(i));
                StorageReference imgRef= firebaseStorage.getReference("uploads/"+user.getUid()+"/"+datedetail+"/"+i+".png");
                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", ""+(contentList.size()-1)).build();

                contentList.set(i, pictureList.get(i));
                successCount++;

                if (pictureList.size()==successCount) { // 이렇게 넣어야 저장소 위치로 미리 업로드 방지 가능.
                    TraceInfo traceInfo = new TraceInfo(contents, walktime, distance, pace, contentList, startRun, endRun);
                    Log.e("로그", "trace 성공");
                    storeUploader(traceInfo);
                }
            }

            // 이미지 없을때도 db 업로드 되도록
            if(pictureList.size()==0){
                TraceInfo traceInfo = new TraceInfo(contents, walktime, distance, pace, contentList, startRun, endRun);
                Log.e("로그", "trace 성공");
                storeUploader(traceInfo);
            }
        }else{
            // 이게 계속 실행돼서 실시간 detail 안바뀐거였음...
            finish();
        }
    }

    private void storeUploader(TraceInfo traceInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String detail= sdf.format(traceInfo.getCreatedAt());
        db.collection("traces").document(user.getUid()).collection("date").document(detail).set(traceInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.e("로그", "수정 정보 등록 성공");
//                        Log.e("로그", "result"+Activity.RESULT_OK);
//                       success = true;
                        Intent intent = new Intent();
                        intent.putExtra("traceInfo", traceInfo);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("로그", "수정 정보 등록 실패");
                    }
                });
    }

    private void postInit() {
        if (traceInfo != null) {
            et_contents.setText(traceInfo.getContents());

            String end_date = new SimpleDateFormat("yyyy/MM/dd").format(traceInfo.getCreatedAt());
            String start_date = new SimpleDateFormat("yyyy/MM/dd").format(traceInfo.getStartRun());
            String end = new SimpleDateFormat("k:mm").format(traceInfo.getCreatedAt());
            String start = new SimpleDateFormat("k:mm").format(traceInfo.getStartRun());
            int h, m, s, walkTime;
            String walk;

            walkTime = traceInfo.getWalkTime();
            h = walkTime/3600; walkTime%=3600; m = walkTime/60; walkTime%=60; s = walkTime;

            if(h==0){
                if(m==0)
                    walk = "("+s+"초)";
                else
                    walk = "("+m+"분 "+s+"초)";
            }
            else
                walk = "("+h+"시간 "+m+"분 "+s+"초)";

            if(end_date.equals(start_date))
                tv_walkTime.setText(end_date+"  "+start+" ~ "+end+" "+walk);
            else
                tv_walkTime.setText(start_date+" "+start+" ~ "+end_date+" "+end+" "+walk);

            double distance = traceInfo.getDistance();

            if(distance>=1000){
                distance*=0.001;
                String d = String.format("%.2f",distance);
                tv_distance.setText("산책 거리 : "+d+"km");
            }
            else{
                String d = String.format("%d",(int)traceInfo.getDistance());
                tv_distance.setText("산책 거리 : "+d+"m");
            }

            String pace = String.format("%.2f",traceInfo.getPace());
            tv_pace.setText("평균 산책 속도 : "+pace+"m/s");
        }
    }
}