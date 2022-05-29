package com.example.fighting;

import static com.example.cal_project1.CalendarUtils.daysInMonthArray;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity_min extends AppCompatActivity implements com.example.cal_project1.CalenderAdapter.OnItemListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    LocalDate selectedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_min);
        initWidgets();
        selectedDate = LocalDate.now();
        com.example.cal_project1.CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

    }
    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");
        return date.format(formatter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(com.example.cal_project1.CalendarUtils.selectedDate));

        ArrayList<LocalDate> daysInMonth = daysInMonthArray(com.example.cal_project1.CalendarUtils.selectedDate);

        com.example.cal_project1.CalenderAdapter calenderAdapter = new com.example.cal_project1.CalenderAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calenderAdapter);
    }


    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view){
        com.example.cal_project1.CalendarUtils.selectedDate = com.example.cal_project1.CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view){
        com.example.cal_project1.CalendarUtils.selectedDate = com.example.cal_project1.CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date!=null) {
            com.example.cal_project1.CalendarUtils.selectedDate = date;
            setMonthView();
        }
        startActivity(new Intent(this, com.example.cal_project1.WeekViewActivity.class));
    }

    public void weeklyAction(View view) {
        startActivity(new Intent(this, com.example.cal_project1.WeekViewActivity.class));
    }
}