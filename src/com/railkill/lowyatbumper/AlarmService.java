package com.railkill.lowyatbumper;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends Service implements AsyncResponse {

	public static String TAG = AlarmService.class.getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(this, "LowyatBumper", Context.MODE_PRIVATE);
		String un = prefs.getString("username", null);
		String pw = prefs.getString("password", null);
		String alarmName = intent.getStringExtra("alarmName");
		//String hour = intent.getStringExtra("hour");
		//String minute = intent.getStringExtra("minute");
		ArrayList<String> bumpStrings = intent.getStringArrayListExtra("bumpStrings");
		String notiTitle = "Lowyat: Bump Fail";
		String notiMsg = alarmName + ": Does not exist.";
		
		if (un == null || pw == null) {
			// Missing user details error.
			notiMsg += "Missing login details.";
		}
		else {
			if (bumpStrings == null || bumpStrings.isEmpty()) {
				// Empty bump list error.
				notiMsg += "Cannot retrieve posts.";
			}
			else {
				// Bump scheduled posts.
				try {
					
					LoginHandler bumpRun = new LoginHandler(bumpStrings);
					bumpRun.delegate = this;
			    	bumpRun.execute("https://forum.lowyat.net/index.php?act=Login&CODE=01",
			    			un, pw);
			    	notiTitle = "Lowyat: Bump Sent";
			    	notiMsg = alarmName + " bumped at " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE)) + ".";
				}
				catch (Exception e) {
					e.printStackTrace();
					// Network error.
					notiMsg += "Network error.";
				}
			}
		}
		
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle(notiTitle)
			    .setContentText(notiMsg)
			    .setSound(soundUri)
			    .setAutoCancel(true);
		
		Intent resultIntent = new Intent(this, BumperActivity.class);
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    this,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		// Sets an ID for the notification
		int mNotificationId = 103967;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		AlarmReceiver.setAlarms(getApplicationContext());
		// Set alarms in AlarmReceiver, not here. AlarmService does the work, AlarmReceiver schedules the stuff.

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void processFinish(String[][] output) {
		// Do nothing, I think.		
	}
}
