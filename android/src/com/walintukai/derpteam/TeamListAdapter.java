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
import android.widget.TextView;

public class TeamListAdapter extends ArrayAdapter<Member> {
	
	private static final String GRAPH_PATH = "http://graph.facebook.com/";
	
	private Context context;
	private int layoutResourceId;
	private List<Member> members;
	
	public TeamListAdapter(Context context, int layoutResourceId, List<Member> members) {
		super(context, layoutResourceId, members);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.members = members;
	}
	
	private static class ViewHolder {
		private ImageView posterFbPic;
		private TextView posterName;
		private TextView title;
		private ImageView memberPic;
		private TextView caption;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.posterFbPic = (ImageView) convertView.findViewById(R.id.poster_fb_picture);
            holder.posterName = (TextView) convertView.findViewById(R.id.poster_name);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.memberPic = (ImageView) convertView.findViewById(R.id.member);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }

		String graphPathPic = GRAPH_PATH + members.get(position).getPosterFbId() + "/picture";
		UrlImageViewHelper.setUrlDrawable(holder.posterFbPic, graphPathPic, R.drawable.image_placeholder);
		holder.posterName.setText(members.get(position).getPosterFirstName());
        holder.title.setText(members.get(position).getTitle());
        UrlImageViewHelper.setUrlDrawable(holder.memberPic, members.get(position).getImageUrl(), R.drawable.image_placeholder);
        holder.caption.setText(members.get(position).getCaption());

        return convertView;
	}

}
