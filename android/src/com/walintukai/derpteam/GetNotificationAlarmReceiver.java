package com.walintukai.derpteam;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GetNotificationAlarmReceiver extends BroadcastReceiver{
	
	@Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm went off", Toast.LENGTH_LONG).show();
        Log.v("testing", "WORKING ALARM");
        
        
        //Call notification server here
        
        JSONWriter writer = new JSONWriter(getActivity());
    	writer.createJsonForActiveFriends();
    	
    	HttpPostRequest post = new HttpPostRequest(getActivity());
    	post.createPost(HttpPostRequest.GET_PIC_URL);
    	post.addJSON(JSONWriter.FILENAME_GET_PIC);
    	String jsonString = post.sendPostReturnJson();
    	
    	JSONReader reader = new JSONReader(getActivity());
    	member = reader.getMemberObject(jsonString);
    	
    	
    	
    	
        NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.derpteam_logo)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!");
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(0, mBuilder.build());
		
    }
}
