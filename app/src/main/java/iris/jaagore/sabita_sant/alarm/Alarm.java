package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;


import java.text.SimpleDateFormat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sud on 10/3/17.
 */

public class Alarm {

    /*
        next_alarm = time of next alarm in milis
        repeat= if repeat is true then repeat alarm everyday
        snoozeTime= snooze time in mins
        */
    long nextAlarm;
    boolean repeat;
    int snoozeTime;


    boolean active;

    SharedPreferences saved;

    public Alarm(Context context) {

        saved = context.getSharedPreferences("Alarm", MODE_PRIVATE);
    }

    public void saveAlarmState(long nextAlarm, boolean repeat, int snoozeTime, boolean active) {
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


    public int getSnoozeTime() {
        snoozeTime = saved.getInt("snoozeTime", 1);
        return snoozeTime;
    }

    public String getAlarmText() {
        nextAlarm = getNextAlarm();
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm aaa");
        String time= timeFormat.format(nextAlarm);
        return time;

    }


}
