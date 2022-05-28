package com.example.fighting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class makeidactivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText make_email, make_password, make_password_check;
    private Button make_button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_id);
        Intent intent = getIntent();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Fighting2");

        make_email=(EditText) findViewById(R.id.make_email);
        make_password=(EditText) findViewById(R.id.make_password);
        make_password_check=(EditText) findViewById(R.id.make_password_check);

        make_button3=(Button) findViewById(R.id.make_button3);

        make_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작
                String strEmail=make_email.getText().toString();
                String strPassword=make_password.getText().toString();
                String strPasswordCheck=make_password_check.getText().toString();

                if(strPasswordCheck.equals(strPassword)){
                    //Firebase Auth 진행
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(
                            makeidactivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                        UserAccount account=new UserAccount();
                                        account.setIdToken(firebaseUser.getUid());
                                        account.setEmailId(firebaseUser.getEmail());
                                        account.setPassword(strPassword);

                                        //setValue : database에 insert 행위
                                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                                        Toast.makeText(makeidactivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();

                                        Intent intent1 = new Intent(makeidactivity.this, makeprofileactivity.class);
                                        startActivity(intent1);

                                    }
                                    else{
                                        Toast.makeText(makeidactivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                else {
                    Toast.makeText(makeidactivity.this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

}
