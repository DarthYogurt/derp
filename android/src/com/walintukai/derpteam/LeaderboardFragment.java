package com.walintukai.derpteam;

import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LeaderboardFragment extends Fragment {
	
	private ListView listView;
	
	static LeaderboardFragment newInstance() {
		LeaderboardFragment fragment = new LeaderboardFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
		listView = (ListView) view.findViewById(R.id.leaderboard_listview);
		new GetLeaderboardTask().execute();
		
		return view;
	}
	
	private class GetLeaderboardTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog loadingDialog;
		private List<LeaderboardTeam> topTeamArray;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected Void doInBackground(Void... params) {	
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getTopTeamsJsonString();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	topTeamArray = reader.getTopTeamArray(jsonString);
	    	
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	LeaderboardTeamAdapter teamAdapter = new LeaderboardTeamAdapter(getActivity(), 
	    			R.layout.listview_row_topteam, topTeamArray);
	    	SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity(), 
	    			SeparatedListAdapter.TYPE_LEADERBOARD);
	    	adapter.addSection("Most Derpy Teams", teamAdapter);
	    	listView.setAdapter(adapter);
	    	loadingDialog.hide();
	        return;
	    }
	}
	
}
