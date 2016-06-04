package com.muhammadalikarami.smartplug.objects;

/**
 * Created by Admin on 4/30/2016.
 */
public class Alarm {

    private int alarmId;
    private String alarmName;
    private long whenSetTime;
    private long executeTime;
    private int plugNum;
    private String plugName;
    private boolean isOn;

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public long getWhenSetTime() {
        return whenSetTime;
    }

    public void setWhenSetTime(long whenSetTime) {
        this.whenSetTime = whenSetTime;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public int getPlugNum() {
        return plugNum;
    }

    public void setPlugNum(int plugNum) {
        this.plugNum = plugNum;
    }

    public String getPlugName() {
        return plugName;
    }

    public void setPlugName(String plugName) {
        this.plugName = plugName;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }
}
