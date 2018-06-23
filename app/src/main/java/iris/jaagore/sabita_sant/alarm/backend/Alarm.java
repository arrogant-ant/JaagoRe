package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

/**
 * Created by Sud on 6/2/18.
 */
@Entity(tableName = "Alarm")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "alarmTime")
    long alarmTime;

    @ColumnInfo(name = "snoozeDuration")
    int snoozeDuration;

    @ColumnInfo(name = "repeatStatus")
    boolean repeatStatus;

    @Nullable
    @ColumnInfo(name = "repeatDays")
    String repeatDays;

    public Alarm( long alarmTime, int snoozeDuration, boolean repeatStatus, @Nullable String repeatDays) {
        this.alarmTime = alarmTime;
        this.snoozeDuration = snoozeDuration;
        this.repeatStatus = repeatStatus;
        this.repeatDays = repeatDays;
    }
}