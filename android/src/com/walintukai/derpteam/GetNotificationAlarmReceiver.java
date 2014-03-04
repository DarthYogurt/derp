package com.walintukai.derpteam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
        
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        int icon = R.drawable.arrow_down;        
        CharSequence tickerText = "Hello"; // ticker-text
        long when = System.currentTimeMillis();         
        CharSequence contentTitle = "Hello";  
        CharSequence contentText = "Hello";      
        
//        Intent notificationIntent = new Intent(this, Example.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        
        Notification notification = new Notification(icon, tickerText, when);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        // and this
        private static final int HELLO_ID = 1;
        mNotificationManager.notify(HELLO_ID, notification);
    }
}
