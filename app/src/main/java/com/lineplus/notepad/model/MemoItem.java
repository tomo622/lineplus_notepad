package com.lineplus.notepad.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MemoItem implements Serializable{
    private static final long serialVersionUID = 1L;

    private long idx;
    private String title;
    private String date;
    private String content;
    private ArrayList<Image> images;

    private boolean selected;

    public MemoItem(){
        images = new ArrayList<>();
    }

    public MemoItem(long idx, String title, String date, String content, ArrayList<Image> images) {
        this.idx = idx;
        this.title = title;
        this.date = date;
        this.content = content;
        this.images = images;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getIdx() {
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

    public ArrayList<Image> getImages() {
        return images;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setIdx(long idx) {
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

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
