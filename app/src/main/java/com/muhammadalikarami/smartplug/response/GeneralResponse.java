package com.muhammadalikarami.smartplug.response;

/**
 * Created by Admin on 6/2/2016.
 */
public class GeneralResponse {
    private boolean ok;
    private String[] messages;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }
}
