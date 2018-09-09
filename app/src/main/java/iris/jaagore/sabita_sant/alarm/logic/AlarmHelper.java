package iris.jaagore.sabita_sant.alarm.logic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import iris.jaagore.sabita_sant.alarm.AlarmNotification;
import iris.jaagore.sabita_sant.alarm.backend.*;
import iris.jaagore.sabita_sant.alarm.backend.Alarm;
import iris.jaagore.sabita_sant.alarm.utils.Constants;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Sud on 8/18/18.
 */

public class AlarmHelper {
    private final AlarmNotification alarmNotification;
    private Context context;
    private AlarmManager alarmManager;
    private Alarm alarm;
    private AlarmDatabase db;
    PendingIntent pendingIntent;
    private static final String TAG = "AlarmHelper";

    public AlarmHelper(Context context, int alarmID) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmNotification = new AlarmNotification(context);

        //get alarm object
        db = AlarmDatabase.getInstance(context);
        alarm = db.alarmDao().getAlarm(alarmID);

        // pending intent setup
        Intent receiver_intent = new Intent(context, AlarmReceiver.class);
        receiver_intent.putExtra(Constants.ALARM_ID_KEY, alarmID);
        pendingIntent = PendingIntent.getBroadcast(context, alarmID, receiver_intent, PendingIntent.FLAG_CANCEL_CURRENT);

    }

    /*-------------- v2------------------------- */
    public void setAlarm() {
        long alarmTime = alarm.getAlarmTime();
        int waitDays = 0;
        if (alarm.getRepeatCount() > 0) {
            Calendar alarmCalendar = Calendar.getInstance();
            alarmCalendar.setTimeInMillis(alarmTime);
            int alarmDay = alarmCalendar.get(Calendar.DAY_OF_WEEK) - 1; // as Calendar.DAY_OF_WEEK starts from 1

            // check if alarm must play on repeat day or not
            for (int i = 0; i < 7; i++) {
                if (alarm.getRepeatDays()[(alarmDay + i) % 7]) {
                    waitDays = i;
                    break;
                }
            }
            alarmTime += waitDays * Constants.DAY_IN_MILIS;
        }

        // set alarm pending
        setAlarmManager(alarmTime);

        // show notification
        alarmNotification.setPending(alarmTime);


    }

    private void setAlarmManager(long ALARM_TIME) {
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(ALARM_TIME, pendingIntent);
        alarmManager.setAlarmClock(ac, pendingIntent);
        Log.i(TAG, "setAlarmManager: " + ALARM_TIME);
        /*alarmNotification.setPending(ALARM_TIME);
        if (Build.VERSION.SDK_INT >= 23) { // https://stackoverflow.com/questions/34378707/alarm-manager-does-not-work-in-background-on-android-6-0
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ALARM_TIME, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ALARM_TIME, pendingIntent);
        }*/

    }

    public void stopAlarm() {
        // todo check repeat status
        if (alarm.getRepeatCount() > 0) {
            alarm.increaseAlarmTime(Constants.DAY_IN_MILIS);
            setAlarm();
        } else {
            alarm.setActive(false);
            alarmManager.cancel(pendingIntent);
            alarmNotification.cancel();
        }
        db.alarmDao().updateAlarm(alarm);

    }

    public void snoozeAlarm() {
        setAlarmManager(Calendar.getInstance().getTimeInMillis() + alarm.getSnoozeDuration() * 1000);
    }

    public void setStatus(boolean status) {
        if (status)
            setAlarm();
        else
            stopAlarm();
    }
}
