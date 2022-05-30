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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class findidactivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText find_name1, find_name2, find_email  ;
    private Button find_email_button, find_password_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_id);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Fighting2");
        find_name1=(EditText)findViewById(R.id.find_name1);
        find_email_button=(Button)findViewById(R.id.find_email_button);
        find_name2=(EditText)findViewById(R.id.find_name2);
        find_email=(EditText)findViewById(R.id.find_email);
        find_password_button=(Button)findViewById(R.id.find_password_button);

        find_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseRef.child("UserAccount").child(find_name1.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email=snapshot.child("emailId").getValue(String.class);
                        if(email!=null){
                            Toast.makeText(getApplicationContext(),"이메일은 "+email+"입니다"
                                    ,Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"존재하지 않는 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"에러 발생! 다시 시도해주세요",Toast.LENGTH_SHORT).show();//토스메세지 출력

                    }
                });
            }
        });

        find_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName=((EditText)findViewById(R.id.find_name2)).toString();

                mDatabaseRef.child("UserAccount").child(find_name2.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email=snapshot.child("emailId").getValue(String.class);
                        String strEmail=find_email.getText().toString();

                        if(email.equals(strEmail)){
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseAuth.sendPasswordResetEmail(strEmail.trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(findidactivity.this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), loginactivity.class));
                                            } else {
                                                Toast.makeText(findidactivity.this, "메일 보내기 실패!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"닉네임과 이메일 정보가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"에러 발생! 다시 시도해주세요",Toast.LENGTH_SHORT).show();//토스메세지 출력
                    }
                });
            }
        });
    }
}
