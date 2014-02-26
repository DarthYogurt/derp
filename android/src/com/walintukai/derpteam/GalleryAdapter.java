package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryAdapter extends ArrayAdapter<Picture> {
	
	private Context context;
	private int layoutResourceId;
	private List<Picture> pictures;
	
	public GalleryAdapter(Context context, int layoutResourceId, List<Picture> pictures) {
		super(context, layoutResourceId, pictures);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.pictures = pictures;
	}
	
	private static class ViewHolder {
		private ImageView imageView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.gridview_picture);
//			holder.imageView.setLayoutParams(new GridView.LayoutParams(340, 340));
//			holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }
		
        UrlImageViewHelper.setUrlDrawable(holder.imageView, pictures.get(position).getImageUrl());

        return convertView;
	}
	
	public void refreshList(List<Picture> newPictures) {
		pictures.clear();
		pictures.addAll(newPictures);
		this.notifyDataSetChanged();
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		
//		if (convertView == null) {
//			imageView = new ImageView(context);
//			imageView.setLayoutParams(new GridView.LayoutParams(340, 340));
//			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//		}
//		else {
//			imageView = (ImageView) convertView;
//		}
//		
//		return imageView;
//	}

}
