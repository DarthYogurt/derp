package com.walintukai.derpteam;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter {
	
	private Context context;
	private Integer[] thumbIds = { 
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
			R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
	};
	
	public GalleryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return thumbIds.length;
//		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//			imageView.setPadding(8, 8, 8, 8);
		}
		else {
			imageView = (ImageView) convertView;
		}
		
		imageView.setImageResource(thumbIds[position]);
		
		return imageView;
	}

}
