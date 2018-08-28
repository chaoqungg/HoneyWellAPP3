package com.honeywell.honeywellproject.BleTaskModule.SonTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class SonTaskSaveEvent {


    public int           position;
    public String        sonSeries;
    public String        sonAddr;
    public String        sonDescription;
    public SonTaskSaveEvent(int position, String sonSeries, String sonAddr,String sonDescription) {
        this.position=position;
        this.sonSeries=sonSeries;
        this.sonAddr=sonAddr;
        this.sonDescription=sonDescription;
    }
}
