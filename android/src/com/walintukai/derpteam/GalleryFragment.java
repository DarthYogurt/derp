package com.walintukai.derpteam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryFragment extends Fragment {
	
	private GridView gridView;
	private List<Picture> picturesArray;
	
	static GalleryFragment newInstance() {
		GalleryFragment fragment = new GalleryFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		picturesArray = new ArrayList<Picture>();
		
		gridView = (GridView) view.findViewById(R.id.gridview);
		
		GalleryAdapter adapter = new GalleryAdapter(getActivity(), R.layout.gridview_image, picturesArray);
		gridView.setAdapter(adapter);
		
		gridView.setOnScrollListener(new EndlessScrollListener());

	    gridView.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    		Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    new GetPicturesTask(1).execute();
		
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
	
	private class GetPicturesTask extends AsyncTask<Void, Void, Void> {
		private int pageNumber;
		
		private GetPicturesTask(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
		    String jsonString = get.getGalleryJsonString(8, pageNumber);
		    
		    JSONReader reader = new JSONReader(getActivity());
		    picturesArray = reader.getPicturesArray(jsonString);
		    
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	// TODO: REFRESH LIST, NOTIFY DATA SET CHANGED
	    	GalleryAdapter adapter = new GalleryAdapter(getActivity(), R.layout.gridview_image, picturesArray);
			gridView.setAdapter(adapter);
	        return;
	    }
	}
	
	public class EndlessScrollListener implements OnScrollListener {
		private int visibleThreshold = 2;
		private int currentPage = 1;
		private int previousTotal = 0;
		private boolean loading = true;
		
		public EndlessScrollListener() { }
		
		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					currentPage++;
				}
			}
			if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				// execute loading of new items here: new LoadPicsTask().execute(currentPage + 1);
				Log.v("LOADING", "PAGE " + Integer.toString(currentPage));
				loading = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

}
