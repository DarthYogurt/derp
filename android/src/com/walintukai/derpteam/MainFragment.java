package com.walintukai.derpteam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	
	static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		ImageView btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageView btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageButton btnTakePicture = (ImageButton) view.findViewById(R.id.take_picture);
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				TakePictureFragment fragment = new TakePictureFragment();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		ImageButton btnShowGallery = (ImageButton) view.findViewById(R.id.show_gallery);
		btnShowGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				GalleryFragment fragment = new GalleryFragment();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		return view;
	}
	
}
