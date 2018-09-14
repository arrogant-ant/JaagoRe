package iris.example.sabita_sant.alarm.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sud on 10/21/17.
 */

public class ConnectivityReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences=context.getSharedPreferences("Alarm",MODE_PRIVATE);
       // quoteStatus=sharedPreferences.getBoolean("isQuoteUsed",true);
        if (isOnline(context)) {
            Log.d("Receiver","internet");
            context.startService(new Intent(context, QuoteService.class));
        }
        else
        {
            Log.d("Receiver","NO internet");
        }

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

}
