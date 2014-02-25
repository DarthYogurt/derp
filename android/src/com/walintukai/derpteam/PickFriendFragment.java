package com.walintukai.derpteam;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PickFriendFragment extends Fragment {

	private static final String KEY_IMG_FILENAME = "imgFilename";
	private static final String KEY_CAPTION = "caption";
	
	private List<GraphUser> fbFriends;
	private ListView listView;
	private String imgFilename;
	private String caption;
	private String targetName;
	private String targetFbId;
	private int targetUserId;

	static PickFriendFragment newInstance(String imgFilename, String caption) {
		PickFriendFragment fragment = new PickFriendFragment();
		Bundle args = new Bundle();
		args.putString(KEY_IMG_FILENAME, imgFilename);
		args.putString(KEY_CAPTION, caption);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pick_friend, container, false);
		
		Bundle args = getArguments();
		imgFilename = args.getString(KEY_IMG_FILENAME);
		caption = args.getString(KEY_CAPTION);
		
		listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				GraphUser friend = fbFriends.get(position);
				targetName = friend.getName();
				targetFbId = friend.getId();
				
				AssignTeamDialogFrament dialog = new AssignTeamDialogFrament();
				dialog.show(getActivity().getFragmentManager(), "assignTeam");
			}
		});
		
		requestFacebookFriends(Session.getActiveSession());
		
		return view;
	}
	
	private void requestFacebookFriends(Session session) {
		Request friendsRequest = createRequest(session);
		friendsRequest.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				fbFriends = getResults(response);
				Collections.sort(fbFriends, new GraphUserComparator());
				FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), R.layout.listview_row_friend, fbFriends);
				listView.setAdapter(adapter);
			}
		});
		friendsRequest.executeAsync();
	}
	
	private Request createRequest(Session session) {
		Request request = Request.newGraphPathRequest(session, "me/friends", null);

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[] {"id", "name"};
		fields.addAll(Arrays.asList(requiredFields));

		Bundle parameters = request.getParameters();
		parameters.putString("fields", TextUtils.join(",", fields));
		request.setParameters(parameters);

		return request;
    }
	
	private List<GraphUser> getResults(Response response) {
		GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
		GraphObjectList<GraphObject> data = multiResult.getData();
		return data.castToListOf(GraphUser.class);
	}
	
	private class GraphUserComparator implements Comparator<GraphUser> {
		@Override
		public int compare(GraphUser user1, GraphUser user2) {
			return user1.getName().compareTo(user2.getName());
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
			writer.createJsonForImage(imgFilename, caption, targetFbId, targetUserId);
			writer.logJson(JSONWriter.FILENAME_ASSIGN_TEAM);
			
			HttpPostRequest post = new HttpPostRequest(getActivity());
			post.createPost(HttpPostRequest.UPLOAD_PIC_URL);
			post.addJSON(JSONWriter.FILENAME_ASSIGN_TEAM);
			post.addPicture(imgFilename);
			post.sendPost();
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	
	    	Intent intent = new Intent(getActivity(), MainActivity.class);
	    	startActivity(intent);
	    	getActivity().finish();
	    	
//	    	FragmentManager fm = getFragmentManager();
//			FragmentTransaction ft = fm.beginTransaction();
//			MainFragment fragment = MainFragment.newInstance();
//			ft.replace(R.id.fragment_container, fragment);
//			ft.commit();
	    	
	        return;
	    }
	}
	
}