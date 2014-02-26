package com.walintukai.derpteam;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryFragment extends Fragment {
	
	static GalleryFragment newInstance() {
		GalleryFragment fragment = new GalleryFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		GridView gridView = (GridView) view.findViewById(R.id.gridview);
		gridView.setAdapter(new GalleryAdapter(getActivity()));
		gridView.setOnScrollListener(new EndlessScrollListener());

	    gridView.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    		Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    new GetGalleryThread().start();
		
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
	
	private class GetGalleryThread extends Thread {
		public void run() {
			HttpGetRequest get = new HttpGetRequest();
		    String json = get.getGalleryJsonString(6, 1);
		    Log.v("GALLERY JSON", json);
		}
	}

}
