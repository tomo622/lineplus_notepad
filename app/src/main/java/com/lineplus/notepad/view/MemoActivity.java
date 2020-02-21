package com.lineplus.notepad.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lineplus.notepad.data.DataManager;
import com.lineplus.notepad.data.DataObservable;
import com.lineplus.notepad.data.DataObserver;
import com.lineplus.notepad.data.DataObserverNotice;
import com.lineplus.notepad.model.MemoItem;
import com.lineplus.notepad.R;

public class MemoActivity extends AppCompatActivity implements DataObservable {
    public static final String INTENT_REQ_NEW = "INTENT_REQ_NEW";
    public static final String INTENT_REQ_ITEM = "INTENT_REQ_ITEM";
    private boolean isNew;
    private MemoItem memoItem = null;
    private boolean changedFlag;

    private InputMethodManager imm;

    private ConstraintLayout constLayout_main;
    private ConstraintLayout constLayout_back;
    private View include_slide;
    private FrameLayout frameLayout_save;
    private ImageView img_back;
    private TextView txt_back;
    private TextView txt_save;
    private TextView txt_date;
    private EditText edit_title;
    private EditText edit_content;
    private ImageButton imgBnt_deleteMemo;
    private ImageButton imgBtn_addMemo;
    private ToggleButton tgl_photo;

    private ViewPhoto viewPhoto;

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
            include_slide = findViewById(R.id.memo_include_slide);
            frameLayout_save = findViewById(R.id.memo_frameLayout_save);
            img_back = findViewById(R.id.memo_img_back);
            txt_back = findViewById(R.id.memo_txt_back);
            txt_save = findViewById(R.id.memo_txt_save);
            txt_date = findViewById(R.id.memo_txt_date);
            edit_title = findViewById(R.id.memo_edit_title);
            edit_content = findViewById(R.id.memo_edit_content);
            imgBnt_deleteMemo = findViewById(R.id.memo_imgBnt_deleteMemo);
            imgBtn_addMemo = findViewById(R.id.memo_imgBtn_addMemo);
            tgl_photo = findViewById(R.id.memo_tgl_photo);
            viewPhoto = new ViewPhoto(this);

            //////////////////////////////////////////////////
            // 설정(변경 필요 없는 설정)
            //////////////////////////////////////////////////
            DataObserver.getInstance().register(this);

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
                    saveMemoEx(true);
                }
            };
            constLayout_back.setOnClickListener(onClickBackBtn);
            img_back.setOnClickListener(onClickBackBtn);
            txt_back.setOnClickListener(onClickBackBtn);

            View.OnClickListener onClickSaveBtn = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMemoEx(false);
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

            tgl_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        slideUp(include_slide);
                    }
                    else{
                        slideDown(include_slide);
                    }
                }
            });


            init(true);

        }catch (Exception e) {
            Log.e("MEMO ACTIVITY", e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataObserver.getInstance().unregister(this);
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
        include_slide.setVisibility(View.GONE);
        tgl_photo.setChecked(false);

        txt_date.setText(memoItem.getDate());
        edit_title.setText(memoItem.getTitle());
        edit_content.setText(memoItem.getContent());

        frameLayout_save.setVisibility(View.GONE);
        toggleChangedFlag(false);

        if(isNew){
            txt_date.setVisibility(View.GONE);
            imgBnt_deleteMemo.setVisibility(View.GONE);
            imgBtn_addMemo.setVisibility(View.GONE);
        }
    }


    public MemoItem getMemoItem() {
        return memoItem;
    }

    public ToggleButton getTgl_photo() {
        return tgl_photo;
    }

    @Override
    public void notify(DataObserverNotice dataObserverNotice) {
        //완료 버튼에 의한 추가, 수정만 들어온다.
        if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.INSERT)){
            if(dataObserverNotice.isResult()){
                long insertedIdx = dataObserverNotice.getLparam1();
                refreshData(insertedIdx, false);
            }
            else{

            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.UPDATE)){
            if(dataObserverNotice.isResult()){
                refreshData(memoItem.getIdx(), false);
            }
            else{
            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.INSERT_IMAGE)){
            if(dataObserverNotice.isResult()){
                refreshData(memoItem.getIdx(), true);
            }
            else{
            }
        }
    }

    private void hideKeyboard(){
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(edit_content.getWindowToken(), 0);
        }
    }

    private void finishMemo(boolean isBack){
        //뒤로
        if(isBack){
            finish();
        }
        //완료
        else{
            hideKeyboard();
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

    public void slideUp(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    public void slideDown(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    private void saveMemoEx(boolean isBack){
        if(changedFlag == false){
            finishMemo(isBack);
            return;
        }
        else{
            //데이터 확인
            if(isNew && memoItem.getIdx() != 0){
                Log.e("MEMO ACTIVITY", "Is not new memo.");
                return;
            }
            if(!isNew && memoItem.getIdx() == 0){
                Log.e("MEMO ACTIVITY", "Is not modify memo.");
                return;
            }
            String title = edit_title.getText().toString();
            String content = edit_content.getText().toString();
            int imageCnt = memoItem.getImages().size();

            //제목, 내용, 이미지 다 입력이 없는 경우
            if(title.isEmpty() && content.isEmpty() && imageCnt == 0){
                if(!isNew){
                    //원래있던 데이터라면 삭제
                    deleteMemoEx();
                    finishMemo(true);
                }
                else{
                    //새로만든 경우라면 그냥 끝낸다.
                    finishMemo(isBack);
                }
                return;
            }

            //데이서 삽입/수정
            memoItem.setTitle(title);
            memoItem.setContent(content);

            if(isNew){
                DataManager.getInstance(this).requestMemoInsert(memoItem);
            }
            else{
                DataManager.getInstance(this).requestMemoUpdate(memoItem);
            }

            finishMemo(isBack);
        }
    }

    private void deleteMemoEx(){
        long idxs[] = new long[1];
        idxs[0] = memoItem.getIdx();

        DataManager.getInstance(MemoActivity.this).requestMemosDelete(idxs);
    }

    private void deleteMemo(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("정말 삭제하시겠습니까?");
        adb.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                deleteMemoEx();
                dialog.dismiss();
                finishMemo(true);
            }

        });
        adb.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    private void refreshData(long idx, boolean changedImage){
        for(MemoItem memo : DataManager.getInstance(this).getMemos()){
            if(memo.getIdx() == idx){
                memoItem.setIdx(memo.getIdx());
                memoItem.setDate(memo.getDate());
                memoItem.setTitle(memo.getTitle());
                memoItem.setContent(memo.getContent());
                memoItem.getImages().clear();
                memoItem.getImages().addAll(memo.getImages());

                break;
            }
        }

        //현재 데이터 갱신
        txt_date.setText(memoItem.getDate());
        if(changedImage) return;
        edit_title.setText(memoItem.getTitle());
        edit_content.setText(memoItem.getContent());

        //모드 변경
        if(isNew){
            isNew = false;
            txt_date.setVisibility(View.VISIBLE);
            imgBnt_deleteMemo.setVisibility(View.VISIBLE);
            imgBtn_addMemo.setVisibility(View.VISIBLE);
        }

        if(changedFlag == true){
            toggleChangedFlag(false);
        }
    }
}
