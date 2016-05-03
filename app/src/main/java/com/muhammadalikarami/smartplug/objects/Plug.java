package com.muhammadalikarami.smartplug.objects;

/**
 * Created by Admin on 4/30/2016.
 */
public class Plug {

    private int plugNum;
    private String plugName;
    private boolean isOn;

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
