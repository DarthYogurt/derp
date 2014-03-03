package com.walintukai.derpteam;

import java.util.Set;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	private Preferences prefs;
	private ImageView rateMember;
	private TextView caption;
	private Member member;
	private Set<Integer> votedPicturesSet;
	
	static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		prefs = new Preferences(getActivity());
		votedPicturesSet = GlobalMethods.readVotedPicturesSet(getActivity());
		
		caption = (TextView) view.findViewById(R.id.caption);
		rateMember = (ImageView) view.findViewById(R.id.rate_picture);
		new GetRandomMemberTask().execute();
		
		ImageView btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
				votedPicturesSet.add(member.getPicId());
				GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
				new SendVoteThread(member.getPicId(), false).start();
				new GetRandomMemberTask().execute();
			}
		});
		
		ImageView btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
				votedPicturesSet.add(member.getPicId());
				GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
				new SendVoteThread(member.getPicId(), true).start();
				new GetRandomMemberTask().execute();
			}
		});
		
		Button btnYourTeam = (Button) view.findViewById(R.id.btn_your_team);
		btnYourTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(prefs.getFbUserId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button btnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				TakePictureFragment fragment = TakePictureFragment.newInstance("");
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button btnGallery = (Button) view.findViewById(R.id.btn_gallery);
		btnGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				GalleryFragment fragment = GalleryFragment.newInstance();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button btnFriendsTeam = (Button) view.findViewById(R.id.btn_friends_team);
		btnFriendsTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				PickTeamFragment fragment = PickTeamFragment.newInstance();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		return view;
	}
	
	private class GetRandomMemberTask extends AsyncTask<Void, Void, Void> {
		
	    protected Void doInBackground(Void... params) {
	    	JSONWriter writer = new JSONWriter(getActivity());
	    	writer.createJsonForGetPic(0);
	    	
	    	HttpPostRequest post = new HttpPostRequest(getActivity());
	    	post.createPost(HttpPostRequest.GET_PIC_URL);
	    	post.addJSON(JSONWriter.FILENAME_GET_PIC);
	    	String jsonString = post.sendPostReturnJson();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	member = reader.getMemberObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	caption.setText(member.getCaption());
	    	UrlImageViewHelper.setUrlDrawable(rateMember, member.getImageUrl(), R.drawable.image_placeholder);
	        return;
	    }
	}
	
	private class SendVoteThread extends Thread {
		private int picId;
		private boolean vote;
		
		private SendVoteThread(int picId, boolean vote) {
			this.picId = picId;
			this.vote = vote;
		}
		
		public void run() {
			JSONWriter writer = new JSONWriter(getActivity());
			if (vote) { writer.createJsonForUpVote(picId); }
			else { writer.createJsonForDownVote(picId); }
			
			if (GlobalMethods.isNetworkAvailable(getActivity())) {
				HttpPostRequest post = new HttpPostRequest(getActivity());
				post.createPost(HttpPostRequest.VOTE_URL);
				post.addJSON(JSONWriter.FILENAME_PIC_VOTE);
				post.sendPost();
			}
			else {
				getActivity().runOnUiThread(new Runnable() {
					public void run() { 
						Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	
}
