package com.example.fighting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// 나의 산책일지 프래그먼트로 띄우기 위한 액티비티
public class MyWalkRecord extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private WalkRecordView subfragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);
        fragmentManager = getSupportFragmentManager();
        subfragment = new WalkRecordView();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_record,subfragment).commit();
    }
}