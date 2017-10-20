package iris.jaagore.sabita_sant.alarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver
        extends BroadcastReceiver {
    static AlarmReceiver ins;


    NotificationManager notificationManager;

    public AlarmReceiver() {
        Log.e("test", "constructor receiver");
        ins = this;
    }



    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent();
        i.setClass(context, AlarmScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Log.e("start", "activity");


    }
}
