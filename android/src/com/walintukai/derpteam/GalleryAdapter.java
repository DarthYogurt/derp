package com.walintukai.derpteam;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class GalleryAdapter extends ArrayAdapter<Member> {
	
	private Context context;
	private int layoutResourceId;
	private List<Member> members;
	
	public GalleryAdapter(Context context, int layoutResourceId, List<Member> members) {
		super(context, layoutResourceId, members);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.members = members;
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

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }
		
        UrlImageViewHelper.setUrlDrawable(holder.imageView, members.get(position).getImageUrl());

        return convertView;
	}
	
	public void refreshList(List<Member> newMembers) {
		members.clear();
		members.addAll(newMembers);
		this.notifyDataSetChanged();
	}
	
}
