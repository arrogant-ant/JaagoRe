package iris.example.sabita_sant.alarm.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import iris.example.sabita_sant.alarm.AlarmScreen;
import iris.example.sabita_sant.alarm.utils.Constants;

import static android.content.Context.POWER_SERVICE;

public class AlarmReceiver
        extends BroadcastReceiver {
    static AlarmReceiver ins;
    private static final String TAG = "AlarmReceiver";


    public AlarmReceiver() {
        ins = this;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        /*wakeLock =
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Jaago re");
            wakeLock.acquire();
        }*/
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        Log.i(TAG, "onReceive: at");
        int id = intent.getIntExtra(Constants.ALARM_ID_KEY, 0);
        if (id == 0) {
            Log.e(TAG, "onReceive: "+Constants.ALARM_ID_KEY+" not present");
            return;
        }
        Intent i = new Intent();
        i.putExtra(Constants.ALARM_ID_KEY,id);
        i.setClass(context, AlarmScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Log.i(TAG, "onReceive: end");
        wakeLock.release();


    }
}
