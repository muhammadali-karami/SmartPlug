package com.muhammadalikarami.smartplug.response;

import com.muhammadalikarami.smartplug.objects.Alarm;
import com.muhammadalikarami.smartplug.objects.Plug;

/**
 * Created by Admin on 6/2/2016.
 */
public class SyncResponse {
    private boolean ok;
    private String[] messages;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public class Data {
        private Plug[] plugs;
        private Alarm[] alarms;

        public Plug[] getPlugs() {
            return plugs;
        }

        public void setPlugs(Plug[] plugs) {
            this.plugs = plugs;
        }

        public Alarm[] getAlarms() {
            return alarms;
        }

        public void setAlarms(Alarm[] alarms) {
            this.alarms = alarms;
        }
    }
}
