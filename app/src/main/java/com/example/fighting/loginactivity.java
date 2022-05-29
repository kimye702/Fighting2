package com.example.fighting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_make=(TextView)findViewById(R.id.login_make);
        login_find=(TextView)findViewById(R.id.login_find);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Fighting2");


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
}
