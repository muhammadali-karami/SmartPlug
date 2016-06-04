package com.muhammadalikarami.smartplug.response;

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

    public class Data {
        private Plugs[] plugs;

        public Plugs[] getPlugs() {
            return plugs;
        }

        public void setPlugs(Plugs[] plugs) {
            this.plugs = plugs;
        }
    }

    public class Plugs {
        private int plugNum;
        private String plugName;
        private String plugStatus;

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
}
