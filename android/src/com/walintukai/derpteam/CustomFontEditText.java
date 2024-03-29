package com.walintukai.derpteam;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomFontEditText extends EditText {

	public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomFontEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomFontEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SourceSansPro-Regular.otf");
		setTypeface(tf);
	}

 }