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

public class y_imageAdapter extends RecyclerView.Adapter<y_imageAdapter.y_imageHolder> {

    private ArrayList<y_image> Images;

    public void setImages(ArrayList<y_image> Image) {
        this.Images=Image;
    }
    private Activity activity;

    public y_imageAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public y_imageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_post, parent, false);
        y_imageHolder holder = new y_imageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull y_imageHolder holder, int position) {
        y_image data = Images.get(position);
        //Toast.makeText(activity, data.getWimage(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(activity, data.getWimage(), Toast.LENGTH_SHORT).show();
        Glide.with(activity).load(data.getWimage()).centerCrop().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return Images.size();
    }

    public class y_imageHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public y_imageHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.iv_wimage);
        }
    }
}
