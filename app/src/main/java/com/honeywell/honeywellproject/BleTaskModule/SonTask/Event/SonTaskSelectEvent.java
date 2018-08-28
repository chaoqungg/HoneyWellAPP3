package com.honeywell.honeywellproject.BleTaskModule.SonTask.Event;

/**
 * Created by QHT on 2017-10-10.
 */
public class SonTaskSelectEvent {


    public int            position;
    public boolean        checked;

    public SonTaskSelectEvent(int position, boolean checked) {
        this.position=position;
        this.checked=checked;
    }
}
