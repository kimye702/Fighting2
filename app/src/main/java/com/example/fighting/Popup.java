package com.example.fighting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;

// 산책 끝났을때 팝업띄우는 액티비티
public class Popup extends Activity {
    private TextView tv_walk;
    private EditText et_walk;
    private Button btn_walk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        tv_walk = (TextView) findViewById(R.id.tv_walk);
        et_walk = (EditText) findViewById(R.id.et_walk);
        btn_walk = (Button) findViewById(R.id.btn_walk);

        tv_walk.setHint("내용을 입력하세요");

        // 확인 버튼 클릭
        btn_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data = et_walk.getText().toString();
                Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                intent.putExtra("contents",data);
                setResult(RESULT_OK,intent);

                // 팝업 닫기
                finish();
            }
        });
    }

    // 바깥 레이어 클릭시 닫히지 않게
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    // 안드로이드 뒤로가기 버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }

}
