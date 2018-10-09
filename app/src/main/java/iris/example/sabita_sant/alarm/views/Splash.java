package iris.example.sabita_sant.alarm.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import iris.example.sabita_sant.alarm.BuildConfig;
import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.controller.ConnectivityReceiver;
import iris.example.sabita_sant.alarm.controller.QuoteService;
import iris.example.sabita_sant.alarm.controller.QuotesNotificationWorker;
import iris.example.sabita_sant.alarm.controller.SuggestAlarmHelper;
import iris.example.sabita_sant.alarm.utils.Constants;
import iris.example.sabita_sant.alarm.utils.Message;

public class Splash extends AppCompatActivity {
    private final static String APP_PACKAGE = "iris.example.sabita_sant.alarm";
    private TextView app, firm;
    private View parent;
    Timer timeout;
    private static final String TAG = "Splash";
    private FirebaseDatabase database;
    private DatabaseReference dbRef, versionRef, cancelableRef;
    private TimerTask timeout_task;
    private int PERMISSION_REQUEST_CODE = 50;
    private int IGNORE_OPTIMIZATION_REQUEST = 51;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        parent = findViewById(R.id.parent);
        app = findViewById(R.id.sp_title);
        firm = findViewById(R.id.firm);
        Typeface heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        app.setTypeface(heading);
        firm.setTypeface(heading);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Update");
        cancelableRef = dbRef.child("cancelable");
        versionRef = dbRef.child("minVersion");
        startService(new Intent(Splash.this, QuoteService.class));
        startNotificationWoker();
        timeout = new Timer();
        final long delay = 1500;
        Thread background = new Thread() {
            public void run() {
                try {
                    Log.e("Splash", "after sleep");
                    if (ConnectivityReceiver.isOnline(Splash.this)) {
                        versionRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot == null)
                                    return;
                                int minVersion = dataSnapshot.getValue(Integer.class);
                                Log.i(TAG, "onDataChange: " + minVersion);
                                int current_version = BuildConfig.VERSION_CODE;
                                Log.i(TAG, "onDataChange: " + current_version + minVersion);
                                if (minVersion > current_version) {
                                    timeout.cancel();
                                    suggestUpdate();
                                }/* else {
                                    Intent i = new Intent(getBaseContext(), Home.class);
                                    startActivity(i);
                                    finish();
                                }*/
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i(TAG, "onCancelled: " + databaseError);

                            }
                        });
                    }
                } catch (Exception e) {

                }
                //timeout.cancel();
            }
        };

        // start thread
        background.start();
        timeout_task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(getBaseContext(), Home.class);
                startActivity(i);
                //Remove activity
                finish();
            }
        };
        timeout.schedule(timeout_task, delay);
        getPermissions();

    }

    private void suggestUpdate() {
        cancelableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    return;
                boolean cancelable = dataSnapshot.getValue(Boolean.class);
                showUpdateDialog(cancelable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDialog(boolean cancelable) {
        Log.e("Splash", "update dialog, cancelable= " + cancelable);
        Dialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
        Log.e("Splash", "dialog builder");
        builder.setTitle("Update")
                .setMessage("'Jaago re' has transformed to better")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE)));
                    }
                })
                .setCancelable((cancelable))
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Intent i = new Intent(getBaseContext(), Home.class);
                        startActivity(i);
                        //Remove activity
                        finish();

                    }
                });
        dialog = builder.create();
        dialog.show();
        Log.e("Splash", "update dialog end");

    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat
                    .checkSelfPermission(this, android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {

                //Should we show an explanation
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
                    //Show an explanation
                    final String message = "";
                    Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                            .setAction("GRANT", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Splash.this, new String[]{android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, PERMISSION_REQUEST_CODE);
                                }
                            })
                            .show();

                } else {
                    //No explanation need,we can request the permission
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, PERMISSION_REQUEST_CODE);
                }
            }
            // WAKE LOCK permission
            int wakeLockPermissionCheck = ContextCompat
                    .checkSelfPermission(this, Manifest.permission.WAKE_LOCK);

            if (wakeLockPermissionCheck == PackageManager.PERMISSION_DENIED) {

                //Should we show an explanation
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WAKE_LOCK)) {
                    //Show an explanation
                    final String message = "";
                    Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                            .setAction("GRANT", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.WAKE_LOCK}, PERMISSION_REQUEST_CODE);
                                }
                            })
                            .show();

                } else {
                    //No explanation need,we can request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, PERMISSION_REQUEST_CODE);
                    Log.i(TAG, "getPermissions: inside else " + ContextCompat
                            .checkSelfPermission(this, Manifest.permission.WAKE_LOCK));
                }
                Log.i(TAG, "getPermissions: outside else " + ContextCompat
                        .checkSelfPermission(this, Manifest.permission.WAKE_LOCK));
            }
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if (!isIgnoringBatteryOptimizations) {
                timeout_task.cancel();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, IGNORE_OPTIMIZATION_REQUEST);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode);
        if (requestCode == IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            Log.i(TAG, "onActivityResult: isIgnoringBatteryOptimizations" + isIgnoringBatteryOptimizations + " pkg " + getPackageName());
            if (isIgnoringBatteryOptimizations) {
                // Ignoring battery optimization
                Intent i = new Intent(getBaseContext(), Home.class);
                startActivity(i);
                //Remove activity
                finish();
            } else {
                // Not ignoring battery optimization
                Message.showSnackbar(this, parent, "Without Battery permission Jaago Re may not perform as expected");
                Intent i = new Intent(getBaseContext(), Home.class);
                startActivity(i);
                //Remove activity
                finish();
            }
        }
    }

    private void startNotificationWoker() {
        PeriodicWorkRequest.Builder quotesNotiBuilder =
                new PeriodicWorkRequest.Builder(QuotesNotificationWorker.class, 12,
                        TimeUnit.HOURS);
// ...if you want, you can apply constraints to the builder here...
        Constraints quotesConstraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
// Create the actual work object:
        PeriodicWorkRequest quotesNotiWork = quotesNotiBuilder.setConstraints(quotesConstraints).build();
// Then enqueue the recurring task:
        WorkManager.getInstance().enqueueUniquePeriodicWork(Constants.QUOTES_WORKER, ExistingPeriodicWorkPolicy.KEEP, quotesNotiWork);
        // show suggest alarm dialog at 21:00
        long initialDelay = 24 + 21 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 24;
        OneTimeWorkRequest suggestAlarm =
                new OneTimeWorkRequest.Builder(SuggestAlarmHelper.class).setInitialDelay(initialDelay, TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance().enqueue(suggestAlarm);


    }


}

