package com.moo.fighting;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class y_communityMain extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private y_community fragmentA;
    private FragmentTransaction transaction;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);
        fragmentManager = getSupportFragmentManager();
        fragmentA = new y_community();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentA).commit();

    }

}