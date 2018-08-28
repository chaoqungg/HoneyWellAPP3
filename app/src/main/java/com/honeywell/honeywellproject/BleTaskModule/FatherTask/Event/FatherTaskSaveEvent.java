package com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class FatherTaskSaveEvent {


    public int           position;
    public String        fatherName;
    public String        fatherDescription;
    public FatherTaskSaveEvent(int position, String fatherName, String fatherDescription) {
        this.position=position;
        this.fatherName=fatherName;
        this.fatherDescription=fatherDescription;
    }
}
