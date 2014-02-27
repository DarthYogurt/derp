package com.walintukai.derpteam;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
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
import android.widget.TextView;

public class ViewPictureFragment extends Fragment {

	private static final String KEY_PIC_ID = "picId";
	
	public Picture picture;
	private int picId;
	private ImageView ivPosterPicture;
	private TextView tvPosterName;
	private ImageView ivDerpPicture;
	private TextView tvTitle;
	private TextView tvCaption;
	
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
		
		ivPosterPicture = (ImageView) view.findViewById(R.id.fb_picture);
		tvPosterName = (TextView) view.findViewById(R.id.fb_name);
		ivDerpPicture = (ImageView) view.findViewById(R.id.derp_picture);
		tvTitle = (TextView) view.findViewById(R.id.title);
		tvCaption = (TextView) view.findViewById(R.id.caption);
		
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
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getPictureJsonString(picId);
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	picture = reader.getPictureObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	UrlImageViewHelper.setUrlDrawable(ivDerpPicture, picture.getImageUrl(), R.drawable.image_placeholder);
	    	tvTitle.setText(picture.getTitle());
	    	tvCaption.setText(picture.getCaption());
			getTargetFbInfo();
	        return;
	    }
	}
	
	private void getTargetFbInfo() {
		String targetFbId = picture.getTargetFbId();
		String graphPath = "/" + targetFbId + "/";
		String graphPathPic = "http://graph.facebook.com/" + targetFbId + "/picture";
		
		new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
			public void onCompleted(Response response) {
				try {
					JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
					String name = jObject.getString("first_name");
					tvPosterName.setText(name);
				} 
				catch (JSONException e) { e.printStackTrace(); }
			}
		}).executeAsync();
		
		UrlImageViewHelper.setUrlDrawable(ivPosterPicture, graphPathPic, R.drawable.image_placeholder);
	}
	
}
