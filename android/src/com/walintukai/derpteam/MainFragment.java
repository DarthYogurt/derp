package com.walintukai.derpteam;

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
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	ImageView ratePicture;
	
	static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		ratePicture = (ImageView) view.findViewById(R.id.rate_picture);
		new GetRandomPicTask().execute();
		
		ImageView btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
				new GetRandomPicTask().execute();
			}
		});
		
		ImageView btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
				new GetRandomPicTask().execute();
			}
		});
		
		Button btnYourTeam = (Button) view.findViewById(R.id.btn_your_team);
		btnYourTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
//				TakePictureFragment fragment = new TakePictureFragment();
//				ft.replace(R.id.fragment_container, fragment);
//				ft.addToBackStack(null);
//				ft.commit();
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
				
//				TakePictureFragment fragment = new TakePictureFragment();
//				ft.replace(R.id.fragment_container, fragment);
//				ft.addToBackStack(null);
//				ft.commit();
			}
		});
		
		return view;
	}
	
	private class GetRandomPicTask extends AsyncTask<Void, Void, Void> {
		Picture picture;
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getPictureJsonString(0);
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	picture = reader.getPictureObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	UrlImageViewHelper.setUrlDrawable(ratePicture, picture.getImageUrl(), R.drawable.image_placeholder);
	        return;
	    }
	}
	
}
