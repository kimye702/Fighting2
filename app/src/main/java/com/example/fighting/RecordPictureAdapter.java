package com.example.fighting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

// 상세 기록 안 사진 리사이클러뷰 담당하는 어댑터
public class RecordPictureAdapter extends RecyclerView.Adapter<RecordPictureAdapter.CustomViewHolder> {
    private ArrayList<String> picDataset;
    private Activity activity;
    private FirebaseSupporter firebaseSupporter;

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CustomViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // 원래꺼
    public RecordPictureAdapter(Activity activity, ArrayList<String> picDataset) {
        this.picDataset = picDataset;
        this.activity = activity;
        firebaseSupporter = new FirebaseSupporter(activity);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public RecordPictureAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardView cardView = (CardView) inflater.inflate(R.layout.item_walk_pictures, parent, false);

        final RecordPictureAdapter.CustomViewHolder customViewHolder = new RecordPictureAdapter.CustomViewHolder(cardView);
        // 카드뷰 누르면 내용 보이게
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToast("뷰 클릭 "+ viewType);
            }
        });


        return customViewHolder;
    }


    // 만든거
    @Override
    public void onBindViewHolder(@NonNull RecordPictureAdapter.CustomViewHolder holder, int position) {
        //Log.e("로그", "바인드뷰 실행");

        CardView cardView = holder.cardView;
        ImageView iv_pictures = cardView.findViewById(R.id.iv_pictures);

        String picture = picDataset.get(position);

        // 이미지뷰에 해당 url의 사진 넣어주기
        // https://firebase.google.com/docs/storage/android/download-files?hl=ko 참고
        // https://jizard.tistory.com/179 참고
        Glide.with(activity).load(picture).placeholder(R.drawable.placeholder)
                .error(R.drawable.error).into(iv_pictures);
        // https://leeph.tistory.com/17 참고 이미지 크기 이미지뷰에 맞추기
        // https://recipes4dev.tistory.com/105 참고
    }

    @Override
    public int getItemCount() {
        return picDataset.size();
    }

    private void startToast(String msg){Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();}
}
