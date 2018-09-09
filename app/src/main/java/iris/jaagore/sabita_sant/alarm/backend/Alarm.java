package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import iris.jaagore.sabita_sant.alarm.utils.AlarmType;
import iris.jaagore.sabita_sant.alarm.utils.Constants;

/**
 * Created by Sud on 6/2/18.
 */
@Entity(tableName = "Alarm")
public class Alarm {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "alarmTime")
    long alarmTime;

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

    public Alarm(long alarmTime, int snoozeDuration, int repeatCount, @Nullable boolean[] repeatDays, boolean active, @Nullable String toneURI, AlarmType type, String label) {
        this.alarmTime = alarmTime;
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
        int time = (int) ((alarmTime % Constants.DAY_IN_MILIS) / 1000);
        // last digit represents repeat active
        int id = time * 10;
        if (repeatCount > 0)
            id++;   // 1 for repeat = true
        return id;
    }

    public void increaseAlarmTime(long time) {
        this.alarmTime += time;
    }

    public int getId() {
        return id;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public int getSnoozeDuration() {
        return snoozeDuration;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    @Nullable
    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean isActive() {
        return active;
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

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSnoozeDuration(int snoozeDuration) {
        this.snoozeDuration = snoozeDuration;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setRepeatDays(@Nullable boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(int id) {
        this.id = id;
    }
}