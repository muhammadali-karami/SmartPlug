package com.muhammadalikarami.smartplug.objects;

/**
 * Created by Admin on 4/30/2016.
 */
public class Alarm {

    private String alarmName;
    private long whenSetTime;
    private long executeTime;
    private int plugNum ;
    private String alarmType ;

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

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }
}
