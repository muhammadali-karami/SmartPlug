package com.muhammadalikarami.smartplug.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.muhammadalikarami.smartplug.R;
import com.muhammadalikarami.smartplug.Utility;

public class CustomTextView extends TextView{

	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

//		int fontType = a.getInt(R.styleable.CustomTextView_fonts , 3);
//		//do something with str
//		setFont(context, Utility.fontsMap.get(fontType + ""));

		a.recycle();
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView, defStyle, 0);

//		int fontType = a.getInt(R.styleable.CustomTextView_fonts, 3);
//		//do something with str
//		setFont(context, Utility.fontsMap.get(fontType + ""));

		a.recycle();
	}

	private void setFont(Context context , String fontType) {
		Typeface font = Utility.getFont(context, fontType);
		setTypeface(font);

	}
}
