package com.moo.fighting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class y_modifyImageAdapter extends RecyclerView.Adapter<y_modifyImageAdapter.y_modifyImageHolder> {

    private ArrayList<y_modifyImage> writeImages;
    public void setImage(ArrayList<y_modifyImage> writeImage) {
        writeImages=writeImage;
    }
    Activity activity;

    public y_modifyImageAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public y_modifyImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_modify, parent, false);
        y_modifyImageHolder holder = new y_modifyImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull y_modifyImageHolder holder, int position) {
        y_modifyImage data = writeImages.get(position);
        Glide.with(activity).load(data.getImage()).centerCrop().into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return writeImages.size();
    }

    public class y_modifyImageHolder extends RecyclerView.ViewHolder {
        public ImageView icon;

        public y_modifyImageHolder(@NonNull View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.modifyiv_image);
        }
    }
}
