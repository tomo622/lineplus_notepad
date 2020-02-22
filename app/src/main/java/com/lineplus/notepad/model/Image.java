package com.lineplus.notepad.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idx;
    private long memoIdx;
    private String type;
    private byte[] bitmapBytes;
    private String url;

    private boolean selected;

    public Image(){
    }

    public Image(long idx, long memoIdx, String type, byte[] bitmapBytes, String url) {
        this.idx = idx;
        this.memoIdx = memoIdx;
        this.type = type;
        this.bitmapBytes = bitmapBytes;
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

    public byte[] getBitmapBytes() {
        return bitmapBytes;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSelected() {
        return selected;
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

    public void setBitmapBytes(byte[] bitmapBytes) {
        this.bitmapBytes = bitmapBytes;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
