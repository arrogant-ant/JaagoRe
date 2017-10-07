package com.jaagore.sabita_sant.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

public class AlarmReceiver
        extends BroadcastReceiver {
    static AlarmReceiver ins;
    Context c;

    NotificationManager notificationManager;

    public AlarmReceiver() {
        Log.e("test", "constructor receiver");
        ins = this;
    }

    private void showNotification(Context context, String alert, String title, String msg) {
        Intent arith = new Intent(context, ArithTest.class);
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, arith, PendingIntent.FLAG_UPDATE_CURRENT);
        android.support.v7.app.NotificationCompat.Builder notiBuilder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(alert)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.drawable.icon)
                .addAction(R.drawable.snooze,"Stop",pending_back);//change pending_back to snooze alarm
        notiBuilder.setContentIntent(pending_back);
        notiBuilder.setAutoCancel(true);
        notiBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        notiBuilder.setContentIntent(pending_back);
        notiBuilder.setAutoCancel(true);
        notiBuilder.setDefaults(1);
        this.notificationManager.notify(1, notiBuilder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        c=context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        showNotification(context, "Alarm", "IT'S " + AddAlarm.AlarmText, "tap to stop");
         Intent i=new Intent();
        i.setClass(context, ArithTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtra("snooze",intent.getExtras().getInt("snooze"));
        context.startActivity(i);
        Log.e("start","activity");



    }
}
