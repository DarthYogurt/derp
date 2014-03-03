package com.walintukai.derpteam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class FriendsListAdapter extends ArrayAdapter<Friend> {
	
	private Context context;
	private int layoutResourceId;
	private List<Friend> friends;
	
	public FriendsListAdapter(Context context, int layoutResourceId, List<Friend> friends) {
		super(context, layoutResourceId, friends);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.friends = friends;
	}
	
	private static class ViewHolder {
		private ImageView picture;
		private TextView name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.picture = (ImageView) convertView.findViewById(R.id.friend_picture);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }
		
		UrlImageViewHelper.setUrlDrawable(holder.picture, friends.get(position).getFbPicUrl());
        holder.name.setText(friends.get(position).getFbName());

        return convertView;
	}
	
}
