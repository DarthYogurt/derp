package com.walintukai.derpteam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class StartupNotificationBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive (Context context, Intent intent) {
		Log.v("alarmTesting", "SYSTEM BOOTUP RECEIVED");
		
		// Periodically checks for notifications
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent2 = new Intent(context, GetNotificationAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
		int checkTime = 1000 * 30; //(1000 * 60) * (60 + getRandomNumber());
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.uptimeMillis(), checkTime, pendingIntent);
	}


}
