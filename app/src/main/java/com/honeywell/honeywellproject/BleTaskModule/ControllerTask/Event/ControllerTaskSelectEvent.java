package com.honeywell.honeywellproject.BleTaskModule.ControllerTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class ControllerTaskSelectEvent {


    public int            position;
    public boolean        checked;

    public ControllerTaskSelectEvent(int position, boolean checked) {
        this.position=position;
        this.checked=checked;
    }
}
