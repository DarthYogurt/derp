package com.walintukai.derpteam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class FriendsListAdapter extends ArrayAdapter<GraphUser> {
	
	private Context context;
	private int layoutResourceId;
	private List<GraphUser> friends;
	
	public FriendsListAdapter(Context context, int layoutResourceId, List<GraphUser> friends) {
		super(context, layoutResourceId, friends);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.friends = friends;
	}
	
	private static class ViewHolder {
		private ProfilePictureView picture;
		private TextView name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.picture = (ProfilePictureView) convertView.findViewById(R.id.friend_picture);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }
		
		holder.picture.setProfileId(friends.get(position).getId());
        holder.name.setText(friends.get(position).getName());

        return convertView;
	}

}
