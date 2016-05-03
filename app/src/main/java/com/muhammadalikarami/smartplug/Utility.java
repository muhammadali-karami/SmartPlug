package com.muhammadalikarami.smartplug;

import android.content.Context;

/**
 * Created by Admin on 5/1/2016.
 */
public class Utility {

    private static Context context;

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static void setContext(Context context) {
        Utility.context = context;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static Context getContext() {
        return context;
    }

}
