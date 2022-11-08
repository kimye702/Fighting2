package com.moo.fighting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginactivity extends AppCompatActivity {
    private TextView login_make, login_find;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private DatabaseReference databaseIdRef;
    private EditText login_email, login_password;
    private Button login_button;
    private CheckBox login_checkbox;
    String auto_check;


    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Fighting);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_make=(TextView)findViewById(R.id.login_make);
        login_find=(TextView)findViewById(R.id.login_find);
        login_checkbox=(CheckBox)findViewById(R.id.login_checkbox);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Fighting2");

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        if (pref !=null){
            auto_check = pref.getString("check",""); //name이라는 키 값으로 받는 것.
            if (mFirebaseAuth.getCurrentUser() != null && auto_check.equals("true")) {
                // User is signed in (getCurrentUser() will be null if not signed in)
                Intent intent = new Intent(loginactivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                // or do some other stuff that you want to do
            }
        }



        login_email=(EditText) findViewById(R.id.login_email);
        login_password=(EditText) findViewById(R.id.login_password);
        login_button=(Button)findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 요청
                String strEmail=login_email.getText().toString();
                String strPassword=login_password.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(loginactivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //로그인 성공
                                    Toast.makeText(loginactivity.this, "로그인에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(loginactivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); //현재 액티비티 파괴
                                }
                                else {
                                    Toast.makeText(loginactivity.this, "로그인에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        login_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginactivity.this, makeidactivity.class);
                startActivity(intent);
            }
        });

        login_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(loginactivity.this, findidactivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onPause() {
        super.onPause();
        if(login_checkbox.isChecked()==true){
            SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit(); //Editor라는 Inner class가 정의되어 있음
            editor.putString("check","true");
            editor.commit();//이 때 이제 저장이 되는 거임

        }

        else{
            SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit(); //Editor라는 Inner class가 정의되어 있음
            editor.putString("check", "false");
            editor.commit();//이 때 이제 저장이 되는 거임
        }

    }


}
