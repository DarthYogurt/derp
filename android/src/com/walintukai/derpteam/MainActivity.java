package com.walintukai.derpteam;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		
		ImageView btnVoteDown = (ImageView) findViewById(R.id.btn_vote_down);
		btnVoteDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(MainActivity.this, "Voted Down", Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageView btnVoteUp = (ImageView) findViewById(R.id.btn_vote_up);
		btnVoteUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(MainActivity.this, "Voted Up", Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageButton btnTakePicture = (ImageButton) findViewById(R.id.take_picture);
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton btnShowGallery = (ImageButton) findViewById(R.id.show_gallery);
		btnShowGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
