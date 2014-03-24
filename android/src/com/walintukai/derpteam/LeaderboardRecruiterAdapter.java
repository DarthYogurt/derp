package com.walintukai.derpteam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LeaderboardRecruiterAdapter extends ArrayAdapter<LeaderboardItem> {
	
	private Context context;
	private int layoutResourceId;
	private List<LeaderboardItem> recruiters;
	
	public LeaderboardRecruiterAdapter(Context context, int layoutResourceId, List<LeaderboardItem> recruiters) {
		super(context, layoutResourceId, recruiters);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.recruiters = recruiters;
	}
	
	private static class ViewHolder {
		private CustomFontBoldTextView name;
		private CustomFontBoldTextView points;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (CustomFontBoldTextView) convertView.findViewById(R.id.first_name);
            holder.points = (CustomFontBoldTextView) convertView.findViewById(R.id.points);
            
            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }

		holder.name.setText(GlobalMethods.getFirstName(recruiters.get(position).getFbName()));
		holder.points.setText(Integer.toString(recruiters.get(position).getPoints()));
        
        return convertView;
	}
	
}
