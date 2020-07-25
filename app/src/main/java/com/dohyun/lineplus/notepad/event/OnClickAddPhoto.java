package com.dohyun.lineplus.notepad.event;

public abstract class OnClickAddPhoto {
    public enum TYPE{TAKE_PICTURE, FROM_ALBUM, INPUT_URL};

    public abstract void onClick(TYPE type, String strParam1);
}
