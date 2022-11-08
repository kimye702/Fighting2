package com.moo.fighting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class y_writeImageAdapter extends RecyclerView.Adapter<y_writeImageAdapter.y_writeImageHolder> {

    private ArrayList<y_writeImage> writeImages;
    public void setImage(ArrayList<y_writeImage> writeImage) {
        writeImages=writeImage;
    }
    Activity activity;


    @NonNull
    @Override
    public y_writeImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_image, parent, false);
        y_writeImageHolder holder = new y_writeImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull y_writeImageHolder holder, int position) {
        y_writeImage data = writeImages.get(position);
            holder.icon.setImageURI(data.getImage());

    }

    @Override
    public int getItemCount() {
        return writeImages.size();
    }

    public class y_writeImageHolder extends RecyclerView.ViewHolder {
        public ImageView icon;

        public y_writeImageHolder(@NonNull View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.iv_image);
        }
    }
}
