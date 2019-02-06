package iris.example.sabita_sant.alarm.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;

import iris.example.sabita_sant.alarm.utils.AlarmType;
import iris.example.sabita_sant.alarm.utils.Constants;

/**
 * Created by Sud on 6/2/18.
 */
@Entity(tableName = "Alarm")
public class Alarm {
    @ColumnInfo(name = "alarmTime")
    long alarmTime;
    @ColumnInfo(name = "baseAlarmTime")
    long baseAlarmTime;
    @ColumnInfo(name = "snoozeDuration")
    int snoozeDuration;
    @ColumnInfo(name = "repeatCount")
    int repeatCount;
    @Nullable
    @ColumnInfo(name = "repeatDays")
    boolean[] repeatDays;
    @ColumnInfo(name = "active")
    boolean active;
    @Nullable
    @ColumnInfo(name = "toneURI")
    String toneURI;
    @ColumnInfo(name = "type")
    AlarmType type;
    @ColumnInfo(name = "label")
    String label;
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    public Alarm(long alarmTime, int snoozeDuration, int repeatCount, @Nullable boolean[] repeatDays, boolean active, @Nullable String toneURI, AlarmType type, String label) {
        this.alarmTime = alarmTime;
        this.baseAlarmTime = alarmTime;
        this.snoozeDuration = snoozeDuration;
        this.repeatCount = repeatCount;
        this.repeatDays = repeatDays;
        this.active = active;
        this.toneURI = toneURI;
        this.type = type;
        this.label = label;
        this.id = generateID();
    }


    private int generateID() {
        int time = (int) ((baseAlarmTime % Constants.DAY_IN_MILIS) / 60000);
        // last digit represents repeat active
        int id = time * 10;
        if (repeatCount > 0)
            id++;   // 1 for repeat = true
        return id;
    }

    public void increaseAlarmTime(long time) {
        this.alarmTime += time;
    }

    public void increaseBaseAlarmTime(long time) {
        baseAlarmTime += time;
        alarmTime = baseAlarmTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public long getBaseAlarmTime() {
        return baseAlarmTime;
    }

    public void setBaseAlarmTime(long baseAlarmTime) {
        this.baseAlarmTime = baseAlarmTime;
    }

    public int getSnoozeDuration() {
        return snoozeDuration;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Nullable
    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(@Nullable boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Nullable
    public String getToneURI() {
        return toneURI;
    }

    public void setToneURI(@Nullable String toneURI) {
        this.toneURI = toneURI;
    }

    public AlarmType getType() {
        return type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        SimpleDateFormat tf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        return "Alarm{" +
                "id=" + id +
                ", alarmTime=" + tf.format(alarmTime) +
                '}';
    }
}