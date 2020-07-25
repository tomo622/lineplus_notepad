package com.dohyun.lineplus.notepad.data;

public class DataObserverNotice {
    public enum TYPE{
        SELECT, DELETE, INSERT, UPDATE, INSERT_IMAGE, DELETE_IMAGE;
    }

    private TYPE type;
    private boolean result;
    private long lparam1;

    public DataObserverNotice(){}

    public DataObserverNotice(TYPE type, boolean result, long lparam1){
        this.type = type;
        this.result = result;
        this.lparam1 = lparam1;
    }

    public TYPE getType() {
        return type;
    }

    public boolean isResult() {
        return result;
    }

    public long getLparam1() {
        return lparam1;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setLparam1(long lparam1) {
        this.lparam1 = lparam1;
    }
}
