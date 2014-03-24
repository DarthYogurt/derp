package com.walintukai.derpteam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LeaderboardFragment extends Fragment {
	
	static LeaderboardFragment newInstance() {
		LeaderboardFragment fragment = new LeaderboardFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
		return view;
	}
	
}
