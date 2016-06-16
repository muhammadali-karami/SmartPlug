package com.muhammadalikarami.smartplug.utility;

import java.util.Calendar;

import android.content.Context;
import android.widget.Toast;

import com.muhammadalikarami.smartplug.R;
import com.muhammadalikarami.smartplug.Utility;

public class TimeUtility {

	private static int diffHour, diffMin;

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// get diff time for set alarm
	public static int getDiffTimeForSetAlarm(int alarmHour, int alarmMin) {

		Calendar calendar = Calendar.getInstance();
		int nowDay = calendar.get(Calendar.DAY_OF_YEAR);// for repeat alarm! (version 2)
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		int nowMin = calendar.get(Calendar.MINUTE);
		int nowSec = calendar.get(Calendar.SECOND);
		
		// * * * * * * * * * * * * * * * * * * * * * * * * *
		if (alarmHour > nowHour) {			
			if (alarmMin > nowMin) {
				diffHour = alarmHour - nowHour;
				diffMin = alarmMin - nowMin;
			}
			else if (alarmMin == nowMin) {
				diffHour = alarmHour - nowHour;
				diffMin = 0;
			}
			else {
				diffHour = alarmHour - nowHour - 1;
				diffMin = 60 - (nowMin - alarmMin);
			}
		}
		// * * * * * * * * * * * * * * * * * * * * * * * * *		
		else if (alarmHour == nowHour) {
			if (alarmMin > nowMin) {
				diffHour = 0;
				diffMin = alarmMin - nowMin;
			}
			else {
				diffHour = 23;
				diffMin = 60 - (nowMin - alarmMin);
			}		
		}
		// * * * * * * * * * * * * * * * * * * * * * * * * *
		else {			
			if (alarmMin > nowMin) {
				diffHour = 24 - (nowHour - alarmHour);
				diffMin = alarmMin - nowMin;
			}
			else if (alarmMin == nowMin) {
				diffHour = 24 - (nowHour - alarmHour);
				diffMin = 0;
			}
			else {
				diffHour = 24 - (nowHour - alarmHour) - 1;
				diffMin = 60 - (nowMin - alarmMin);
			}
		}
		
		int diffTime = (diffHour * 3600) + (diffMin * 60) - (nowSec);
		return diffTime;

	}
	
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// show remaining time	
	public static void showRemainingTime(){
		
		String remainingTime = null;
		Context context = Utility.getContext();

		if (diffHour == 0 && diffMin == 1) {
			remainingTime = context.getString(R.string.time_utility_less_than_one_minute);
		}
		else if (diffHour == 0) {
			diffMin -= 1;
			if (diffMin > 1)
				remainingTime = context.getString(R.string.time_utility_about) + " " + diffMin + " " + context.getString(R.string.time_utility_minutes);
			else
				remainingTime = context.getString(R.string.time_utility_about) + " " + diffMin + " " + context.getString(R.string.time_utility_minute);
		}
		else if (diffHour > 0 && diffMin == 0) {
			if (diffHour > 1)
				remainingTime = context.getString(R.string.time_utility_about) + " " + diffHour + " " + context.getString(R.string.time_utility_hours);
			else
				remainingTime = context.getString(R.string.time_utility_about) + " " + diffHour + " " + context.getString(R.string.time_utility_hour);
		}
		else {
			remainingTime = context.getString(R.string.time_utility_about) + " " + diffHour + " ";
			if (diffHour > 1)
				remainingTime += context.getString(R.string.time_utility_hours);
			else
				remainingTime += context.getString(R.string.time_utility_hour);

			diffMin -= 1;
			if (diffMin > 0) {
				remainingTime += " " + context.getString(R.string.time_utility_and) + " " + diffMin + " ";

				if (diffMin > 1)
					remainingTime += context.getString(R.string.time_utility_minutes);
				else
					remainingTime += context.getString(R.string.time_utility_minute);
			}
		}

		remainingTime += " " + context.getString(R.string.time_utility_remaining);
		Toast.makeText(Utility.getContext(), remainingTime, Toast.LENGTH_LONG).show();
	}	
	
}
