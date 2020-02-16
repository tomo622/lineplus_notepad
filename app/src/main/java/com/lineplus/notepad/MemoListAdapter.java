package com.lineplus.notepad;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.ViewHolder> {
    private ArrayList<MemoItem> items;
    private boolean showSelectButton;
    private boolean beforeGoneSelectButton; //이전엔 숨김상태였는지 여부 (선택 버튼 초기화를 위해)
    private int itemsCnt;

    public MemoListAdapter(){

    }

    public MemoListAdapter(ArrayList<MemoItem> items, boolean showSelectButton){
        this.items = items;
        this.showSelectButton = showSelectButton;
    }

    public boolean isShowSelectButton() {
        return showSelectButton;
    }

    public void setShowSelectButton(boolean showSelectButton) {
        if(showSelectButton && !this.showSelectButton){
            beforeGoneSelectButton = true;
            itemsCnt = 0;
        }
        this.showSelectButton = showSelectButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        return new MemoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MemoItem item = items.get(position);

        holder.tgl_select.setVisibility(showSelectButton ? View.VISIBLE : View.GONE);

        holder.txt_id.setText(Integer.toString(item.getIdx()));
        holder.txt_title.setText(item.getTitle());
        holder.txt_date.setText(item.getDate());
        holder.txt_content.setText(item.getContent());
        //holder.img_image.

        if(showSelectButton){
            //이전에 숨김상태였다면 모든 아이템들의 체크 상태를 해제한다.
            if(beforeGoneSelectButton){
                item.setSelected(false);
                if(itemsCnt++ == items.size())
                    beforeGoneSelectButton = false;
            }
            holder.tgl_select.setChecked(item.isSelected());

            final ToggleButton tempTglSelect = holder.tgl_select;
            tempTglSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setSelected(tempTglSelect.isChecked());
                }
            });
        }



// Sample...
//        int width = ((ViewGroup)parent.getParent()).getWidth(); //RecyclerView의 부모인 FrameLayout의 넓이
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        holder.textViewStoreIdx.setText(Integer.toString(item.getStoreIdx()));
//
//        holder.textViewKeyword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(menuTagButtonClickCallback != null){
//                    menuTagButtonClickCallback.onClick(item.getKeyword());
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txt_id;
        protected TextView txt_title;
        protected TextView txt_date;
        protected TextView txt_content;
        protected ToggleButton tgl_select;
        protected ImageView img_image;

        public ViewHolder(View itemView) {
            super(itemView);

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
