package com.example.fighting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventEditActivity extends AppCompatActivity {
    public static Context mContext;
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;

    private LocalTime time;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();

        eventDateTV.setText("날짜: "+(CalendarUtils.selectedDate));
        eventTimeTV.setText("시간: "+CalendarUtils.formattedTime(time));

    }

    private void initWidgets(){
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
    }

    public String readFromFile(String name) throws Exception {
        FileInputStream fileInputStream = openFileInput(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuffer stringBuffer = new StringBuffer();

        String content = null;
        while ((content = reader.readLine()) != null) {
            stringBuffer.append(content + "\n");
        }
        reader.close();
        fileInputStream.close();
        return stringBuffer.toString();
    }
    public void writeToFile(String name, String content) throws Exception {
        FileOutputStream outputStream = openFileOutput(name, MODE_APPEND);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(content);
        writer.write("\n");
        writer.flush();
        writer.close();
        outputStream.close();
    }

    public void saveEventAction(View view) {
        String name = CalendarUtils.formattedDate(CalendarUtils.selectedDate)+".txt";
        String eventName = eventNameET.getText().toString();
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate,time);
        Event.eventsList.add(newEvent);
        try {
            writeToFile(name, eventName);
            Toast.makeText(getApplicationContext(),"/data/data/file/"+name+"에 저장되었습니다!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            e.printStackTrace();
        }

        finish();
    }


}