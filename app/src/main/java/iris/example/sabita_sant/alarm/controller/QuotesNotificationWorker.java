package iris.example.sabita_sant.alarm.controller;

import android.support.annotation.NonNull;

import androidx.work.Worker;
import iris.example.sabita_sant.alarm.utils.AlarmNotification;

/**
 * Created by Sud on 9/30/18.
 */

public class QuotesNotificationWorker extends Worker {
    @NonNull
    @Override
    public Result doWork() {
        // get quote
        // send notification
        new AlarmNotification(getApplicationContext()).showQuotesNotification();

        return Result.SUCCESS;
    }
}
