package iris.jaagore.sabita_sant.alarm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    private final static String APP_PACKAGE="iris.example.sabita_sant.alarm";
    private TextView app,firm;
    final long delay=2000;//2 sec delay
    Timer timeout;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app= (TextView) findViewById(R.id.sp_title);
        firm= (TextView) findViewById(R.id.firm);
        MobileAds.initialize(this, String.valueOf(R.string.banner_ad));
        adView= (AdView) findViewById(R.id.banner_ad);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        Typeface heading=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        app.setTypeface(heading);
        firm.setTypeface(heading);
        timeout=new Timer();

        timeout.schedule(timeout_task,delay);
        Log.e("Splash","start");
        Thread background = new Thread() {
            public void run() {

                try {

                    Log.e("Splash","after sleep");
                    if(ConnectivityReceiver.isOnline(Splash.this))
                        checkUpdate();

                    else
                    {
                        // Thread will sleep for 1 seconds
                        sleep(1000);
                        Intent i=new Intent(getBaseContext(),AddAlarm.class);
                        startActivity(i);

                        //Remove activity
                        finish();

                    }


                } catch (Exception e) {

                }
                timeout.cancel();
            }
        };

        // start thread
        background.start();

    }

    TimerTask timeout_task=new TimerTask() {
        @Override
        public void run() {
            Intent i=new Intent(getBaseContext(),AddAlarm.class);
            startActivity(i);

            //Remove activity
            finish();
        }
    };

    private void checkUpdate() {
        String version_url="http://techdrona.net/jaagore/version.php";
        try {
            URL url=new URL(version_url);
            HttpURLConnection connection;
            String response;
            BufferedReader reader;
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response=reader.readLine();
            Log.e("Splash","before parsing response : "+response);
            parseVersion(response);
            Log.e("Splash","after parsing");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void parseVersion(String response) {
        try {
            JSONObject version=new JSONObject(response);
            int current_version= BuildConfig.VERSION_CODE;
            int min_version=version.getInt("min");
            final boolean cancelable=version.getBoolean("cancelable");
            Log.e("Splash","min = "+min_version+" curr = "+current_version);

            if(min_version>current_version)
            {
                Log.e("Splash","true");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDialog(cancelable);
                    }
                });

            }
            else
            {
                Intent i=new Intent(getBaseContext(),AddAlarm.class);
                startActivity(i);

                //Remove activity
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateDialog(boolean cancelable) {
        Log.e("Splash","update dialog, cancelable= "+cancelable);
        Dialog dialog=new Dialog(Splash.this);
        
        AlertDialog.Builder builder=new AlertDialog.Builder(Splash.this);
        Log.e("Splash","dialog builder");
        builder.setTitle("Update")
                .setMessage("'Jaago re' has transformed to better")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+APP_PACKAGE)));
                    }
                })
        .setCancelable((cancelable))
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Intent i=new Intent(getBaseContext(),AddAlarm.class);
                startActivity(i);
                //Remove activity
                finish();

            }
        });
        dialog=builder.create();
        dialog.show();
        Log.e("Splash","update dialog end");

    }
}
