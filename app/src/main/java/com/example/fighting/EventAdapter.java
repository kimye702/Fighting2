package com.example.fighting;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0,events);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View getView(int position, @NonNull View converView, @NonNull ViewGroup parent){
        Event event = getItem(position);

        if(converView == null)
            converView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);
        TextView eventCellTV = converView.findViewById(R.id.eventCellTV);

        String eventTitle = event.getName() + " "+ CalendarUtils.formattedTime(event.getTime());
        eventCellTV.setText(eventTitle);
        return converView;
    }



}
