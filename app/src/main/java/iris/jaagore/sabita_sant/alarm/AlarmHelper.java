package iris.jaagore.sabita_sant.alarm;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Sud on 10/6/17.
 */

public class AlarmHelper extends Alarm {
    AlarmNotification alarmNotification;
    Context context;
    Long alarmTime;
    AlarmManager alarmManager;
    Intent receiver_intent;
    final long DAY = 86400000;
    final long MILI_SEC=60000;
    PendingIntent pendingIntent;

    public AlarmHelper(Context context) {
        super(context);
        this.context = context;
        alarmNotification = new AlarmNotification(context);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

    }

    private void setAlarmManager(long ALARM_TIME) {
        alarmManager.cancel(pendingIntent);
        receiver_intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, receiver_intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager.AlarmClockInfo ac= new AlarmManager.AlarmClockInfo(ALARM_TIME, pendingIntent);
        alarmManager.setAlarmClock(ac,pendingIntent);
        alarmNotification.setPending(ALARM_TIME);

    }

    public void setAlarm(long nextAlarm, boolean repeat, int snoozeTime, boolean active) {
        saveAlarmState(nextAlarm, repeat, snoozeTime, active);
        alarmTime = nextAlarm;
        setAlarmManager(alarmTime);
        Toast.makeText(context,"Alarm set at : "+getAlarmText(),Toast.LENGTH_SHORT).show();
    }

    public void setRepeatAlarm() {
        alarmTime = getNextAlarm() + DAY;
        setNextAlarm(alarmTime);
        setAlarmManager(alarmTime);
    }

    public void snoozeAlarm()

    {
        Toast.makeText(context,"Alarm snoozed for "+getSnoozeTime()+" mins",Toast.LENGTH_SHORT).show();
        alarmTime = getNextAlarm() + getSnoozeTime()*MILI_SEC;
        setNextAlarm(alarmTime);
        setAlarmManager(alarmTime);
    }

    public void stopAlarm() {

        setActive(false);
        alarmManager.cancel(pendingIntent);
        alarmNotification.cancel();

    }

    public void setPrevious() {
        alarmTime = getNextAlarm();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (alarmTime < currentTime) {
            long time = alarmTime % DAY;
            long current = currentTime % DAY;
            alarmTime = currentTime + DAY - (current - time);
        }
        setNextAlarm(alarmTime);
        setActive(true);
        setAlarmManager(alarmTime);
        Toast.makeText(context,"Alarm set at : "+getAlarmText(),Toast.LENGTH_SHORT).show();
    }
}
