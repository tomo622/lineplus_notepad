package com.dohyun.lineplus.notepad.view.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dohyun.lineplus.notepad.R;
import com.dohyun.lineplus.notepad.event.OnCheckMemoItemSelect;
import com.dohyun.lineplus.notepad.event.OnClickMemoItem;
import com.dohyun.lineplus.notepad.event.OnSingleClickListener;
import com.dohyun.lineplus.notepad.model.Image;
import com.dohyun.lineplus.notepad.model.MemoItem;
import com.dohyun.lineplus.notepad.util.GraphicFunc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.ViewHolder> {
    private ViewGroup parent;
    private ArrayList<MemoItem> items;
    private boolean showSelectButton;
    private OnClickMemoItem onClickMemoItem;
    private OnCheckMemoItemSelect onCheckMemoItemSelect;
    private ArrayList<MemoItem> selectedItems;

    public MemoListAdapter(){

    }

    public MemoListAdapter(ArrayList<MemoItem> items, boolean showSelectButton, OnClickMemoItem onClickMemoItem, OnCheckMemoItemSelect onCheckMemoItemSelect){
        this.items = items;
        this.showSelectButton = showSelectButton;
        this.onClickMemoItem = onClickMemoItem;
        this.onCheckMemoItemSelect = onCheckMemoItemSelect;
        selectedItems = new ArrayList<>();
    }

    public boolean isShowSelectButton() {
        return showSelectButton;
    }

    public ArrayList<MemoItem> getSelectedItems() {
        return selectedItems;
    }

    public void setShowSelectButton(boolean showSelectButton) {
        if(!showSelectButton && this.showSelectButton){
            for(MemoItem selectedItem : selectedItems) {
                selectedItem.setSelected(false);
            }
            selectedItems.clear();
            onCheckMemoItemSelect.checkSelectedCount(selectedItems.size());
        }
        this.showSelectButton = showSelectButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;

        View view = LayoutInflater.from(this.parent.getContext()).inflate(R.layout.item_memo, this.parent, false);
        return new MemoListAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MemoItem item = items.get(position);

        holder.tgl_select.setVisibility(showSelectButton ? View.VISIBLE : View.GONE);

        holder.txt_id.setText(Long.toString(item.getIdx()));
        String title = item.getTitle();
        if(title == null || title.equals("")){
            holder.txt_title.setText("제목없음");
        }
        else{
            holder.txt_title.setText(item.getTitle());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String todayTokens[] = sdf.format(date).split(" ");
        String dateTokens[] = item.getDate().split(" ");
        if(todayTokens.length == 2 && dateTokens.length == 2){
            if(todayTokens[0].equals(dateTokens[0])){
                holder.txt_date.setText(dateTokens[1]); //오늘 날짜는 시간만 표시
            }
            else{
                holder.txt_date.setText(dateTokens[0]); //이전 날짜는 날짜만 표시
            }
        }
        else{
            holder.txt_date.setText(item.getDate());
        }
        holder.txt_content.setText(item.getContent());

        if(item.getImages().size() > 0){
            Image thumbnail = item.getImages().get(0);

            if(thumbnail.getType().equals("URL")){
                GraphicFunc.setImageByUrlToImageView(parent.getContext(), thumbnail.getUrl(), holder.img_image);
            }
            else if(thumbnail.getType().equals("DIR")){
                GraphicFunc.setImageByDirToImageView(parent.getContext(), thumbnail.getDir(), holder.img_image);
            }
            holder.img_image.setVisibility(View.VISIBLE);
        }
        else{
            holder.img_image.setVisibility(View.GONE);
        }



        //현재 아이템 선택 상태에 따라 토글 버튼을 변경
        holder.tgl_select.setChecked(item.isSelected());

        holder.tgl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelectState(item, holder.tgl_select, holder.linLayout_item, false);
            }
        });

        //현재 아이템 선택 상태에 따라 레이아웃을 변경
        if(item.isSelected()){
            holder.linLayout_item.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGreen)));
        }
        else{
            holder.linLayout_item.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGray)));
        }

        holder.linLayout_item.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                if(!showSelectButton){
                    onClickMemoItem.onClick(item);
                }
                else{
                    toggleSelectState(item, holder.tgl_select, holder.linLayout_item, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("ResourceType")
    private void toggleSelectState(MemoItem item, ToggleButton toggleButton, LinearLayout linearLayout, boolean handleTglBtn){
        item.setSelected(!item.isSelected()); //상태 변경

        //선택 토글버튼의 상태를 변경해줘야하는 경우
        if(handleTglBtn){
            toggleButton.setChecked(item.isSelected());
        }

        if(item.isSelected()){
            linearLayout.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGreen)));
        }
        else{
            linearLayout.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGray)));
        }

        //변경된 상태에 따라 적용
        if(item.isSelected()){
            selectedItems.add(item);
        }
        else{
            selectedItems.remove(item);
        }
        onCheckMemoItemSelect.checkSelectedCount(selectedItems.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout linLayout_item;
        protected TextView txt_id;
        protected TextView txt_title;
        protected TextView txt_date;
        protected TextView txt_content;
        protected ToggleButton tgl_select;
        protected ImageView img_image;

        public ViewHolder(View itemView) {
            super(itemView);

            linLayout_item = itemView.findViewById(R.id.imemo_linLayout_item);
            txt_id = itemView.findViewById(R.id.imemo_txt_id);
            txt_title = itemView.findViewById(R.id.imemo_txt_title);
            txt_date = itemView.findViewById(R.id.imemo_txt_date);
            txt_content = itemView.findViewById(R.id.imemo_txt_content);
            tgl_select = itemView.findViewById(R.id.imemo_tgl_select);
            img_image = itemView.findViewById(R.id.imemo_img_image);

            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(1000);
            tgl_select.setAnimation(animation);
        }
    }
}
