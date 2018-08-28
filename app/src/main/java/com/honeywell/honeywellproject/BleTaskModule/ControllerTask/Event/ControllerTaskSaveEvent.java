package com.honeywell.honeywellproject.BleTaskModule.ControllerTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class ControllerTaskSaveEvent {


    public int           position;
    public String        controllerName;
    public String        controllerDescription;
    public ControllerTaskSaveEvent(int position, String controllerName, String controllerDescription) {
        this.position=position;
        this.controllerName=controllerName;
        this.controllerDescription=controllerDescription;
    }
}
