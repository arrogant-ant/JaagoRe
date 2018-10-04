package iris.example.sabita_sant.alarm.controller;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import iris.example.sabita_sant.alarm.utils.Constants;

/**
 * Created by Sud on 10/4/18.
 */

public class SuggestAlarmHelper extends Worker {
    @NonNull
    @Override
    public Result doWork() {
        PeriodicWorkRequest.Builder suggestAlarmBuilder =
                new PeriodicWorkRequest.Builder(SuggestAlarmWorker.class, 1,
                        TimeUnit.DAYS);
// Create the actual work object:
        PeriodicWorkRequest suggestAlarm = suggestAlarmBuilder.build();
// Then enqueue the recurring task:
        WorkManager.getInstance().enqueueUniquePeriodicWork(Constants.SUGGEST_ALARM_WORER, ExistingPeriodicWorkPolicy.REPLACE, suggestAlarm);
        return Result.SUCCESS;
    }
}
