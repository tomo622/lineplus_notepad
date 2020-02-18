package com.lineplus.notepad.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lineplus.notepad.database.DatabaseManager;
import com.lineplus.notepad.util.GraphicFunc;
import com.lineplus.notepad.model.MemoItem;
import com.lineplus.notepad.view.adapter.MemoListAdapter;
import com.lineplus.notepad.event.OnCheckMemoItemSelect;
import com.lineplus.notepad.event.OnClickMemoItem;
import com.lineplus.notepad.R;
import com.lineplus.notepad.view.helper.SwipeHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int INTENT_MEMO_ACTIVITY = 0;

    private FrameLayout frameLayout_deleteMemo;
    private TextView txt_deleteMemo;
    private TextView txt_memoCount;
    private ToggleButton tgl_selectMemo;
    private ImageButton imgBtn_addMemo;
    private RecyclerView recy_memoList;

    private ArrayList<MemoItem> memoItems;
    private MemoListAdapter memoListAdapter;
    private SwipeHelper memoListSwipeHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////////////////////////////////
        // 생성
        //////////////////////////////////////////////////
        frameLayout_deleteMemo = findViewById(R.id.main_frameLayout_deleteMemo);
        txt_deleteMemo = findViewById(R.id.main_txt_deleteMemo);
        txt_memoCount = findViewById(R.id.main_txt_memoCount);
        tgl_selectMemo = findViewById(R.id.main_tgl_selectMemo);
        imgBtn_addMemo = findViewById(R.id.main_imgBtn_addMemo);
        recy_memoList = findViewById(R.id.main_recy_memoList);

        memoItems = new ArrayList<>();
        /// 메모 아이템 클릭 이벤트 (선택 모드가 아닌 경우에만)
        OnClickMemoItem onClickMemoItem = new OnClickMemoItem() {
            @Override
            public void onClick(MemoItem memoItem) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.putExtra(MemoActivity.INTENT_REQ_NEW, false);
                intent.putExtra(MemoActivity.INTENT_REQ_ITEM, memoItem);
                startActivityForResult(intent, INTENT_MEMO_ACTIVITY);
            }
        };
        /// 메모 아이템 선택 이벤트 (선택 모드인 경우에만)
        OnCheckMemoItemSelect onCheckMemoItemSelect = new OnCheckMemoItemSelect() {
            @Override
            public void checkSelectedCount(int cnt) {
                if(cnt > 0 && !txt_deleteMemo.getText().equals(R.string.delete_memo)){
                    txt_deleteMemo.setText(R.string.delete_memo);
                }
                else if (cnt == 0){
                    txt_deleteMemo.setText(R.string.delete_all_memo);
                }
            }
        };
        memoListAdapter = new MemoListAdapter(memoItems, false, onClickMemoItem, onCheckMemoItemSelect);

        final Bitmap imgDelete = ((BitmapDrawable)getDrawable(R.drawable.ic_btn_delete)).getBitmap();
        int deleteBtnWidth = imgDelete.getWidth() + (int) GraphicFunc.dpToPx(this, 15 * 2); //삭제 이미지 너비 + 왼쪽, 오른쪽 margin 각각 15dp
        memoListSwipeHelper = new SwipeHelper(this, recy_memoList, deleteBtnWidth) {
            @Override
            public void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {
                swipeButtons.add(new SwipeHelper.SwipeButton(
                        imgDelete,
                        ContextCompat.getColor(MainActivity.this, R.color.colorRed),
                        new SwipeHelper.OnSwipeButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                            }
                        }
                ));
            }
        };


        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////
        DatabaseManager.getInstance(this); //데이터베이스 및 테이블 생성을 위해 호출

        toggleAddDeleteButton(tgl_selectMemo.isChecked());

        recy_memoList.setAdapter(memoListAdapter);
        recy_memoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        //////////////////////////////////////////////////
        // 바인딩
        //////////////////////////////////////////////////
        tgl_selectMemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleAddDeleteButton(b);
                memoListAdapter.setShowSelectButton(b);
                memoListAdapter.notifyDataSetChanged();
            }
        });

        imgBtn_addMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.putExtra(MemoActivity.INTENT_REQ_NEW, true);
                startActivityForResult(intent, INTENT_MEMO_ACTIVITY);
            }
        });

        txt_deleteMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //모두 삭제
                if(txt_deleteMemo.getText().equals(R.string.delete_all_memo)){

                }
                //선택된 메모 목록 삭제
                else{
                    //TODO: 선택된 아이템의 idx로 DB에서 삭제 후 리스트 갱신
                    ArrayList<MemoItem> seletedMemoItems = memoListAdapter.getSelectedItems();
                    String temp = "";
                    for(MemoItem item : seletedMemoItems){
                        temp += Integer.toString(item.getIdx());
                        temp +=", ";
                    }
                    Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setDate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INTENT_MEMO_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                //TODO: 리스트 갱신
            }
        }
    }

    private void toggleAddDeleteButton(boolean isSelectMode){
        imgBtn_addMemo.setVisibility(isSelectMode?View.GONE:View.VISIBLE);
        frameLayout_deleteMemo.setVisibility(isSelectMode?View.VISIBLE:View.GONE);
    }

    private void setDate(){
        int memoItemCnt = setMemoListData();
        txt_memoCount.setText(Integer.toString(memoItemCnt));
    }

    private int setMemoListData(){
        memoItems.clear();

        //TODO: TESTCODE
        for(int i = 0; i < 40; i++) {
            String cnt = Integer.toString(i + 1);
            String title = "title " + cnt;
            String date = "date " + cnt;
            String content = "content " + cnt;
            MemoItem memoItem = new MemoItem(i, title, date, content, null);

            memoItems.add(memoItem);
        }

        memoListAdapter.notifyDataSetChanged();

        return memoItems.size();
    }
}
