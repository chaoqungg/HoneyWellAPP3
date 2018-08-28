package com.honeywell.honeywellproject.ELModule.data;

/**
 * Created by QHT on 2018-01-31.
 */
public class LightEvent {
    public String commandstr;
    public int[]        command;
    public LightEvent(int[] command,String commandstr) {
        this.command=command;
        this.commandstr=commandstr;
    }
}
