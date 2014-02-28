package com.walintukai.derpteam;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
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
	
	private Context context;
	private int layoutResourceId;
	private List<Member> members;
	private ViewHolder holder;
	private String name;
	
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
		private TextView score;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.posterFbPic = (ImageView) convertView.findViewById(R.id.poster_fb_picture);
            holder.posterName = (TextView) convertView.findViewById(R.id.poster_name);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.memberPic = (ImageView) convertView.findViewById(R.id.member);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }

		setFbFields(members.get(position));
		
        holder.title.setText(members.get(position).getTitle());
        UrlImageViewHelper.setUrlDrawable(holder.memberPic, members.get(position).getImageUrl());

        return convertView;
	}
	
	private void setFbFields(Member member) {
		String posterFbId = member.getPosterFbId();
		String graphPath = "/" + posterFbId + "/";
		String graphPathPic = "http://graph.facebook.com/" + posterFbId + "/picture";
		
		new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
			public void onCompleted(Response response) {
				try {
					JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
					name = jObject.getString("first_name");
					holder.posterName.setText(name);
				} 
				catch (JSONException e) { e.printStackTrace(); }
			}
		}).executeAsync();
		
		UrlImageViewHelper.setUrlDrawable(holder.posterFbPic, graphPathPic, R.drawable.image_placeholder);
	}

}
