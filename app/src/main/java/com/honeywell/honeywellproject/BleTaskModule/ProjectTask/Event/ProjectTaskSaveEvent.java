package com.honeywell.honeywellproject.BleTaskModule.ProjectTask.Event;

/**
 * Created by QHT on 2017-12-26.
 */
public class ProjectTaskSaveEvent {


    public int            position;
    public String        projectName;
    public String        projectDescription;
    public ProjectTaskSaveEvent(int position, String projectName,String projectDescription) {
        this.position=position;
        this.projectName=projectName;
        this.projectDescription=projectDescription;
    }
}
