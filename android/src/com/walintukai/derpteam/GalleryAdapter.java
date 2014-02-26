package com.walintukai.derpteam;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter {
	
	private Context context;
	ImageView imageView;
	
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
//		ImageView imageView;
		
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(340, 340));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		}
		else {
			imageView = (ImageView) convertView;
		}
		
		imageView.setImageResource(thumbIds[position]);
//		new GetRandomPicTask().execute();
		
		return imageView;
	}
	
	private class GetRandomPicTask extends AsyncTask<Void, Void, Void> {
		Picture picture;
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getImageJsonString(0);
	    	
	    	JSONReader reader = new JSONReader(context);
	    	picture = reader.getPictureObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	UrlImageViewHelper.setUrlDrawable(imageView, picture.getImageUrl(), R.drawable.image_placeholder);
	        return;
	    }
	}

}
