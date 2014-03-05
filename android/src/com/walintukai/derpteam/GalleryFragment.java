package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryFragment extends Fragment {
	
	private GridView gridView;
	private GalleryAdapter adapter;
	private List<Member> membersArray;
	private int totalPages;
	
	static GalleryFragment newInstance() {
		GalleryFragment fragment = new GalleryFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		membersArray = new ArrayList<Member>();
		
		gridView = (GridView) view.findViewById(R.id.gridview);
		
		adapter = new GalleryAdapter(getActivity(), R.layout.gridview_image, membersArray);
		gridView.setAdapter(adapter);
		gridView.setOnScrollListener(new EndlessScrollListener());
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    		FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ViewMemberFragment fragment = ViewMemberFragment.newInstance(membersArray.get(position).getPicId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
	        }
	    });
	    
	    if (GlobalMethods.isNetworkAvailable(getActivity())) { new GetMembersTask(1).execute(); }
	    else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
		
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
	
	private class GetMembersTask extends AsyncTask<Void, Void, List<Member>> {
		private int pageNumber;
		
		private GetMembersTask(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		
	    protected List<Member> doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	JSONReader reader = new JSONReader(getActivity());
		    String jsonString = get.getGalleryJsonString(10, pageNumber);
		    totalPages = reader.getGalleryTotalPages(jsonString);
		    
		    if (!membersArray.isEmpty()) {
		    	List<Member> newMembers = reader.getMembersArray(jsonString);
		    	membersArray.addAll(newMembers);
		    }
		    else {
		    	membersArray = reader.getMembersArray(jsonString);
		    }
	        return membersArray;
	    }

	    protected void onPostExecute(List<Member> result) {
	    	super.onPostExecute(result);
	    	adapter.refreshList(result);
	        return;
	    }
	}
	
	public class EndlessScrollListener implements OnScrollListener {
		private int visibleThreshold = 1;
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
				if (currentPage <= totalPages) {
					loading = true;
					Log.i("LOADING", "PAGE " + Integer.toString(currentPage));
					new GetMembersTask(currentPage).execute();
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

}
