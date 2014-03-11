package com.walintukai.derpteam;

import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewTeamFragment extends Fragment {
	
	private static final String KEY_FB_ID = "fbId";
	
	private String fbId;
	private ListView listView;
	private TeamListAdapter adapter;
	private TextView teamOwnerName;
	private ImageView teamOwnerPicture;
	
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
		
		View header = inflater.inflate(R.layout.listview_team_header, null);
		teamOwnerName = (TextView) header.findViewById(R.id.team_owner_name);
		teamOwnerPicture = (ImageView) header.findViewById(R.id.team_owner_picture);
		listView = (ListView) view.findViewById(R.id.team_listview);
		listView.addHeaderView(header);
		
		if (GlobalMethods.isNetworkAvailable(getActivity())) {
			setHeader(fbId);
			String teamOwnerPictureUrl = "http://graph.facebook.com/" + fbId + "/picture?type=large";
			UrlImageViewHelper.setUrlDrawable(teamOwnerPicture, teamOwnerPictureUrl);
			new SetListViewTask().execute();
		}
		else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
		
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
	
	private class SetListViewTask extends AsyncTask<Void, Void, List<Member>> {
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

}
