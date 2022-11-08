package com.moo.fighting.Walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moo.fighting.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


// 상세 기록 화면 담당하는 액티비티 + RecordPictureAdapter 사용
public class DetailRecord extends AppCompatActivity{
    private TraceInfo traceInfo;
    private TextView tv_walkTime;
    private TextView tv_distance;
    private TextView tv_pace;
    private TextView tv_contents;
    private ArrayList<String> pictures;
    private RecyclerView recyclerView;
    private RecordPictureAdapter walkPictureAdapter;
    private FirebaseSupporter firebaseSupporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordpost);

        tv_walkTime = findViewById(R.id.tv_walkTime);
        tv_distance = findViewById(R.id.tv_distance);
        tv_pace = findViewById(R.id.tv_pace);
        tv_contents = findViewById(R.id.tv_contents);

        traceInfo = (TraceInfo) getIntent().getSerializableExtra("traceInfo");
        pictures = traceInfo.getPictures();

        firebaseSupporter = new FirebaseSupporter(this);

        // 리사이클러뷰 연결
        // https://blog.hexabrain.net/363 참고
        recyclerView = findViewById(R.id.rv_pictures);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        walkPictureAdapter = new RecordPictureAdapter(this, pictures);

        recyclerView.setAdapter(walkPictureAdapter);

        setOnTraceListener(onTraceListener);

        uiUpdate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("로그", "requestCode : " + requestCode);
//        Log.e("로그", "resultCode : " + resultCode);
//        Log.e("로그", "Activity.RESULT_OK : " + RESULT_OK);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    traceInfo = (TraceInfo) data.getSerializableExtra("traceInfo");
                    uiUpdate();
//                    Log.e("로그", "uiUpdate  "+traceInfo.getContents());
                }
                break;
        }
    }

    public void setOnTraceListener(OnTraceListener onTraceListener){
        firebaseSupporter.setOnTraceListener(onTraceListener);
    }

    OnTraceListener onTraceListener = new OnTraceListener() {
        @Override
        public void onDelete() {
            finish();
            Log.e("로그", "DetailRecord 삭제 성공");
        }
        @Override
        public void onModify(int position) {
            Log.e("로그: ","DetailRecord 수정 성공");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trace, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vanish:
                firebaseSupporter.storageDelete(traceInfo);
//                finish();
                return true;

            case R.id.change:
                myStartActivity(ModifyRecordActivity.class, traceInfo);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void uiUpdate(){
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

        tv_contents.setText(traceInfo.getContents());

        walkPictureAdapter.notifyDataSetChanged();
    }

    private void myStartActivity(Class c, TraceInfo traceInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("traceInfo", traceInfo);
        startActivityForResult(intent, 0);
    }

}
