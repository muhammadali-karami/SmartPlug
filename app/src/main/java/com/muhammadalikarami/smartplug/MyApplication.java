package com.muhammadalikarami.smartplug;

import android.app.Application;

/**
 * Created by Admin on 5/1/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Utility.setContext(this);
    }
}
