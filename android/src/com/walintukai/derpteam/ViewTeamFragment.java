package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ViewTeamFragment extends Fragment {
	
	private static final String KEY_FB_ID = "fbId";
	
	private String fbId;
	private ListView listView;
	private TeamListAdapter adapter;
	
	
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
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle args = getArguments();
		fbId = args.getString(KEY_FB_ID);
		
		listView = (ListView) view.findViewById(R.id.team_listview);
		
		new GetMembersTask().execute();
		
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().getFragmentManager().popBackStack();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class GetMembersTask extends AsyncTask<Void, Void, List<Member>> {
		
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
	        return;
	    }
	}

}
