package com.lineplus.notepad;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MemoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idx;
    private String title;
    private String date;
    private String content;
    private Bitmap image;
    private boolean selected;

    public MemoItem(){

    }

    public MemoItem(int idx, String title, String date, String content, Bitmap image) {
        this.idx = idx;
        this.title = title;
        this.date = date;
        this.content = content;
        this.image = image;
    }

    public int getIdx() {
        return idx;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
