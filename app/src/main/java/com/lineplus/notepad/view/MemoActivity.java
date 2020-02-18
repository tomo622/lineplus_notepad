package com.lineplus.notepad.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lineplus.notepad.database.DatabaseManager;
import com.lineplus.notepad.model.MemoItem;
import com.lineplus.notepad.R;

public class MemoActivity extends AppCompatActivity {
    public static final String INTENT_REQ_NEW = "INTENT_REQ_NEW";
    public static final String INTENT_REQ_ITEM = "INTENT_REQ_ITEM";
    private boolean isNew;
    private MemoItem memoItem = null;

    private InputMethodManager imm;

    private ConstraintLayout constLayout_main;
    private ConstraintLayout constLayout_back;
    private FrameLayout frameLayout_save;
    private ImageView img_back;
    private TextView txt_back;
    private TextView txt_save;
    private TextView txt_date;
    private EditText edit_title;
    private EditText edit_content;
    private ImageButton imgBnt_deleteMemo;
    private ImageButton imgBtn_addMemo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        try{

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
            frameLayout_save = findViewById(R.id.memo_frameLayout_save);
            img_back = findViewById(R.id.memo_img_back);
            txt_back = findViewById(R.id.memo_txt_back);
            txt_save = findViewById(R.id.memo_txt_save);
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
                txt_date.setVisibility(View.GONE);
                imgBnt_deleteMemo.setVisibility(View.GONE);
                imgBtn_addMemo.setVisibility(View.GONE);
            }

            //////////////////////////////////////////////////
            // 바인딩
            //////////////////////////////////////////////////
            constLayout_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard();
                }
            });

            View.OnClickListener onClickBackBtn = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMemo(true);
                }
            };
            constLayout_back.setOnClickListener(onClickBackBtn);
            img_back.setOnClickListener(onClickBackBtn);
            txt_back.setOnClickListener(onClickBackBtn);

            View.OnClickListener onClickSaveBtn = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMemo(false);
                }
            };
            frameLayout_save.setOnClickListener(onClickSaveBtn);
            txt_save.setOnClickListener(onClickSaveBtn);

        }catch (Exception e) {
            Log.e("MEMO ACTIVITY", e.toString());
        }
    }

    private void hideKeyboard(){
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(edit_content.getWindowToken(), 0);
        }
    }

    private void saveMemo(boolean isFinish){
        long idx = 0; //삽입이나 업데이트 이후에 해당 idx를 반환 받는다.

        //삽입
        if(isNew){
            if(memoItem.getIdx() != 0){
                Log.e("MEMO ACTIVITY", "Is not new memo.");
                return;
            }

            String title = edit_title.getText().toString();
            String content = edit_content.getText().toString();

            //제목, 내용 둘 다 입력이 없는 경우
            if(title.isEmpty() && content.isEmpty()){
                completeSave(isFinish, false);
                return;
            }

            //데이터 삽입
            memoItem.setTitle(title);
            memoItem.setContent(content);
            idx = DatabaseManager.getInstance(this).insertMemo(memoItem);

            //데이터 삽입 실패한 경우
            if(idx <= 0){
                Toast.makeText(MemoActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("MEMO ACTIVITY", "Insert memo fail.");

                completeSave(isFinish, false);
            }
            //데이터 삽입 성공한 경우
            else{
                //현재 데이터 갱신
                MemoItem insertedMemoItem = DatabaseManager.getInstance(this).selectMemoByIdx(idx);
                memoItem.setIdx(insertedMemoItem.getIdx());
                memoItem.setDate(insertedMemoItem.getDate());
                memoItem.setTitle(insertedMemoItem.getTitle());
                memoItem.setContent(insertedMemoItem.getContent());

                txt_date.setText(memoItem.getDate());
                edit_title.setText(memoItem.getTitle());
                edit_content.setText(memoItem.getContent());

                //수정모드로 변경
                isNew = false;
                txt_date.setVisibility(View.VISIBLE);
                imgBnt_deleteMemo.setVisibility(View.VISIBLE);
                imgBtn_addMemo.setVisibility(View.VISIBLE);

                Toast.makeText(MemoActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

                completeSave(isFinish, true);
            }
        }
        //업데이트
        else{
            //idx 있음
            //alter
        }
    }

    private void completeSave(boolean isFinish, boolean isSuccess){
        /// 뒤로
        if(isFinish){
            if(isSuccess){
                setResult(RESULT_OK, null);
            }
            else{
                setResult(RESULT_CANCELED, null);
            }
            finish();
        }
        /// 완료
        else{
            hideKeyboard();
        }
    }
}
