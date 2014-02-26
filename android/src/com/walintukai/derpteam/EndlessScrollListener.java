package com.walintukai.derpteam;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class EndlessScrollListener implements OnScrollListener {
	
	private int visibleThreshold = 2;
	private int currentPage = 1;
	private int previousTotal = 0;
	private boolean loading = true;
	
	public EndlessScrollListener() {
		
	}
	
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
