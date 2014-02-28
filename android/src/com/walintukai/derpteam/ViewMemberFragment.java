package com.walintukai.derpteam;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewMemberFragment extends Fragment {

	private static final String KEY_PIC_ID = "picId";
	
	private int picId;
	private ImageView ivTargetFbPic;
	private TextView tvTargetName;
	private ImageView ivDerpPicture;
	private TextView tvTitle;
	private TextView tvCaption;
	
	static ViewMemberFragment newInstance(int picId) {
		ViewMemberFragment fragment = new ViewMemberFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_PIC_ID, picId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_member, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ivTargetFbPic = (ImageView) view.findViewById(R.id.fb_picture);
		tvTargetName = (TextView) view.findViewById(R.id.fb_name);
		ivDerpPicture = (ImageView) view.findViewById(R.id.derp_picture);
		tvTitle = (TextView) view.findViewById(R.id.title);
		tvCaption = (TextView) view.findViewById(R.id.caption);
		
		Bundle args = getArguments();
		picId = args.getInt(KEY_PIC_ID);
		
		new GetMemberTask().execute();
		
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
	
	private class GetMemberTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog loadingDialog;
		private Member member;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected Void doInBackground(Void... params) {
	    	HttpGetRequest get = new HttpGetRequest();
	    	String jsonString = get.getMemberJsonString(picId);
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	member = reader.getMemberObject(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	String graphPathPic = "http://graph.facebook.com/" + member.getTargetFbId() + "/picture";
	    	UrlImageViewHelper.setUrlDrawable(ivTargetFbPic, graphPathPic, R.drawable.image_placeholder);
	    	getTargetFbName(member.getTargetFbId());
	    	UrlImageViewHelper.setUrlDrawable(ivDerpPicture, member.getImageUrl(), R.drawable.image_placeholder);
	    	tvTitle.setText(member.getTitle());
	    	tvCaption.setText(member.getCaption());
	    	
	    	ivTargetFbPic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					FragmentManager fm = getActivity().getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					
					ViewTeamFragment fragment = ViewTeamFragment.newInstance(member.getTargetFbId());
					ft.replace(R.id.fragment_container, fragment);
					ft.addToBackStack(null);
					ft.commit();
				}
			});
	    	loadingDialog.hide();
	        return;
	    }
	}
	
	private void getTargetFbName(String targetFbId) {
		String graphPath = "/" + targetFbId + "/";
		new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
			public void onCompleted(Response response) {
				try {
					JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
					String name = jObject.getString("first_name");
					tvTargetName.setText(name);
				} 
				catch (JSONException e) { e.printStackTrace(); }
			}
		}).executeAsync();
	}
	
}
