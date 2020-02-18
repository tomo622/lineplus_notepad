package com.lineplus.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MemoActivity extends AppCompatActivity {
    public static final String INTENT_REQ_NEW = "INTENT_REQ_NEW";
    public static final String INTENT_REQ_ITEM = "INTENT_REQ_ITEM";
    private boolean isNew;
    private MemoItem memoItem;

    private InputMethodManager imm;

    private ConstraintLayout constLayout_main;
    private ConstraintLayout constLayout_back;
    private ImageView img_back;
    private TextView txt_back;
    private TextView txt_date;
    private EditText edit_title;
    private EditText edit_content;
    private ImageButton imgBnt_deleteMemo;
    private ImageButton imgBtn_addMemo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        //////////////////////////////////////////////////
        // 데이터 얻기
        //////////////////////////////////////////////////
        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_REQ_NEW)) {
            isNew = intent.getBooleanExtra(INTENT_REQ_NEW, false);
        }
        if(isNew){
            memoItem = new MemoItem();
        }
        else{
            if(intent.hasExtra(INTENT_REQ_ITEM)){
                memoItem = (MemoItem) intent.getSerializableExtra(INTENT_REQ_ITEM);
            }
        }

        if(memoItem == null){
            Log.e("MEMO ACTIVITY", "MemoItem object is null.");
            return;
        }

        //////////////////////////////////////////////////
        // 생성
        //////////////////////////////////////////////////
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        constLayout_main = findViewById(R.id.memo_constLayout_main);
        constLayout_back = findViewById(R.id.memo_constLayout_back);
        img_back = findViewById(R.id.memo_img_back);
        txt_back = findViewById(R.id.memo_txt_back);
        txt_date = findViewById(R.id.memo_txt_date);
        edit_title = findViewById(R.id.memo_edit_title);
        edit_content = findViewById(R.id.memo_edit_content);
        imgBnt_deleteMemo = findViewById(R.id.memo_imgBnt_deleteMemo);
        imgBtn_addMemo = findViewById(R.id.memo_imgBtn_addMemo);

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////

        if(!isNew){
            txt_date.setText(memoItem.getDate());
            edit_title.setText(memoItem.getTitle());
            edit_content.setText(memoItem.getContent());
        }
        else{
            imgBnt_deleteMemo.setVisibility(View.GONE);
            imgBtn_addMemo.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////
        // 바인딩
        //////////////////////////////////////////////////
        constLayout_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edit_content.getWindowToken(), 0);
            }
        });

        View.OnClickListener onClickBackBtn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : 데이터 변경이 있는 경우 저장으로 RESULT_OK, 아니면 취소로 판단하고 RESULT_CANCEL
                setResult(RESULT_OK, null);
                finish();
            }
        };
        //TODO: 완료 버튼 만들기, 완료 누르면 데이터 변경 있는 경우 저장으로 판단하고 저장(), 화면은 닫지 않고 키보드 포커스만 제거한다.
        //저장 성공 후 idx 가져와야한다. 그 상태에서 다시 수정을 위해 필요하다.
        //저장 후 또한 isNew상태를 변경한다.

        constLayout_back.setOnClickListener(onClickBackBtn);
        img_back.setOnClickListener(onClickBackBtn);
        txt_back.setOnClickListener(onClickBackBtn);
    }
}
