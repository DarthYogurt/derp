package com.walintukai.derpteam;

import java.util.Set;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	private Preferences prefs;
	private ImageView rateMember;
	private TextView caption;
	private Member member;
	private int picId;
	private ImageView btnVoteDown;
	private ImageView btnVoteUp;
	private ImageView btnNextPic;
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
		btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		btnNextPic = (ImageView) view.findViewById(R.id.btn_next_pic);
		Button btnYourTeam = (Button) view.findViewById(R.id.btn_your_team);
		Button btnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		Button btnGallery = (Button) view.findViewById(R.id.btn_gallery);
		Button btnFriendsTeam = (Button) view.findViewById(R.id.btn_friends_team);
		
		rateMember.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ViewMemberFragment fragment = ViewMemberFragment.newInstance(picId);
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnNextPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (GlobalMethods.isNetworkAvailable(getActivity())) { new GetRandomMemberTask().execute(); }
				else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (GlobalMethods.isNetworkAvailable(getActivity())) { 
					Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
					votedPicturesSet.add(member.getPicId());
					GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
					new SendVoteThread(member.getPicId(), false).start();
					new GetRandomMemberTask().execute();
				}
				else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (GlobalMethods.isNetworkAvailable(getActivity())) { 
					Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
					votedPicturesSet.add(member.getPicId());
					GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
					new SendVoteThread(member.getPicId(), true).start();
					new GetRandomMemberTask().execute();
				}
				else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
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
		
		if (GlobalMethods.isNetworkAvailable(getActivity())) { new GetRandomMemberTask().execute(); }
		else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
		
		return view;
	}
	
	private boolean hasAlreadyVoted() {
		return member.getUserVoted().equalsIgnoreCase("true") || member.getUserVoted().equalsIgnoreCase("false");
	}
	
	private class GetRandomMemberTask extends AsyncTask<Void, Void, Void> {
		
		protected void onPreExecute() {
			btnVoteDown.setVisibility(View.GONE);
    		btnVoteUp.setVisibility(View.GONE);
			btnNextPic.setVisibility(View.GONE);
		}
		
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
	    	UrlImageViewHelper.setUrlDrawable(rateMember, member.getImageUrl(), R.drawable.image_placeholder,
	    			new UrlImageViewCallback() {
						@Override
						public void onLoaded(ImageView arg0, Bitmap arg1, String arg2, boolean arg3) {
							Animation popIn = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in);
							rateMember.startAnimation(popIn);
						}
	    	});
	    	picId = member.getPicId();
	    	if (hasAlreadyVoted()) { 
	    		btnNextPic.setVisibility(View.VISIBLE);
    		}
	    	else {
	    		btnVoteDown.setVisibility(View.VISIBLE);
	    		btnVoteUp.setVisibility(View.VISIBLE);
	    	}
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
			
			HttpPostRequest post = new HttpPostRequest(getActivity());
			post.createPost(HttpPostRequest.VOTE_URL);
			post.addJSON(JSONWriter.FILENAME_PIC_VOTE);
			post.sendPost();
		}
	}
	
}
