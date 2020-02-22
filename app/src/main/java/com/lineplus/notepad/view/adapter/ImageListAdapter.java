package com.lineplus.notepad.view.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lineplus.notepad.R;
import com.lineplus.notepad.event.OnSingleClickListener;
import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.util.GraphicFunc;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ViewGroup parent;
    private ArrayList<Image> items;
    private boolean isSelectionMode;

    public ImageListAdapter(){}

    public ImageListAdapter(ArrayList<Image> items) {
        this.items = items;
        isSelectionMode = false;
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if(!selectionMode){
            //선택 모드가 off가 되면 아이템 선택 상태 초기화
            for(Image img : items){
                img.setSelected(false);
            }
        }
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(this.parent.getContext()).inflate(R.layout.item_photo, this.parent, false);
        return new ImageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Image item = items.get(position);

        int width = ((ViewGroup)parent.getParent()).getWidth();
        CardView.LayoutParams cardViewLayoutParams = new CardView.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT);
        cardViewLayoutParams.leftMargin = 1;
        cardViewLayoutParams.rightMargin = 1;
        holder.cardView.setLayoutParams(cardViewLayoutParams);

        if(item.getType().equals("IMAGE")){
            Bitmap bmp = GraphicFunc.bytesToBitmap(item.getBitmapBytes());
        }
        else if(item.getType().equals("URL")){
            GraphicFunc.setImageByUrlToImageView(parent.getContext(), item.getUrl(), holder.img_image);
        }

        if(isSelectionMode){
            holder.tgl_select.setVisibility(View.VISIBLE);
            holder.tgl_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setSelected(b);
                    Drawable alpha = holder.img_image.getDrawable();
                    if(alpha == null){
                        alpha = holder.img_image.getBackground();
                    }
                    if(alpha != null){
                        if(b){
                            alpha.setAlpha(50);
                        }
                        else{
                            alpha.setAlpha(255);
                        }
                    }
                }
            });
            holder.tgl_select.setChecked(item.isSelected());
            holder.img_image.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onClickEx(View view) {
                    holder.tgl_select.setChecked(!holder.tgl_select.isChecked());
                }
            });
        }
        else{
            holder.tgl_select.setVisibility(View.INVISIBLE);
        }

//        //현재 아이템 선택 상태에 따라 토글 버튼을 변경
//        holder.tgl_select.setChecked(item.isSelected());
//
//        holder.tgl_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleSelectState(item, holder.tgl_select, holder.linLayout_item, false);
//            }
//        });
//
//        //현재 아이템 선택 상태에 따라 레이아웃을 변경
//        if(item.isSelected()){
//            holder.linLayout_item.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGreen)));
//        }
//        else{
//            holder.linLayout_item.setBackgroundColor(Color.parseColor(parent.getResources().getString(R.color.colorDarkGray)));
//        }
//
//        holder.linLayout_item.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onClickEx(View view) {
//                if(!showSelectButton){
//                    onClickMemoItem.onClick(item);
//                }
//                else{
//                    toggleSelectState(item, holder.tgl_select, holder.linLayout_item, true);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardView;
        protected ImageView img_image;
        protected ToggleButton tgl_select;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.iphoto_cardView);
            img_image = itemView.findViewById(R.id.iphoto_img_image);
            tgl_select = itemView.findViewById(R.id.iphoto_tgl_select);
        }
    }
}
