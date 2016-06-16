package com.muhammadalikarami.smartplug.objects;

/**
 * Created by Admin on 4/30/2016.
 */
public class Alarm {

    private int alarmId;
    private int plugNum;
    private String alarmName;
    private String whenSetTime;
    private String executeTime;
    private String alarmStatus;

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getPlugNum() {
        return plugNum;
    }

    public void setPlugNum(int plugNum) {
        this.plugNum = plugNum;
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

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}
