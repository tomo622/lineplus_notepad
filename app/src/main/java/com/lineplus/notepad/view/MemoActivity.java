package com.lineplus.notepad.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private boolean changedFlag;
    private boolean doUpdateMemoList; //한번이라도 데이터가 변경 또는 추가, 삭제 된다면 true로 변경 (메인 화면으로 돌아가서 목록을 갱신할지 여부를 결정)

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

            TextWatcher tw = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(changedFlag == false){
                        toggleChangedFlag(true);
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };
            edit_title.addTextChangedListener(tw);
            edit_content.addTextChangedListener(tw);

            imgBnt_deleteMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteMemo();
                }
            });

            imgBtn_addMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    init(false);
                }
            });



            init(true);

        }catch (Exception e) {
            Log.e("MEMO ACTIVITY", e.toString());
        }
    }

    private void init(boolean isCreate){
        //최초 생성되어 초기화되는 경우 intent로부터 초기 데이터와 생성/수정 여부를 결정한다.
        if(isCreate){
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
        }
        //추가버튼에 의해 초기화되는 경우
        else{
            isNew = true;
            memoItem = new MemoItem();
        }

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////

        txt_date.setText(memoItem.getDate());
        edit_title.setText(memoItem.getTitle());
        edit_content.setText(memoItem.getContent());

        frameLayout_save.setVisibility(View.GONE);
        toggleChangedFlag(false);
        doUpdateMemoList = false;

        if(isNew){
            txt_date.setVisibility(View.GONE);
            imgBnt_deleteMemo.setVisibility(View.GONE);
            imgBtn_addMemo.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(){
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(edit_content.getWindowToken(), 0);
        }
    }

    private void toggleChangedFlag(boolean changed){
        changedFlag = changed;
        if(changedFlag){
            frameLayout_save.setVisibility(View.VISIBLE);
        }
        else{
            frameLayout_save.setVisibility(View.GONE);
        }
    }

    private void deleteMemo(){
        //TODO: 정말 삭제하시겠습니까?

        long idxs[] = new long[1];
        idxs[0] = memoItem.getIdx();

        int deletedCnt = DatabaseManager.getInstance(this).deleteMemoByIdx(idxs);
        if(deletedCnt > 0){
            Toast.makeText(MemoActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
            doUpdateMemoList = true;
            complete(true);
        }
        else{
            Toast.makeText(MemoActivity.this, "삭제를 실패했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("MEMO ACTIVITY", "Delete memo fail.");
        }
    }

    private void saveMemo(boolean isFinish){
        if(changedFlag == false){
            complete(isFinish);
            return;
        }

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
                complete(isFinish);
                return;
            }

            //데이터 삽입
            memoItem.setTitle(title);
            memoItem.setContent(content);
            long idx = DatabaseManager.getInstance(this).insertMemo(memoItem);

            //데이터 삽입 실패한 경우
            if(idx < 0){
                Toast.makeText(MemoActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("MEMO ACTIVITY", "Insert memo fail.");

                complete(isFinish);
            }
            //데이터 삽입 성공한 경우
            else{
                saveSuccessProc(isFinish, idx);
            }
        }
        //업데이트
        else{
            if(memoItem.getIdx() == 0){
                Log.e("MEMO ACTIVITY", "Is not modify memo.");
                return;
            }

            String title = edit_title.getText().toString();
            String content = edit_content.getText().toString();

            //제목, 내용 둘 다 입력이 없는 경우
            if(title.isEmpty() && content.isEmpty()){
                complete(isFinish);
                return;
            }

            //데이터 수정
            memoItem.setTitle(title);
            memoItem.setContent(content);
            int updateCnt = DatabaseManager.getInstance(this).updateMemoById(memoItem);

            //데이터 수정이 실패한 경우
            if(updateCnt <= 0){
                Toast.makeText(MemoActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("MEMO ACTIVITY", "Upate memo fail.");

                complete(isFinish);
            }
            //데이터 수정 성공한 경우
            else{
                saveSuccessProc(isFinish, memoItem.getIdx());
            }
        }
    }

    private void saveSuccessProc(boolean isFinish, long idx){
        if(!isFinish){
            //현재 데이터 갱신
            MemoItem insertedMemoItem = DatabaseManager.getInstance(this).selectMemoByIdx(idx);
            memoItem.setDate(insertedMemoItem.getDate());
            memoItem.setTitle(insertedMemoItem.getTitle());
            memoItem.setContent(insertedMemoItem.getContent());

            txt_date.setText(memoItem.getDate());
            edit_title.setText(memoItem.getTitle());
            edit_content.setText(memoItem.getContent());

            toggleChangedFlag(false);

            if(isNew){
                memoItem.setIdx(insertedMemoItem.getIdx());

                //수정모드로 변경
                isNew = false;
                txt_date.setVisibility(View.VISIBLE);
                imgBnt_deleteMemo.setVisibility(View.VISIBLE);
                imgBtn_addMemo.setVisibility(View.VISIBLE);
            }
        }

        doUpdateMemoList = true;
        //Toast.makeText(MemoActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        complete(isFinish);
    }

    private void complete(boolean isFinish){
        /// 뒤로
        if(isFinish){
            if(doUpdateMemoList){
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
