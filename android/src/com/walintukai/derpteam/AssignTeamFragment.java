package com.walintukai.derpteam;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AssignTeamFragment extends Fragment {

	private static final String KEY_IMG_FILENAME = "imgFilename";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	
	private String imgFilename;
	private String title;
	private String caption;
	private String targetName;
	private String targetFbId;
	private String targetUserId;

	static AssignTeamFragment newInstance(String imgFilename, String title, String caption) {
		AssignTeamFragment fragment = new AssignTeamFragment();
		Bundle args = new Bundle();
		args.putString(KEY_IMG_FILENAME, imgFilename);
		args.putString(KEY_TITLE, title);
		args.putString(KEY_CAPTION, caption);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pick_team, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle args = getArguments();
		imgFilename = args.getString(KEY_IMG_FILENAME);
		title = args.getString(KEY_TITLE);
		caption = args.getString(KEY_CAPTION);
		
		final List<Friend> fbFriends = GlobalMethods.readFriendsArray(getActivity());
		
		TextView header = (TextView) view.findViewById(R.id.pick_team_header);
		header.setText(R.string.assign_team);
		
		ListView listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				Friend friend = fbFriends.get(position);
				targetName = friend.getFbName();
				targetFbId = friend.getFbId();
				
				AssignTeamDialogFrament dialog = new AssignTeamDialogFrament();
				dialog.show(getActivity().getFragmentManager(), "assignTeam");
			}
		});
		
		FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), R.layout.listview_row_friend, fbFriends);
		listView.setAdapter(adapter);
		
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
	
	private class AssignTeamDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Assign to " + targetName + "'s Team?")
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			new SendImageTask().execute();
	        		}
	        	})
	        	.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			dismiss();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private class SendImageTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.dialog_send_to_server));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);	
		}
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
			targetUserId = get.getUserId(targetFbId);
	    	
	    	JSONWriter writer = new JSONWriter(getActivity());
			writer.createJsonForMember(imgFilename, title, caption, targetFbId, targetUserId);
			writer.logJson(JSONWriter.FILENAME_ASSIGN_TEAM);
			
			if (GlobalMethods.isNetworkAvailable(getActivity())) {
				HttpPostRequest post = new HttpPostRequest(getActivity());
				post.createPost(HttpPostRequest.UPLOAD_PIC_URL);
				post.addJSON(JSONWriter.FILENAME_ASSIGN_TEAM);
				post.addPicture(imgFilename);
				post.sendPost();
			}
			else {
				getActivity().runOnUiThread(new Runnable() {
					public void run() { 
						Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
					}
				});
			}
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	Toast.makeText(getActivity(), R.string.derp_assigned, Toast.LENGTH_SHORT).show();
	    	
	    	Intent intent = new Intent(getActivity(), MainActivity.class);
	    	startActivity(intent);
	    	getActivity().finish();
	    	
	        return;
	    }
	}
	
}
