package com.example.fighting.Walk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fighting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// 나의 산책 일지 리사이클러뷰 관리하는 어댑터
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.CustomViewHolder> {
    private ArrayList<TraceInfo> mDataset;
    private Activity activity;
    private FirebaseSupporter firebaseSupporter;
    private OnTraceListener onTraceListener;
    private final int MORE_INDEX = 2;
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CustomViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // 원래꺼
    public RecordAdapter(Activity activity, ArrayList<TraceInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        firebaseSupporter = new FirebaseSupporter(activity);
    }

    public void setOnTraceListener(OnTraceListener onTraceListener){
        firebaseSupporter.setOnTraceListener(onTraceListener);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }


    @NonNull
    @Override
    public RecordAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        final CustomViewHolder customViewHolder = new CustomViewHolder(cardView);
        // 카드뷰 누르면 내용 보이게
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailRecord.class);
                intent.putExtra("traceInfo", (Serializable) mDataset.get(customViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        // 옆에 세로 점세개 메뉴버튼 누르면 수정혹은 삭제되게
        cardView.findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, customViewHolder.getAdapterPosition());
            }
        });


        // 카드뷰 옆의  세점이 안보였었음 -> 해결
        // https://ryeggg.tistory.com/38 참고

        return customViewHolder;
    }


    // 만든거
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //Log.e("로그", "바인드뷰 실행");
        CardView cardView = holder.cardView;
        TextView tv_title = cardView.findViewById(R.id.tv_title);

        TraceInfo traceInfo = mDataset.get(position);
        String end_date = new SimpleDateFormat("yyyy/MM/dd").format(traceInfo.getCreatedAt());
        String start_date = new SimpleDateFormat("yyyy/MM/dd").format(traceInfo.getStartRun());
        String end = new SimpleDateFormat("k시 mm분").format(traceInfo.getCreatedAt());
        String start = new SimpleDateFormat("k시 mm분").format(traceInfo.getStartRun());
        // https://developer-joe.tistory.com/16 참고 시간관련

        if(end_date.equals(start_date))
            tv_title.setText(end_date+" "+start+" ~ "+end);
        else
            tv_title.setText(start_date+" "+start+" ~ "+end_date+" "+end);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // 세로 점 세개 누르면 등장하는 메뉴 팝업으로!!
    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.change:
//                        firebaseSupporter.change(position);
//                        Log.e("로그", "수정 실행1");
//                        return true;

                    case R.id.vanish:
                        firebaseSupporter.storageDelete(mDataset.get(position));
                        Log.e("로그", "삭제 실행1");
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.delete, popup.getMenu());
        popup.show();
    }

//    private void myStartActivity(Class c, TraceInfo traceInfo) {
//        Intent intent = new Intent(activity, c);
//        intent.putExtra("traceInfo", traceInfo);
//        activity.startActivityForResult(intent, 123);
//    }

}