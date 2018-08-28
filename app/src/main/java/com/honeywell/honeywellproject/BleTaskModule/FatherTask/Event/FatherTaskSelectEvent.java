package com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event;

/**
 * Created by QHT on 2017-10-10.
 */
public class FatherTaskSelectEvent {


    public int            position;
    public boolean        checked;

    public FatherTaskSelectEvent(int position,boolean checked) {
        this.position=position;
        this.checked=checked;
    }
}
