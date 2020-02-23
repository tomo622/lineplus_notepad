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
import com.lineplus.notepad.event.OnCheckImageSelect;
import com.lineplus.notepad.event.OnSingleClickListener;
import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.util.GraphicFunc;
import com.lineplus.notepad.view.ShowPhotoDialog;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ViewGroup parent;
    private ArrayList<Image> items;
    private OnCheckImageSelect onCheckImageSelect;
    private boolean isSelectionMode;
    private int selectedCnt;

    public ImageListAdapter(){}

    public ImageListAdapter(ArrayList<Image> items, OnCheckImageSelect onCheckImageSelect) {
        this.items = items;
        isSelectionMode = false;
        this.onCheckImageSelect = onCheckImageSelect;
        selectedCnt = 0;
    }

    public ArrayList<Image> getItems() {
        return items;
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if(!selectionMode){
            //선택 모드가 off가 되면 아이템 선택 상태 초기화
            for(Image img : items){
                img.setSelected(false);
            }
            selectedCnt = 0;
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

        if(item.getType().equals("DIR")){
            GraphicFunc.setImageByDirToImageView(parent.getContext(), item.getDir(), holder.img_image);
        }
        else if(item.getType().equals("URL")){
            GraphicFunc.setImageByUrlToImageView(parent.getContext(), item.getUrl(), holder.img_image);
        }

        if(isSelectionMode){
            holder.tgl_select.setVisibility(View.VISIBLE);
            holder.tgl_select.setChecked(item.isSelected());
        }
        else{
            holder.tgl_select.setVisibility(View.INVISIBLE);
        }

        holder.tgl_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isSelectionMode){
                    item.setSelected(b);
                    Drawable alpha = holder.img_image.getDrawable();
                    if(alpha == null){
                        alpha = holder.img_image.getBackground();
                    }
                    if(alpha != null){
                        if(b){
                            alpha.setAlpha(50);
                            selectedCnt++;
                            if(selectedCnt > items.size()){
                                selectedCnt = items.size();
                            }
                            onCheckImageSelect.checkSelectedCount(selectedCnt);
                        }
                        else{
                            alpha.setAlpha(255);
                            selectedCnt--;
                            if(selectedCnt < 0){
                                selectedCnt = 0;
                            }
                            onCheckImageSelect.checkSelectedCount(selectedCnt);
                        }
                    }
                }
            }
        });

        holder.img_image.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                if(isSelectionMode) {
                    holder.tgl_select.setChecked(!holder.tgl_select.isChecked());
                }
                else{
                    ShowPhotoDialog showPhotoDialog = new ShowPhotoDialog(parent.getContext(), items, items.indexOf(item));
                    showPhotoDialog .show();
                }
            }
        });
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
