package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

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
	private List<Integer> seenPicturesArray;
	
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
		seenPicturesArray = GlobalMethods.readSeenPicturesArray(getActivity());
		
		caption = (TextView) view.findViewById(R.id.caption);
		rateMember = (ImageView) view.findViewById(R.id.rate_picture);
		new GetRandomMemberTask().execute();
		
		ImageView btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
				seenPicturesArray.add(member.getPicId());
				GlobalMethods.writeSeenPicturesArray(getActivity(), seenPicturesArray);
				new GetRandomMemberTask().execute();
			}
		});
		
		ImageView btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
				seenPicturesArray.add(member.getPicId());
				GlobalMethods.writeSeenPicturesArray(getActivity(), seenPicturesArray);
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
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getMemberJsonString(0);
	    	
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
	
}
