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

public class LeaderboardTeamAdapter extends ArrayAdapter<LeaderboardItem> {
	
	private Context context;
	private int layoutResourceId;
	private List<LeaderboardItem> teams;
	
	public LeaderboardTeamAdapter(Context context, int layoutResourceId, List<LeaderboardItem> teams) {
		super(context, layoutResourceId, teams);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.teams = teams;
	}
	
	private static class ViewHolder {
		private CustomFontBoldTextView name;
		private CustomFontBoldTextView points;
		private ImageView randomPic1;
		private ImageView randomPic2;
		private ImageView randomPic3;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (CustomFontBoldTextView) convertView.findViewById(R.id.first_name);
            holder.points = (CustomFontBoldTextView) convertView.findViewById(R.id.up_vote_count);
            holder.randomPic1 = (ImageView) convertView.findViewById(R.id.random_pic_1);
            holder.randomPic2 = (ImageView) convertView.findViewById(R.id.random_pic_2);
            holder.randomPic3 = (ImageView) convertView.findViewById(R.id.random_pic_3);
            
            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }

		holder.name.setText(GlobalMethods.getFirstName(teams.get(position).getFbName()));
		holder.points.setText(Integer.toString(teams.get(position).getPoints()));
		UrlImageViewHelper.setUrlDrawable(holder.randomPic1, teams.get(position).getPicUrl1());
		UrlImageViewHelper.setUrlDrawable(holder.randomPic2, teams.get(position).getPicUrl2());
		UrlImageViewHelper.setUrlDrawable(holder.randomPic3, teams.get(position).getPicUrl3());
        
        return convertView;
	}
	
}
