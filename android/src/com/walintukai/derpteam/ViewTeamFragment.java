package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewTeamFragment extends Fragment {
	
	private static final String KEY_FB_ID = "fbId";
	
	private Preferences prefs;
	private String fbId;
	private ListView listView;
	private TeamListAdapter adapter;
	private TextView teamOwnerName;
	private ImageView teamOwnerPicture;
	private LinearLayout statsContainer;
	
	static ViewTeamFragment newInstance(String fbId) {
		ViewTeamFragment fragment = new ViewTeamFragment();
		Bundle args = new Bundle();
		args.putString(KEY_FB_ID, fbId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_team, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		Bundle args = getArguments();
		fbId = args.getString(KEY_FB_ID);
		
		prefs = new Preferences(getActivity());
		
		View header = inflater.inflate(R.layout.listview_team_header, null);
		teamOwnerName = (TextView) header.findViewById(R.id.team_owner_name);
		teamOwnerPicture = (ImageView) header.findViewById(R.id.team_owner_picture);
		statsContainer = (LinearLayout) header.findViewById(R.id.stats_container);
		listView = (ListView) view.findViewById(R.id.team_listview);
		listView.addHeaderView(header);
		
		if (GlobalMethods.isNetworkAvailable(getActivity())) {
			setHeader(fbId);
			String teamOwnerPictureUrl = "http://graph.facebook.com/" + fbId + "/picture?type=large";
			UrlImageViewHelper.setUrlDrawable(teamOwnerPicture, teamOwnerPictureUrl);
			if (fbId.equalsIgnoreCase(prefs.getFbUserId())) { 
				statsContainer.setVisibility(View.VISIBLE);
				new ShowStatsTask().execute();
				new ShowTeamTask().execute();
			}
			else { new ShowTeamTask().execute(); }
		}
		else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
		
		return view;
	}
	
	private class ShowStatsTask extends AsyncTask<Void, Void, List<Member>> {
		private ProgressDialog loadingDialog;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected List<Member> doInBackground(Void... params) {
	    	JSONWriter writer = new JSONWriter(getActivity());
	    	writer.createJsonForGetStats();
	    	
	    	HttpPostRequest post = new HttpPostRequest(getActivity());
	    	post.createPost(HttpPostRequest.GET_STATS_URL);
	    	post.addJSON(JSONWriter.FILENAME_GET_STATS);
	    	String jsonString = post.sendPostReturnJson();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	List<Member> statsArray = reader.getStatsArray(jsonString);
	    	Collections.sort(statsArray, new VoteComparator());
		    
	        return statsArray;
	    }

	    protected void onPostExecute(List<Member> result) {
	    	super.onPostExecute(result);
	    	for (int i = 0; i < result.size(); i++) {
	    		Member member = result.get(i);
	    		addStatsRow(member.getImageUrl(), member.getTitle(), member.getUpVote(), member.getDownVote());
	    	}

			loadingDialog.hide();
	        return;
	    }
	}
	
	private class ShowTeamTask extends AsyncTask<Void, Void, List<Member>> {
		private ProgressDialog loadingDialog;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected List<Member> doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	JSONReader reader = new JSONReader(getActivity());
		    String jsonString = get.getTeamJsonString(fbId);
		    
		    List<Member> array = new ArrayList<Member>();
		    array = reader.getTeamMembersArray(jsonString);
	        return array;
	    }

	    protected void onPostExecute(List<Member> result) {
	    	super.onPostExecute(result);
	    	adapter = new TeamListAdapter(getActivity(), R.layout.listview_row_member, result);
			listView.setAdapter(adapter);
			loadingDialog.hide();
	        return;
	    }
	}
	
	private void setHeader(String fbId) {
		String graphPath = "/" + fbId + "/";
		new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
			public void onCompleted(Response response) {
				try {
					JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
					String firstName = jObject.getString("first_name");
					teamOwnerName.setText(firstName);
				} 
				catch (JSONException e) { e.printStackTrace(); }
			}
		}).executeAsync();
	}
	
	private void addStatsRow(String imageUrl, String title, int upVote, int downVote) {
		LinearLayout row = new LinearLayout(getActivity());
		row.setOrientation(LinearLayout.HORIZONTAL);
		
		Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SourceSansPro-Bold.otf");
		Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SourceSansPro-Regular.otf");
		
		ImageView ivPicture = new ImageView(getActivity());
		ivPicture.setImageResource(R.drawable.image_placeholder);
		ivPicture.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
		ivPicture.setPadding(0, 0, 20, 0);
		UrlImageViewHelper.setUrlDrawable(ivPicture, imageUrl);
		row.addView(ivPicture);
		
		LinearLayout infoContainer = new LinearLayout(getActivity());
		infoContainer.setOrientation(LinearLayout.VERTICAL);
		infoContainer.setPadding(0, 12, 0, 0);
		
		TextView tvTitle = new TextView(getActivity());
		tvTitle.setTypeface(regular);
		tvTitle.setText(title);
		tvTitle.setTextSize(16);
		tvTitle.setPadding(0, 0, 0, 6);
		infoContainer.addView(tvTitle);
		
		LinearLayout voteContainer = new LinearLayout(getActivity());
		ImageView ivUpVote = new ImageView(getActivity());
		ivUpVote.setImageResource(R.drawable.arrow_up_small);
		ivUpVote.setPadding(0, 0, 2, 0);
		voteContainer.addView(ivUpVote);
		
		TextView tvUpVote = new TextView(getActivity());
		tvUpVote.setTypeface(bold);
		tvUpVote.setTextColor(Color.parseColor("#00b200"));
		tvUpVote.setTextSize(16);
		tvUpVote.setText(Integer.toString(upVote));
		tvUpVote.setPadding(0, 0, 14, 0);
		voteContainer.addView(tvUpVote);
		
		ImageView ivDownVote = new ImageView(getActivity());
		ivDownVote.setImageResource(R.drawable.arrow_down_small);
		ivDownVote.setPadding(0, 0, 2, 0);
		voteContainer.addView(ivDownVote);
		
		TextView tvDownVote = new TextView(getActivity());
		tvDownVote.setTypeface(bold);
		tvDownVote.setTextColor(Color.parseColor("#cc0000"));
		tvDownVote.setTextSize(16);
		tvDownVote.setText(Integer.toString(downVote));
		voteContainer.addView(tvDownVote);
		
		infoContainer.addView(voteContainer);
		
		row.addView(infoContainer);
		
		statsContainer.addView(row);
	}
	
	private class VoteComparator implements Comparator<Member> {
		@Override
		public int compare(Member member1, Member member2) {
			return member2.getUpVote() - member1.getUpVote();
		}
	}

}
