package com.lineplus.notepad.data;

import java.util.ArrayList;

public class DataObserver {
    private static DataObserver instance = null;
    private ArrayList<DataObservable> dataObservables = new ArrayList<>();

    public static DataObserver getInstance(){
        if(instance == null){
            instance = new DataObserver();
        }
        return instance;
    }

    public void register(DataObservable dataObservable){
        dataObservables.remove(dataObservable);
        dataObservables.add(dataObservable);
    }

    public void unregister(DataObservable dataObservable){
        dataObservables.remove(dataObservable);
    }
    public void notifyObservers(DataObserverNotice dataObserverNotice){
        for(DataObservable dataObservable : dataObservables){
            dataObservable.notify(dataObserverNotice);
        }
    }
}
