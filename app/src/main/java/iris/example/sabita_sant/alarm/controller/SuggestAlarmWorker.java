package iris.example.sabita_sant.alarm.controller;

import android.support.annotation.NonNull;

import java.util.List;

import androidx.work.Worker;
import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.utils.AlarmNotification;

/**
 * Created by Sud on 10/4/18.
 */

public class SuggestAlarmWorker extends Worker {
    @NonNull
    @Override
    public Result doWork() {
        AlarmDatabase db = AlarmDatabase.getInstance(getApplicationContext());
        List<Alarm> alarms = db.alarmDao().getAll();
        for (Alarm alarm:
             alarms) {
            if(alarm.isActive())
                return Result.SUCCESS;
        }
        new AlarmNotification(getApplicationContext()).suggestAlarmNotification();
        return Result.SUCCESS;
    }
}
