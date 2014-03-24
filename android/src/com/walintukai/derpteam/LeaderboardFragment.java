package com.walintukai.derpteam;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				LeaderboardItem leader = (LeaderboardItem) parent.getAdapter().getItem(position);	
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(leader.getFbId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		new GetLeaderboardTask().execute();
		return view;
	}
	
	private class GetLeaderboardTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog loadingDialog;
		private List<LeaderboardItem> topTeamArray;
		private List<LeaderboardItem> topRecruiterArray;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected Void doInBackground(Void... params) {	
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getTopTeamsJsonString();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	topTeamArray = reader.getTopTeamArray(jsonString);
	    	topRecruiterArray = reader.getTopRecruiterArray(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	LeaderboardTeamAdapter teamAdapter = new LeaderboardTeamAdapter(getActivity(), 
	    			R.layout.listview_row_topteam, topTeamArray);
	    	LeaderboardRecruiterAdapter recruiterAdapter = new LeaderboardRecruiterAdapter(getActivity(), 
	    			R.layout.listview_row_toprecruiter, topRecruiterArray);
	    	SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity(), 
	    			SeparatedListAdapter.TYPE_LEADERBOARD);
	    	adapter.addSection("Derpiest Teams", teamAdapter);
	    	adapter.addSection("Top Derpers", recruiterAdapter);
	    	listView.setAdapter(adapter);
	    	loadingDialog.hide();
	        return;
	    }
	}
	
}
