package com.dohyun.lineplus.notepad.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dohyun.lineplus.notepad.data.DataManager;
import com.dohyun.lineplus.notepad.data.DataObservable;
import com.dohyun.lineplus.notepad.data.DataObserver;
import com.dohyun.lineplus.notepad.data.DataObserverNotice;
import com.dohyun.lineplus.notepad.database.DatabaseManager;
import com.dohyun.lineplus.notepad.event.OnSingleClickListener;
import com.dohyun.lineplus.notepad.util.GraphicFunc;
import com.dohyun.lineplus.notepad.model.MemoItem;
import com.dohyun.lineplus.notepad.view.adapter.MemoListAdapter;
import com.dohyun.lineplus.notepad.event.OnCheckMemoItemSelect;
import com.dohyun.lineplus.notepad.event.OnClickMemoItem;
import com.dohyun.lineplus.notepad.R;
import com.dohyun.lineplus.notepad.view.helper.SwipeHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataObservable {
    private Button btn_deleteMemo;
    private TextView txt_memoCount;
    private TextView txt_deleteMemoCount;
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
        btn_deleteMemo = findViewById(R.id.main_btn_deleteMemo);
        txt_memoCount = findViewById(R.id.main_txt_memoCount);
        txt_deleteMemoCount = findViewById(R.id.main_txt_deleteMemoCount);
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
                Bundle bundle = new Bundle();
                bundle.putSerializable(MemoActivity.INTENT_REQ_ITEM, memoItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        /// 메모 아이템 선택 이벤트 (선택 모드인 경우에만)
        OnCheckMemoItemSelect onCheckMemoItemSelect = new OnCheckMemoItemSelect() {
            @Override
            public void checkSelectedCount(int cnt) {
                if(cnt > 0 && !btn_deleteMemo.getText().equals(R.string.delete_memo)){
                    btn_deleteMemo.setText(R.string.delete_memo);
                    txt_deleteMemoCount.setText(Integer.toString(cnt) + "개 선택됨");
                }
                else if (cnt == 0){
                    btn_deleteMemo.setText(R.string.delete_all_memo);
                    txt_deleteMemoCount.setText("");
                }
            }
        };
        memoListAdapter = new MemoListAdapter(memoItems, false, onClickMemoItem, onCheckMemoItemSelect);

        final Bitmap imgDelete = ((BitmapDrawable)getDrawable(R.drawable.ic_btn_delete)).getBitmap();
        final int deleteBtnWidth = imgDelete.getWidth() + (int) GraphicFunc.dpToPx(this, 15 * 2); //삭제 이미지 너비 + 왼쪽, 오른쪽 margin 각각 15dp
        memoListSwipeHelper = new SwipeHelper(this, recy_memoList, deleteBtnWidth) {
            @Override
            public void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {
                swipeButtons.add(new SwipeHelper.SwipeButton(
                        imgDelete,
                        ContextCompat.getColor(MainActivity.this, R.color.colorRed),
                        new SwipeHelper.OnSwipeButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                ArrayList<MemoItem> deleteMemo = new ArrayList<>();
                                deleteMemo.add(memoItems.get(pos));
                                deleteMemoEx(deleteMemo);
                            }
                        }
                ));
            }
        };

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////
        DataObserver.getInstance().register(this);

        DatabaseManager.getInstance(this); //데이터베이스 및 테이블 생성을 위해 호출

        toggleAddDeleteButton(tgl_selectMemo.isChecked());

        recy_memoList.setAdapter(memoListAdapter);
        recy_memoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recy_memoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        //////////////////////////////////////////////////
        // 바인딩
        //////////////////////////////////////////////////
        tgl_selectMemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleAddDeleteButton(b);
                memoListAdapter.setShowSelectButton(b);
                memoListAdapter.notifyDataSetChanged();
                if(b){
                    memoListSwipeHelper.useSwipe(false);
                    txt_memoCount.setVisibility(View.GONE);
                    txt_deleteMemoCount.setVisibility(View.VISIBLE);
                }
                else{
                    memoListSwipeHelper.useSwipe(true);
                    txt_memoCount.setVisibility(View.VISIBLE);
                    txt_deleteMemoCount.setVisibility(View.GONE);
                }
            }
        });

        imgBtn_addMemo.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.putExtra(MemoActivity.INTENT_REQ_NEW, true);
                startActivity(intent);
            }
        });

        btn_deleteMemo.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                //모두 삭제
                if(btn_deleteMemo.getText().toString().equals(getResources().getString(R.string.delete_all_memo))){
                    deleteMemoEx(memoItems);
                }
                //선택된 메모 목록 삭제
                else{
                    ArrayList<MemoItem> seletedMemoItems = memoListAdapter.getSelectedItems();
                    deleteMemoEx(seletedMemoItems);
                }
            }
        });

        DataManager.getInstance(this).requestMemos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataObserver.getInstance().unregister(this);
    }

    @Override
    public void notify(DataObserverNotice dataObserverNotice) {
        if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.SELECT)){
            if(dataObserverNotice.isResult()){
                setData();
            }
            else{

            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.DELETE)){
            if(dataObserverNotice.isResult()){
                Toast.makeText(MainActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                if(tgl_selectMemo.isChecked()) {
                    tgl_selectMemo.setChecked(false);
                }
                setData();
            }
            else{
                Toast.makeText(MainActivity.this, "삭제를 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.INSERT)){
            if(dataObserverNotice.isResult()){
                setData();
            }
            else{
                Toast.makeText(MainActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.UPDATE)){
            if(dataObserverNotice.isResult()){
                setData();
            }
            else{
                Toast.makeText(MainActivity.this, "수정을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.INSERT_IMAGE)){
            if(dataObserverNotice.isResult()){
                setData();
            }
            else{
                Toast.makeText(MainActivity.this, "이미지 저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(dataObserverNotice.getType().equals(DataObserverNotice.TYPE.DELETE_IMAGE)){
            if(dataObserverNotice.isResult()){
                setData();
            }
            else{
                Toast.makeText(MainActivity.this, "이미지 삭제를 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleAddDeleteButton(boolean isSelectMode){
        imgBtn_addMemo.setVisibility(isSelectMode?View.GONE:View.VISIBLE);
        btn_deleteMemo.setVisibility(isSelectMode?View.VISIBLE:View.GONE);
    }

    private void deleteMemoEx(final ArrayList<MemoItem> memoItems){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("정말 삭제하시겠습니까?");
        adb.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                long idxs[] = new long[memoItems.size()];
                for(int i = 0; i < memoItems.size(); i++){
                    idxs[i] = memoItems.get(i).getIdx();
                }

                DataManager.getInstance(MainActivity.this).requestMemosDelete(idxs);
                dialog.dismiss();
            }

        });
        adb.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    private void setData(){
        memoItems.clear();
        memoItems.addAll(DataManager.getInstance(this).getMemos());
        memoListAdapter.notifyDataSetChanged();

        if(memoItems.size() > 0){
            tgl_selectMemo.setVisibility(View.VISIBLE);
            txt_memoCount.setText(Integer.toString(memoItems.size()) + "개의 메모");
        }
        else{
            tgl_selectMemo.setVisibility(View.GONE);
            txt_memoCount.setText("");
        }
    }
}
