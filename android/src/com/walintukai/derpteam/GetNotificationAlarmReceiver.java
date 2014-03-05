package com.walintukai.derpteam;

import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class GetNotificationAlarmReceiver extends BroadcastReceiver{
	
	@Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm went off", Toast.LENGTH_LONG).show();
        Log.v("testing", "WORKING ALARM");

        // Calling notification server here
        new GetNotificationThread(context).start();

        NotificationCompat.Builder mBuilder =
        		new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.derpteam_logo)
		        .setContentTitle("DerpTeam")
		        .setContentText("You have a new team member!");
        
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("viewYourTeam", true);
        
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(0, mBuilder.build());
    }
	
	private class GetNotificationThread extends Thread {
		
		private Context context;
		
		private GetNotificationThread(Context context) {
			this.context = context;
		}
		
		public void run() {
			JSONWriter writer = new JSONWriter(context);
	    	writer.createJsonForGetNotification();
	    	
	    	HttpPostRequest post = new HttpPostRequest(context);
	    	post.createPost(HttpPostRequest.GET_NOTIFICATION_URL);
	    	post.addJSON(JSONWriter.FILENAME_GET_NOTIFICATION);
	    	String jsonString = post.sendPostReturnJson();
	    	
	    	Log.v("NOTIFICATION", jsonString);
	    	
//	    	JSONReader reader = new JSONReader(getActivity());
//	    	member = reader.getMemberObject(jsonString);
		}
	}

}
