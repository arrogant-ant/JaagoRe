package iris.example.sabita_sant.alarm.controller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Calendar;

import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.utils.AlarmNotification;
import iris.example.sabita_sant.alarm.utils.Constants;
import iris.example.sabita_sant.alarm.views.Home;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Sud on 8/18/18.
 */

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    private final AlarmNotification alarmNotification;
    private AlarmManager alarmManager;
    private Alarm alarm;
    private AlarmDatabase db;
    private PendingIntent pendingIntent, viewerIntent;
    private Context context;

    public AlarmHelper(Context context, int alarmID) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmNotification = new AlarmNotification(context);

        //get alarm object
        db = AlarmDatabase.getInstance(context);
        alarm = db.alarmDao().getAlarm(alarmID);
        final int flags = alarm == null ? PendingIntent.FLAG_NO_CREATE : 0;

        // pending intent setup
        Intent alarm_view = new Intent(context, Home.class);
        alarm_view.putExtra(Constants.ALARM_ID_KEY, alarmID);
        viewerIntent = PendingIntent.getBroadcast(context, alarmID, alarm_view, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent receiver_intent = new Intent(context, AlarmReceiver.class);
        receiver_intent.putExtra(Constants.ALARM_ID_KEY, alarmID);
        pendingIntent = PendingIntent.getBroadcast(context, alarmID, receiver_intent, flags);

    }

    public void setAlarm() {
        // stop if previous alarm exixts with same id
        //alarmManager.cancel(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the notification policy access has been granted for the app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                requestDND();
            }
        }
        long alarmTime = alarm.getBaseAlarmTime();
        // updating alarm time if its less them current time
        while (alarmTime < Calendar.getInstance().getTimeInMillis())
            alarmTime += Constants.DAY_IN_MILIS;
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
            alarm.setAlarmTime(alarmTime);
            alarm.setBaseAlarmTime(alarmTime);
            alarm.setActive(true);
            db.alarmDao().updateAlarm(alarm);
        }

        // set alarm pending
        setAlarmManager(alarmTime);

        // show notification
        alarmNotification.setPending(alarmTime);


    }

    private void requestDND() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setMessage("Please add Alarm Clock to 'Do Not Disturb'.")
                .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    private void setAlarmManager(long ALARM_TIME) {
        if (ALARM_TIME <= 0 || pendingIntent == null) {
            return;
        }
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(ALARM_TIME, viewerIntent);
        alarmManager.setAlarmClock(ac, pendingIntent);
        Log.i(TAG, "setAlarmManager: " + alarm.toString());
        Log.i(TAG, "setAlarmManager: diff "+(alarm.getAlarmTime()-Calendar.getInstance().getTimeInMillis()));
        /*alarmNotification.setPending(ALARM_TIME);
        if (Build.VERSION.SDK_INT >= 23) { // https://stackoverflow.com/questions/34378707/alarm-manager-does-not-work-in-background-on-android-6-0
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ALARM_TIME, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ALARM_TIME, pendingIntent);
        }*/

    }

    public void stopAlarm() {
        if (alarm.getRepeatCount() > 0) {
            alarm.increaseBaseAlarmTime(Constants.DAY_IN_MILIS);
            setAlarm();
        } else {
            alarmManager.cancel(pendingIntent);
            alarm.setActive(false);
            alarmNotification.cancel();
        }
        db.alarmDao().updateAlarm(alarm);

    }

    public void snoozeAlarm() {
        long snoozeDuration = alarm.getSnoozeDuration() * 60000;
        alarm.setAlarmTime(Calendar.getInstance().getTimeInMillis() + snoozeDuration);
        alarm.setActive(true);
        Log.i(TAG, "snoozeAlarm: " + alarm);
        db.alarmDao().updateAlarm(alarm);
        setAlarmManager(alarm.getAlarmTime());
    }
    void setStatus(boolean status) {
        if (status)
            setAlarm();
        else
            stopAlarm();
    }
}
