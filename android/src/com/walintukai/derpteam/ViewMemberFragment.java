package com.walintukai.derpteam;

import java.util.List;
import java.util.Set;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMemberFragment extends Fragment {

	private static final String KEY_PIC_ID = "picId";
	
	private Preferences prefs;
	private int picId;
	private Member member;
	private ImageView ivTargetFbPic;
	private TextView tvTargetName;
	private ImageView ivDerpPicture;
	private TextView tvTitle;
	private TextView tvCaption;
	private TextView tvUpVote;
	private TextView tvDownVote;
	private LinearLayout voteContainer;
	private PopupWindow pwAddComment;
	private EditText etAddComment;
	private LinearLayout commentContainer;
	private Set<Integer> votedPicturesSet;
	
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
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		Bundle args = getArguments();
		picId = args.getInt(KEY_PIC_ID);
		prefs = new Preferences(getActivity());
		
		ivTargetFbPic = (ImageView) view.findViewById(R.id.fb_picture);
		tvTargetName = (TextView) view.findViewById(R.id.fb_name);
		ivDerpPicture = (ImageView) view.findViewById(R.id.derp_picture);
		tvTitle = (TextView) view.findViewById(R.id.title);
		tvCaption = (TextView) view.findViewById(R.id.caption);
		tvUpVote = (TextView) view.findViewById(R.id.vote_up_count);
		tvDownVote = (TextView) view.findViewById(R.id.vote_down_count);
		ImageView btnVoteDown = (ImageView) view.findViewById(R.id.btn_vote_down);
		ImageView btnVoteUp = (ImageView) view.findViewById(R.id.btn_vote_up);
		voteContainer = (LinearLayout) view.findViewById(R.id.vote_container);
		commentContainer = (LinearLayout) view.findViewById(R.id.comment_container);
		
		pwAddComment = new PopupWindow(getActivity());
		final View vAddComment = inflater.inflate(R.layout.popwin_comment, null, false);
		ImageButton btnAddComment = (ImageButton) view.findViewById(R.id.btn_add_comment);
		etAddComment = (EditText) vAddComment.findViewById(R.id.add_comment);
		Button btnFinishComment = (Button) vAddComment.findViewById(R.id.btn_finish_comment);
		
		votedPicturesSet = GlobalMethods.readVotedPicturesSet(getActivity());
		
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (GlobalMethods.isNetworkAvailable(getActivity())) {
					Toast.makeText(getActivity(), "Voted Down", Toast.LENGTH_SHORT).show();
					votedPicturesSet.add(picId);
					GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
					new SendVoteThread(picId, false).start();
					voteContainer.setVisibility(View.GONE);
				}
				else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (GlobalMethods.isNetworkAvailable(getActivity())) {
					Toast.makeText(getActivity(), "Voted Up", Toast.LENGTH_SHORT).show();
					votedPicturesSet.add(picId);
					GlobalMethods.writeVotedPicturesSet(getActivity(), votedPicturesSet);
					new SendVoteThread(picId, true).start();
					voteContainer.setVisibility(View.GONE);
				}
				else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		btnAddComment.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View view) {
				pwAddComment.setTouchable(true);
				pwAddComment.setFocusable(true);
				pwAddComment.setOutsideTouchable(true);
				pwAddComment.setTouchInterceptor(new OnTouchListener() {
			        public boolean onTouch(View v, MotionEvent event) {
			            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			            	pwAddComment.dismiss();
			                return true;
			            }
			            return false;
			        }
			    });
				pwAddComment.setWidth(550);
				pwAddComment.setHeight(450);
				pwAddComment.setContentView(vAddComment);
				pwAddComment.setBackgroundDrawable(new BitmapDrawable());
				pwAddComment.setAnimationStyle(R.style.AddCommentAnimation);
				pwAddComment.showAtLocation(getActivity().findViewById(R.id.fragment_view_member), Gravity.CENTER, 0, -140);
				pwAddComment.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() { 
						getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				});
				
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		        imm.showSoftInput(etAddComment, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		btnFinishComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String comment = etAddComment.getText().toString();
				if (!comment.isEmpty()) { 
					if (GlobalMethods.isNetworkAvailable(getActivity())) {
						new SendCommentTask(picId, comment).execute(); 
					}
					else { Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show(); }
				}
				else { Toast.makeText(getActivity(), R.string.empty_comment, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		if (GlobalMethods.isNetworkAvailable(getActivity())) { new GetMemberTask().execute(); }
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
	
	private boolean hasAlreadyVoted() {
		return member.getUserVoted().equalsIgnoreCase("true") || member.getUserVoted().equalsIgnoreCase("false");
	}
	
	private class GetMemberTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog loadingDialog;
		private List<Comment> commentsArray;
		
		protected void onPreExecute() {
			loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
			loadingDialog.show();
		}
		
	    protected Void doInBackground(Void... params) {	
	    	JSONWriter writer = new JSONWriter(getActivity());
	    	writer.createJsonForGetPic(picId);
	    	
	    	HttpPostRequest post = new HttpPostRequest(getActivity());
	    	post.createPost(HttpPostRequest.GET_PIC_URL);
	    	post.addJSON(JSONWriter.FILENAME_GET_PIC);
	    	String jsonString = post.sendPostReturnJson();
	    	
	    	JSONReader reader = new JSONReader(getActivity());
	    	member = reader.getMemberObject(jsonString);
	    	commentsArray = reader.getCommentsArray(jsonString);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	UrlImageViewHelper.setUrlDrawable(ivTargetFbPic, member.getTargetFbPicUrl(), R.drawable.image_placeholder);
	    	tvTargetName.setText(member.getTargetFirstName());
	    	UrlImageViewHelper.setUrlDrawable(ivDerpPicture, member.getImageUrl(), R.drawable.image_placeholder);
	    	tvTitle.setText(member.getTitle());
	    	tvCaption.setText(member.getCaption());
	    	tvUpVote.setText(Integer.toString(member.getUpVote()));
	    	tvDownVote.setText(Integer.toString(member.getDownVote()));
	    	if (!hasAlreadyVoted()) { voteContainer.setVisibility(View.VISIBLE); }
	    	
	    	for (int i = 0; i < commentsArray.size(); i++) {
	    		addCommentRow(commentsArray.get(i).getPosterFirstName(), commentsArray.get(i).getComment());
			}
	    	
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
	
	private class SendVoteThread extends Thread {
		private int picId;
		private boolean vote;
		
		private SendVoteThread(int picId, boolean vote) {
			this.picId = picId;
			this.vote = vote;
		}
		
		public void run() {
			JSONWriter writer = new JSONWriter(getActivity());
			if (vote) { writer.createJsonForUpVote(picId); }
			else { writer.createJsonForDownVote(picId); }

			HttpPostRequest post = new HttpPostRequest(getActivity());
			post.createPost(HttpPostRequest.VOTE_URL);
			post.addJSON(JSONWriter.FILENAME_PIC_VOTE);
			post.sendPost();
		}
	}
	
	private class SendCommentTask extends AsyncTask<Void, Void, Void> {
		private int picId;
		private String comment;
		
		private SendCommentTask(int picId, String comment) {
			this.picId = picId;
			this.comment = comment;
		}
		
	    protected Void doInBackground(Void... params) {
	    	JSONWriter writer = new JSONWriter(getActivity());
			writer.createJsonForComment(picId, comment);
			
			getActivity().runOnUiThread(new Runnable() {
				public void run() { 
					Toast.makeText(getActivity(), R.string.comment_added, Toast.LENGTH_SHORT).show();
					addCommentRow(prefs.getFbFirstName(), comment);
				}
			});
			
			HttpPostRequest post = new HttpPostRequest(getActivity());
			post.createPost(HttpPostRequest.COMMENT_URL);
			post.addJSON(JSONWriter.FILENAME_COMMENT);
			post.sendPost();

	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	pwAddComment.dismiss();
	    	etAddComment.setText("");
	        return;
	    }
	}
	
	private void addCommentRow(String firstName, String comment) {
		LinearLayout row = new LinearLayout(getActivity());
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setPadding(0, 0, 0, 10);
		
		TextView tvName = new TextView(getActivity());
		tvName.setText(firstName.toUpperCase());
		tvName.setTextSize(14);
		tvName.setTextColor(Color.parseColor("#30acff"));
		tvName.setPadding(0, 0, 10, 0);
		row.addView(tvName);
		
		TextView tvComment = new TextView(getActivity());
		tvComment.setTextSize(14);
		tvComment.setTextColor(Color.parseColor("#000000"));
		tvComment.setText(comment);
		row.addView(tvComment);
		
		commentContainer.addView(row);
	}
	
}
