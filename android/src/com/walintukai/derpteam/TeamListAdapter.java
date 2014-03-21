package com.walintukai.derpteam;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TeamListAdapter extends ArrayAdapter<Member> {
	
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
		private CustomFontBoldTextView posterName;
		private CustomFontBoldTextView targetName;
		private CustomFontBoldTextView title;
		private AspectRatioImageView derpPic;
		private CustomFontBoldTextView caption;
		private CustomFontBoldTextView upVote;
		private CustomFontBoldTextView downVote;
		private LinearLayout commentContainer;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.posterFbPic = (ImageView) convertView.findViewById(R.id.poster_fb_picture);
            holder.posterName = (CustomFontBoldTextView) convertView.findViewById(R.id.poster_name);
            holder.targetName = (CustomFontBoldTextView) convertView.findViewById(R.id.target_name);
            holder.title = (CustomFontBoldTextView) convertView.findViewById(R.id.title);
            holder.derpPic = (AspectRatioImageView) convertView.findViewById(R.id.member);
            holder.caption = (CustomFontBoldTextView) convertView.findViewById(R.id.caption);
            holder.upVote = (CustomFontBoldTextView) convertView.findViewById(R.id.vote_up_count);
            holder.downVote = (CustomFontBoldTextView) convertView.findViewById(R.id.vote_down_count);
    		holder.commentContainer = (LinearLayout) convertView.findViewById(R.id.comment_container);
            
            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }

		UrlImageViewHelper.setUrlDrawable(holder.posterFbPic, members.get(position).getPosterFbPicUrl(), 
										  R.drawable.image_placeholder);
		holder.posterName.setText(members.get(position).getPosterFirstName());
		holder.targetName.setText(members.get(position).getTargetFirstName());
        holder.title.setText(members.get(position).getTitle());
        UrlImageViewHelper.setUrlDrawable(holder.derpPic, members.get(position).getImageUrl(), 
        								  R.drawable.image_placeholder);
        holder.caption.setText(members.get(position).getCaption());
        holder.upVote.setText(Integer.toString(members.get(position).getUpVote()));
        holder.downVote.setText(Integer.toString(members.get(position).getDownVote()));
        
        holder.derpPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fm = ((Activity)context).getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ViewMemberFragment fragment = ViewMemberFragment.newInstance(members.get(position).getPicId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
        });
        
        holder.posterFbPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fm = ((Activity)context).getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(members.get(position).getPosterFbId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
        });
        
        List<Comment> comments = members.get(position).getComments();
    	holder.commentContainer.removeAllViews();
    	for (int i = 0; i < comments.size(); i++) {
        	Comment comment = comments.get(i);
        	
        	LinearLayout row = new LinearLayout(context);
    		row.setOrientation(LinearLayout.HORIZONTAL);
    		row.setPadding(0, 0, 0, 10);
    		
    		CustomFontBoldTextView tvName = new CustomFontBoldTextView(context);
    		tvName.setText(comment.getPosterFirstName().toUpperCase());
    		tvName.setTextSize(14);
    		tvName.setTextColor(Color.parseColor("#30acff"));
    		tvName.setPadding(0, 0, 10, 0);
    		row.addView(tvName);
    		
    		CustomFontTextView tvComment = new CustomFontTextView(context);
    		tvComment.setText(comment.getComment());
    		tvComment.setTextSize(14);
    		tvComment.setTextColor(Color.parseColor("#000000"));
    		row.addView(tvComment);
    		
    		holder.commentContainer.addView(row);
        }

        return convertView;
	}
	
}
