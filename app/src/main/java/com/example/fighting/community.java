package com.example.fighting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class community extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ListActivity fragmentA;
    private ListPictureActivity fragmentB;
    private FragmentTransaction transaction;
    private Button btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

//        Button btn1=(Button)findViewById(R.id.button);
//        Button btn2=(Button)findViewById(R.id.button2);


//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fragmentManager = getSupportFragmentManager();
//
//                fragmentA = new ListActivity();
//
//                transaction = fragmentManager.beginTransaction();
//
//                transaction.replace(R.id.frameLayout, fragmentA).commit();
//            }
//        });
//
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fragmentManager = getSupportFragmentManager();
//
//                fragmentB = new ListPictureActivity();
//
//                transaction = fragmentManager.beginTransaction();
//
//                transaction.replace(R.id.frameLayout, fragmentB).commit();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_free) {
            fragmentManager = getSupportFragmentManager();
            fragmentA = new ListActivity();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, fragmentA).commit();

            return true;
        }else if (id == R.id.menu_picture) {
            fragmentManager = getSupportFragmentManager();
            fragmentB = new ListPictureActivity();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, fragmentB).commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}