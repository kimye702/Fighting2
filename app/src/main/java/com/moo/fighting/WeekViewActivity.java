package com.moo.fighting;

import static com.moo.fighting.CalendarUtils.daysInWeekArray;
import static com.moo.fighting.CalendarUtils.monthYearFromDate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalenderAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    Button button;
    Button finish1;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();
        button = findViewById(R.id.button1);
        finish1 = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() { // 클릭리스너 생성
            @Override // 부모 메소드 재정의
            public void onClick(View v) { // 클릭 이벤트 처리

                String context = null;
                try {
                    String dir = getFilesDir().getAbsolutePath();
                    File f0 = new File(dir, (CalendarUtils.formattedDate(CalendarUtils.selectedDate))+".txt");
                    if(f0.exists()==false) {
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText("아직 계획이 없군요...\n동물과의 추억을 선물해주세요!");
                    }
                    else {
                    context = ((EventEditActivity) EventEditActivity.mContext).readFromFile(CalendarUtils.formattedDate(CalendarUtils.selectedDate) + ".txt");
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        finish1.setOnClickListener(new View.OnClickListener() { // 클릭리스너 생성
            @Override // 부모 메소드 재정의
            public void onClick(View v) { // 클릭 이벤트 처리

                String context = null;
                try {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("");
                    String dir = getFilesDir().getAbsolutePath();
                    File f0 = new File(dir, (CalendarUtils.formattedDate(CalendarUtils.selectedDate))+".txt");
                    boolean d0 = f0.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalenderAdapter calenderAdapter = new CalenderAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calenderAdapter);
        setEventAdapter();

    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void newEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

}