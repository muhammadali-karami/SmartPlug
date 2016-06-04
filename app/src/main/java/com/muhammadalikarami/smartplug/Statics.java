package com.muhammadalikarami.smartplug;

import android.graphics.Typeface;
import android.os.Environment;

public class Statics {

    // volley config
        public static final int DEFAULT_TIMEOUT_MS = 30000;// 30 second
        public static final int DEFAULT_MAX_RETRIES = 0;// don't retry
        public static final float DEFAULT_BACKOFF_MULT = 1f;
    // end

    public static final String fixUrl = "http://192.168.0.1/";
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // AP IP Address
    public static final String simpleUrl      =   fixUrl + "simple";
    public static final String scheduleUrl    =   fixUrl + "schedule";
    public static final String cancelUrl      =   fixUrl + "cancel";
    public static final String syncUrl        =   fixUrl + "sync";
}
