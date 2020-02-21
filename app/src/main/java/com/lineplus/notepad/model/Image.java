package com.lineplus.notepad.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idx;
    private long memoIdx;
    private String type;
    private Bitmap bmp;
    private String url;

    public Image(){}

    public Image(long idx, long memoIdx, String type, Bitmap bmp, String url) {
        this.idx = idx;
        this.memoIdx = memoIdx;
        this.type = type;
        this.bmp = bmp;
        this.url = url;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getIdx() {
        return idx;
    }

    public long getMemoIdx() {
        return memoIdx;
    }

    public String getType() {
        return type;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public String getUrl() {
        return url;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public void setMemoIdx(long memoIdx) {
        this.memoIdx = memoIdx;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
