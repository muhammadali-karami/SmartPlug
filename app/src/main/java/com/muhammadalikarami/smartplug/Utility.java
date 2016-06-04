package com.muhammadalikarami.smartplug;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Admin on 5/1/2016.
 */
public class Utility {

    private static Context context;
    public static HashMap<String , String> fontsMap = new HashMap<>();
    private static Typeface Font;

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static void setContext(Context context) {
        Utility.context = context;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static Context getContext() {
        return context;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static Typeface getFont(Context context , String fontName) {

        Font = Typefaces.get(context, "fonts/" + fontName + ".ttf");
        return Font;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public static class Typefaces {
        private static final String TAG = "Typefaces";

        private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

        public static Typeface get(Context c, String assetPath) {
            synchronized (cache) {
                if (!cache.containsKey(assetPath)) {
                    try {
                        Typeface t = Typeface.createFromAsset(c.getAssets(),
                                assetPath);
                        cache.put(assetPath, t);
                    } catch (Exception e) {
                        return null;
                    }
                }
                return cache.get(assetPath);
            }
        }
    }
}
