package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sud on 10/3/17.
 */

public class Alarm {

    /*
        next_alarm = time of next alarm in milis
        repeat= if repeat is true then repeat alarm everyday
        time_overlay= time deviation from gmt in mins

        snoozeTime= snooze time in mins
        */
    long nextAlarm;
    boolean repeat;
    static int timeOverlay;
    int snoozeTime;


    boolean active;

    SharedPreferences saved;

    public Alarm(Context context) {

        saved = context.getSharedPreferences("Alarm", MODE_PRIVATE);
    }

    public void setupAlarm(Context context, long nextAlarm, boolean repeat, int snoozeTime, boolean active) {
        saved = context.getSharedPreferences("Alarm", MODE_PRIVATE);
        setNextAlarm(nextAlarm);
        setRepeat(repeat);
        setSnoozeTime(snoozeTime);
        setActive(active);

    }

    protected void setNextAlarm(long nextAlarm) {
        SharedPreferences.Editor editor = saved.edit();
        this.nextAlarm = nextAlarm;
        editor.putLong("nextAlarm", nextAlarm);
        editor.apply();
    }

    public void setRepeat(boolean repeat) {
        SharedPreferences.Editor editor = saved.edit();
        this.repeat = repeat;
        editor.putBoolean("repeat", repeat);
        editor.apply();
    }

    public static void setTimeOverlay(int timeOverlay) {
        Alarm.timeOverlay = timeOverlay;

    }

    public void setSnoozeTime(int snoozeTime) {
        SharedPreferences.Editor editor = saved.edit();
        this.snoozeTime = snoozeTime;
        editor.putInt("snoozeTime", snoozeTime);
        editor.apply();
    }
    public void setActive(boolean active) {
        this.active = active;
        SharedPreferences.Editor editor = saved.edit();
        editor.putBoolean("active", active);
        editor.apply();
    }
    public boolean isRepeat() {
        repeat = saved.getBoolean("repeat", false);
        return repeat;
    }
    public boolean isActive() {
        active = saved.getBoolean("active", false);
        return active;
    }


    public long getNextAlarm() {
        nextAlarm = saved.getLong("nextAlarm", 0);
        return nextAlarm;
    }


    public int getTimeOverlay() {
        return timeOverlay;
    }

    public int getSnoozeTime() {
        snoozeTime = saved.getInt("snoozeTime", 1);
        return snoozeTime;
    }

    public String getAlarmTime() {
        StringBuilder alarmText;
        nextAlarm = getNextAlarm();
        long time = nextAlarm / 60000;
        time += 330;
        String format;
        int min = (int) time % 60;
        int hour = (int) (time / 60) % 24;
        if (hour == 0)
            format = "A.M.";
        else if (hour == 12)
            format = "P.M.";
        else if (hour > 12) {
            format = "P.M.";
            hour -= 12;
        } else
            format = "A.M.";
        if (min < 10)
            alarmText = new StringBuilder("").append(hour).append(":0").append(min).append(" ").append(format);
        else
            alarmText = new StringBuilder("").append(hour).append(":").append(min).append(" ").append(format);


        return alarmText.toString();

    }


}
