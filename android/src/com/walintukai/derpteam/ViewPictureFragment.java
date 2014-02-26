package com.walintukai.derpteam;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPictureFragment extends Fragment {

	private static final String KEY_PIC_ID = "picId";
	
	private int picId;
	private ImageView pictureView;
	
	static ViewPictureFragment newInstance(int picId) {
		ViewPictureFragment fragment = new ViewPictureFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_PIC_ID, picId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_picture, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		pictureView = (ImageView) view.findViewById(R.id.derp_picture);
		
		Bundle args = getArguments();
		picId = args.getInt(KEY_PIC_ID);
		
		new GetPictureTask().execute();
		
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
	
	private class GetPictureTask extends AsyncTask<Void, Void, Void> {
		Picture picture;
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getPictureJsonString(picId);
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	picture = reader.getPictureObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	UrlImageViewHelper.setUrlDrawable(pictureView, picture.getImageUrl(), R.drawable.image_placeholder);
	        return;
	    }
	}
	
}
