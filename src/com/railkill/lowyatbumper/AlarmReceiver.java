package com.railkill.lowyatbumper;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		setAlarms(context);
		// The onReceive is supposedly to set the alarm again on boot. The bumping is handled by AlarmService.
	}
	
	public static void setAlarms(Context context) {
		ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(context, "LowyatBumper", Context.MODE_PRIVATE);
		int size = prefs.getInt("scheduledTasks", 0);
		if (size > 0) {
			cancelAlarms(context); // Remove all alarms and recreate them.
			for (int j = 0; j < size; j++) {
				String alarm = prefs.getString("alarm" + j, null);
				if (alarm != null) {
					PendingIntent pIntent = createPendingIntent(context, j, alarm);
					
					String[] parts = alarm.split(";");
					Calendar calendar = Calendar.getInstance();
					int alarmHour = Integer.parseInt(parts[1]);
					int alarmMin = Integer.parseInt(parts[2]);
					int dayNumber = Integer.parseInt(parts[3]);
					
					calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
					calendar.set(Calendar.MINUTE, alarmMin);
					calendar.set(Calendar.SECOND, 00);
					//calendar.set(Calendar.MILLISECOND, 00);
					
					// If its set to a specific day, then extra things need to be done.
					if (dayNumber != 0) {
						// It's a specific day! Get the current day's integer value.
						int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
						int diff = dayNumber - nowDay;
						if (diff < 0) {
							// If difference is negative, it has already passed this week.
							// So we schedule for that day on the next week by adding 7 days.
							diff += 7;
						}
						else if (diff == 0) {
							// If difference is 0, means it's today, then we check if time has already passed.
							if (isTimePassed(alarmHour, alarmMin)) {
								calendar.add(Calendar.DAY_OF_YEAR, 1);
							}
						}
						// If difference is not negative, means it's in future..
						calendar.add(Calendar.DAY_OF_YEAR, diff);
						
						
					}
					else {
						// It's EVERYDAY!
						// Check if time SET has already passed. If yes, then add one day to the alarm.
						if (isTimePassed(alarmHour, alarmMin)) {
							calendar.add(Calendar.DAY_OF_YEAR, 1);
						}
					}					
					
					setAlarm(context, calendar, pIntent);
				}
			}
		}
	}
 
	public static void cancelAlarms(Context context) {
		ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(context, "LowyatBumper", Context.MODE_PRIVATE);
		int size = prefs.getInt("scheduledTasks", 0);
		if (size > 0) {
			for (int j = 0; j < size; j++) {
				String alarm = prefs.getString("alarm" + j, null);
				if (alarm != null) {
					PendingIntent pIntent = createPendingIntent(context, j, alarm);

					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					try {
						alarmManager.cancel(pIntent);
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					
				}
			}
		}
	}

	private static PendingIntent createPendingIntent(Context context, int id, String bumpString) {
		String[] parts = bumpString.split(";");
		
		Intent intent = new Intent(context, AlarmService.class);
		intent.setPackage("com.railkill.lowyatbumper");
		intent.putExtra("id", id);
		intent.putExtra("alarmName", parts[0]);
		intent.putExtra("hour", parts[1]);
		intent.putExtra("minute", parts[2]);
		
		
		// Get remaining strings, they are the forum bump links.
		ArrayList<String> bumpStringArray = new ArrayList<String>();
		for (int i = 4; i < parts.length; i++) {
			bumpStringArray.add(parts[i]);
		}
		intent.putStringArrayListExtra("bumpStrings", (ArrayList<String>) bumpStringArray);

		return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@SuppressLint("NewApi") private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
		}
	}
	
	private static boolean isTimePassed(int h, int m) {
		int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (nowHour > h) {
			// First check if current hour is GREATER than SET hour. IF yes, then add one day.
			return true;
		}
		else if (nowHour == h){
			// If not, check if the hour is the same. If yes, compare minutes. If not, do nothing.
			int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
			if (nowMinute >= m) {
				// If current minute is GREATER than SET minute, then add one day.
				return true;
			}
		}
		return false;
	}
	
}
