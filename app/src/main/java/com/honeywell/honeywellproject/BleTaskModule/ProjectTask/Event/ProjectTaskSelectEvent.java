package com.honeywell.honeywellproject.BleTaskModule.ProjectTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class ProjectTaskSelectEvent {


    public int            position;
    public boolean        checked;

    public ProjectTaskSelectEvent(int position, boolean checked) {
        this.position=position;
        this.checked=checked;
    }
}
