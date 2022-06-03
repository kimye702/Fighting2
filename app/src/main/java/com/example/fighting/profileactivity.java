package com.example.fighting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class profileactivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private TextView name, email;
    private Button logout, delete;
    FirebaseDatabase mdatabase=FirebaseDatabase.getInstance();

    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mFirebaseAuth=FirebaseAuth.getInstance();
        imageView=(ImageView)findViewById(R.id.image);
        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        logout=(Button)findViewById(R.id.logout);
        delete=(Button)findViewById(R.id.delete);

        storageRef.child("users/"+user.getUid()+"/profileImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(profileactivity.this).load(uri).circleCrop().into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.dog);
            }
        });


        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit(); //Editor라는 Inner class가 정의되어 있음
                editor.putString("check", "false");
                editor.commit();//이 때 이제 저장이 되는 거임
                mFirebaseAuth.signOut();
                finish();
            }
        });

       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AlertDialog.Builder alert_confirm = new AlertDialog.Builder(profileactivity.this);
               alert_confirm.setMessage("정말 계정을 삭제 할까요?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               mDatabaseRef=mdatabase.getReference("Fighting2").child("UserAccount").child(user.getDisplayName());
                               mDatabaseRef.removeValue();

                               StorageReference desertRef = storageRef.child("users").child(user.getUid()+"/").child("profileImage.jpg");
                               desertRef.delete();

                               user.delete()
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               Toast.makeText(profileactivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                               finish();
                                               startActivity(new Intent(getApplicationContext(), loginactivity.class));
                                           }
                                       });
                           }
                       }
               );

               alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       Toast.makeText(profileactivity.this, "취소", Toast.LENGTH_LONG).show();
                   }
               });
               alert_confirm.show();
           }
       });
    }
}