package com.lineplus.notepad;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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

        frameLayout_deleteMemo = findViewById(R.id.main_frameLayout_deleteMemo);
        txt_deleteMemo = findViewById(R.id.main_txt_deleteMemo);
        txt_memoCount = findViewById(R.id.main_txt_memoCount);
        tgl_selectMemo = findViewById(R.id.main_tgl_selectMemo);
        imgBtn_addMemo = findViewById(R.id.main_imgBtn_addMemo);
        recy_memoList = findViewById(R.id.main_recy_memoList);

        memoItems = new ArrayList<>();
        memoListAdapter = new MemoListAdapter(memoItems, false);

        recy_memoList.setAdapter(memoListAdapter);
        recy_memoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        final Bitmap imgDelete = ((BitmapDrawable)getDrawable(R.drawable.ic_btn_delete)).getBitmap();
        int deleteBtnWidth = imgDelete.getWidth() + (int)CommonFunc.dpToPx(this, 15 * 2); //삭제 이미지 너비 + 왼쪽, 오른쪽 margin 각각 15dp
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

        tgl_selectMemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                memoListAdapter.setShowSelectButton(b);
                memoListAdapter.notifyDataSetChanged();
            }
        });

        setDate();
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
