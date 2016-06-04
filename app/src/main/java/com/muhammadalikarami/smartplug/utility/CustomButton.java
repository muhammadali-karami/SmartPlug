package com.muhammadalikarami.smartplug.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.muhammadalikarami.smartplug.R;
import com.muhammadalikarami.smartplug.Utility;

/**
 * Created by Arash on 11/4/2015.
 */
public class CustomButton extends Button {

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        int fontType = a.getInt(R.styleable.CustomTextView_fonts , 3);
        //do something with str
        setFont(context, Utility.fontsMap.get(fontType + ""));

        a.recycle();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView, defStyle, 0);

        int fontType = a.getInt(R.styleable.CustomTextView_fonts, 3);
        //do something with str
        setFont(context, Utility.fontsMap.get(fontType + ""));

        a.recycle();
    }

    private void setFont(Context context , String fontType) {
        Typeface font = Utility.getFont(context, fontType);
        setTypeface(font);

    }
}
