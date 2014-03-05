package com.walintukai.derpteam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class GetNotificationAlarmReceiver extends BroadcastReceiver{
	
	Notification notification;
	
	@Override
    public void onReceive(Context context, Intent intent) {
        Log.v("testing", "WORKING ALARM");

        // Calling notification server here
        new GetNotificationThread(context).start();        
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
	    	
	    	if (jsonString.equalsIgnoreCase("none")) {
	    		Log.v("NOTIFICATIONS", "NONE");
	    	}
	    	else {
	    		JSONReader reader = new JSONReader(context);
		    	notification = reader.getNotificationObject(jsonString);
		    	sendNotification(context, notification);
	    	}
		}
	}
	
	private void sendNotification(Context context, Notification notification) {
		String firstName = notification.getPosterFbName().substring(0, notification.getPosterFbName().indexOf(" "));
		String content = firstName + " has put someone on your team!";
		
		NotificationCompat.Builder mBuilder =
        		new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.derpteam_logo)
		        .setContentTitle("DerpTeam")
		        .setContentText(content);
        
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

}
