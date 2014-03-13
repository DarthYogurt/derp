package com.walintukai.derpteam;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontBoldTextView extends TextView {

	public CustomFontBoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomFontBoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomFontBoldTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SourceSansPro-Bold.otf");
		setTypeface(tf);
	}
	
}
