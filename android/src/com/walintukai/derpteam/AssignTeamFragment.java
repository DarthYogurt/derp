package com.walintukai.derpteam;

import java.util.List;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AssignTeamFragment extends Fragment {

	private static final String KEY_IMG_FILENAME = "imgFilename";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	
	private Preferences prefs;
	private String imgFilename;
	private String title;
	private String caption;
	private String targetName;
	private String targetFbId;
	private String targetUserId;
	private String fbPostImageUrl;
	private String fbPostLinkUrl;

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
		
		prefs = new Preferences(getActivity());
		
		Bundle args = getArguments();
		imgFilename = args.getString(KEY_IMG_FILENAME);
		title = args.getString(KEY_TITLE);
		caption = args.getString(KEY_CAPTION);
		
		List<Friend> fbFriends = GlobalMethods.readFriendsArray(getActivity());
		List<Friend> activeFriends = GlobalMethods.readActiveFriendsArray(getActivity());
		
		ListView listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		
		FriendsListAdapter activeFriendsAdapter = new FriendsListAdapter(getActivity(), 
				R.layout.listview_row_friend, activeFriends);
		FriendsListAdapter fbFriendsAdapter = new FriendsListAdapter(getActivity(), 
				R.layout.listview_row_friend, fbFriends);
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity());
		adapter.addSection("Active Friends", activeFriendsAdapter);
		adapter.addSection("All Friends", fbFriendsAdapter);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				Friend friend = (Friend) parent.getAdapter().getItem(position);
				targetName = friend.getFbName();
				targetFbId = friend.getFbId();
				
				AssignTeamDialogFrament dialog = new AssignTeamDialogFrament();
				dialog.show(getActivity().getFragmentManager(), "assignTeam");
			}
		});
		
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
	
	private void goToMainPage() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
    	startActivity(intent);
    	getActivity().finish();
	}
	
	private class AssignTeamDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Assign to " + targetName + "'s Team?")
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			if (GlobalMethods.isNetworkAvailable(getActivity())) { new SendImageTask().execute(); }
	        			else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
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
	
	private class PostToWallDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Post to " + targetName + "'s Facebook Wall?")
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			postToWallDialog();
	        		}
	        	})
	        	.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			dismiss();
	        			goToMainPage();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private class SendImageTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;
		private String postReturnJson;
		
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
			
			HttpPostRequest post = new HttpPostRequest(getActivity());
			post.createPost(HttpPostRequest.UPLOAD_PIC_URL);
			post.addJSON(JSONWriter.FILENAME_ASSIGN_TEAM);
			post.addPicture(imgFilename);
			postReturnJson = post.sendPostReturnJson();
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	fbPostImageUrl = reader.getImageUrlForFbPost(postReturnJson);
	    	fbPostLinkUrl = reader.getLinkUrlForFbPost(postReturnJson);
	    	
	    	Toast.makeText(getActivity(), R.string.derp_assigned, Toast.LENGTH_SHORT).show();
	    	
	    	PostToWallDialogFrament dialog = new PostToWallDialogFrament();
			dialog.show(getActivity().getFragmentManager(), "postToWall");
	    	
	        return;
	    }
	}
	
	private void postToWallDialog() {
		Bundle params = new Bundle();
	    params.putString("name", "DerpTeam for Android");
	    params.putString("caption", caption);
	    params.putString("description", prefs.getFbFirstName() + " has put a new " + title + " on your team!");
	    params.putString("link", fbPostLinkUrl);
	    params.putString("picture", fbPostImageUrl);
	    params.putString("to", targetFbId);

	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(), Session.getActiveSession(), params))
	        .setOnCompleteListener(new OnCompleteListener() {
				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
	                	// When the story is posted, echo the success
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                    	Toast.makeText(getActivity(), R.string.posted_to_wall, Toast.LENGTH_SHORT).show();
	                    	goToMainPage();
	                    } 
	                    else {
	                    	// User clicked the Cancel button
	                    	Log.i("FB POST", "CANCELLED");
	                    	goToMainPage();
	                    }
	            	} 
	            	else if (error instanceof FacebookOperationCanceledException) {
	            		// User clicked the "x" button
	            		Log.i("FB POST", "CANCELLED");
	            		goToMainPage();
	            	} 
	            	else {
	            		// Generic, exception: network error
	            		Toast.makeText(getActivity(), R.string.posted_to_wall_error, Toast.LENGTH_SHORT).show();
	            		goToMainPage();
	            	}
				}
	        }).build();
	    feedDialog.show();
	}
	
}
