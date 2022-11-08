package com.moo.fighting.Walk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moo.fighting.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

// 나의 산책 일지 메인 프래그먼트
public class WalkRecordView extends Fragment {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RecordAdapter recordAdapter;
    private ArrayList<TraceInfo> traceList;
    private boolean updating;
    private boolean topScrolled;
    private int all_count, all_time;
    private double all_dist;
    private int count, time;
    private double dist;
    private TextView tv_trace_title, tv_all_count, tv_all_time, tv_all_dist;
    private int once=1;

    public WalkRecordView(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_walk_record, container, false);
        traceList = new ArrayList<>();

        // view에 inflater 해줘야 findViewById 쓸수 잇음
        tv_trace_title = view.findViewById(R.id.tv_trace_title);
        tv_all_count = view.findViewById(R.id.tv_all_count);
        tv_all_dist = view.findViewById(R.id.tv_all_dist);
        tv_all_time = view.findViewById(R.id.tv_all_time);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        recordAdapter = new RecordAdapter(getActivity(), traceList);
        recordAdapter.setOnTraceListener(onTraceListener);

        final RecyclerView recyclerView = view.findViewById(R.id.rv_record);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recordAdapter);
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
                    traceUpdate(false);
                    Log.e("로그", "스크롤 변화");
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    traceUpdate(true);
                    Log.e("로그", "스크롤");
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

//        traceUpdate(true);
//        Log.e("로그", "메인");

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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("로그", "resume");
        traceUpdate(true);
    }

    OnTraceListener onTraceListener = new OnTraceListener() {
        @Override
        public void onDelete() {
            traceUpdate(true);
            Log.e("로그", "WalkRecordView 삭제 성공");
        }
        @Override
        public void onModify(int position) {
            Log.e("로그: ","WalkRecordView 수정 성공");
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 123) {
//
//        }
//    }

    public void traceUpdate(boolean ok) {
        updating = true;
        if(ok){
            count = 0; time = 0; dist = 0;
        }
        db.collection("traces").document(user.getUid()).collection("date")
                .orderBy("createdAt", Query.Direction.DESCENDING).get() // 최근 기록이 위부터 뜨도록
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            traceList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.e("로그", document.getId() + " => " + document.getData());
                                //Log.e("로그", document.getId()+"걸은 시간"+document.getData().get("walkTime"));
                                TraceInfo traceInfo = new TraceInfo(
                                        document.getData().get("contents").toString(),
                                        Integer.valueOf(document.getData().get("walkTime").toString()),
                                        (double) document.getData().get("distance"),
                                        (double) document.getData().get("pace"),
                                        (ArrayList<String>)document.getData().get("pictures"),
                                        new Date(document.getDate("startRun").getTime()),
                                        new Date(document.getDate("createdAt").getTime()));
                                traceList.add(traceInfo);
                                if(ok){
                                    count++;
                                    dist += traceInfo.getDistance();
                                    time += traceInfo.getWalkTime();
                                }
                                //Log.e("로그", "문서 업데이트 성공");
                            }
                            if(ok){
                                all_count = count;
                                all_dist = dist;
                                all_time = time;

                                // 총 산책 기록 설정하는 부분
                                String name = user.getDisplayName();
                                tv_trace_title.setText(name+"의 산책 기록");

                                tv_all_count.setText(all_count+"회");

                                if(all_dist>=1000){
                                    all_dist*=0.001;
                                    String d = String.format("%.2f",all_dist);
                                    tv_all_dist.setText(d+"km");
                                }
                                else{
                                    String d = String.format("%d",(int)all_dist);
                                    tv_all_dist.setText(d+"m");
                                }

                                int h, m, s;
                                h = all_time/3600; all_time%=3600; m = all_time/60; all_time%=60; s = all_time;
                                tv_all_time.setText(h+":"+m+":"+s);
                            }

                            recordAdapter.notifyDataSetChanged(); // 데이터가 바뀐 traceList로 바뀜
                            // 해당 if문을 이거 앞에 해줘야 제대로 업데이트 됨!!!!!!
                        } else {
                            Log.e("로그", "문서 업데이트 실패");
                        }
                        updating = false;
                    }
                });
    }

    private void startActivityForResult(Intent intent) {
    }
}


