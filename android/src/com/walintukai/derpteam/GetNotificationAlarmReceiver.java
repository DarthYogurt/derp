package com.walintukai.derpteam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GetNotificationAlarmReceiver extends BroadcastReceiver{
	
	@Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm went off", Toast.LENGTH_LONG).show();
        Log.v("testing", "WORKING ALARM");
        
        
        //Call notification server here
        
        
    }
}
