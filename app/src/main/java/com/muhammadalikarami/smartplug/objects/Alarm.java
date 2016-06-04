package com.muhammadalikarami.smartplug.objects;

/**
 * Created by Admin on 4/30/2016.
 */
public class Alarm {

    private int alarmId;
    private String alarmName;
    private String whenSetTime;
    private String executeTime;
    private int intervalTime;
    private int plugNum;
    private String plugName;
    private String plugStatus;

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

    public String getWhenSetTime() {
        return whenSetTime;
    }

    public void setWhenSetTime(String whenSetTime) {
        this.whenSetTime = whenSetTime;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
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

    public String getPlugStatus() {
        return plugStatus;
    }

    public void setPlugStatus(String plugStatus) {
        this.plugStatus = plugStatus;
    }
}
