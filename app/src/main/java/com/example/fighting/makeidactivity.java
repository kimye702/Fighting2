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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class makeidactivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private FirebaseDatabase database;
    private EditText make_email, make_password, make_password_check, make_name;
    private Button make_button3, check;
    private int possible=0;


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
        make_name=(EditText) findViewById(R.id.make_name);

        check=(Button)findViewById(R.id.check);
        make_button3=(Button) findViewById(R.id.make_button3);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseRef.child("UserAccount").child(make_name.getText().toString()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);

                        if(value!=null){
                            Toast.makeText(getApplicationContext(),"이미 존재하는 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"사용가능한 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                            possible=1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"에러 발생! 다시 시도해주세요",Toast.LENGTH_SHORT).show();//토스메세지 출력

                    }
                });
            }
        });

        make_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail=make_email.getText().toString();
                String strPassword=make_password.getText().toString();
                String strPasswordCheck=make_password_check.getText().toString();
                String strName=make_name.getText().toString();
                //회원가입 처리 시작
                if(strPasswordCheck.equals(strPassword)){
                    if(possible==1){
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
                                            account.setName(strName);

                                            //setValue : database에 insert 행위
                                            mDatabaseRef.child("UserAccount").child(strName).setValue(account);

                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(strName)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(makeidactivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                            Intent intent1 = new Intent(makeidactivity.this, makeprofileactivity.class);
                                            startActivity(intent1);
                                            finish();

                                        }
                                        else{
                                            try {
                                                throw task.getException();
                                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(makeidactivity.this, "이메일 형식이 유효하지 않습니다", Toast.LENGTH_SHORT).show();
                                            } catch(FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(makeidactivity.this, "이미 가입된 이메일입니다", Toast.LENGTH_SHORT).show();
                                            } catch(Exception e) {
                                                Toast.makeText(makeidactivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }

                    else{
                        Toast.makeText(getApplicationContext(),"닉네임을 다시 확인해주세요.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                    }

                }

                else {
                    Toast.makeText(makeidactivity.this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

}
