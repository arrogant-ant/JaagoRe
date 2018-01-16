package iris.jaagore.sabita_sant.alarm;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

public class AddAlarm
        extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    long ALARM_TIME;
    static String alarmText;
    static boolean repeat = false;
    static int snooze;
    Calendar calendar = Calendar.getInstance();
    Switch repeat_sw, status_sw;
    private Spinner snooze_sp;
    private ConnectivityReceiver receiver;
    private TimePicker timePicker;
    Button submit_bt;
    Alarm alarm;
    AlarmHelper alarmHelper;
    IntentFilter intentFilter;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_alarm);

        //registering broadcast
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver=new ConnectivityReceiver();

        //adding toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //adding nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //ad setup
        MobileAds.initialize(this,getString(R.string.appID_ad));
        adView= (AdView) findViewById(R.id.ad_add_alarm);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        timePicker = (TimePicker) findViewById(R.id.timePicker);

        alarm = new Alarm(AddAlarm.this);
        alarmHelper = new AlarmHelper(AddAlarm.this);
        alarmText = alarm.getAlarmText();
        this.repeat_sw = (Switch) findViewById(R.id.repeat);
        status_sw = (Switch) findViewById(R.id.status_sw);
        setupSnoozeSpinner();
        status_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    alarmHelper.stopAlarm();
                    Toast.makeText(AddAlarm.this,"Alarm Disabled",Toast.LENGTH_SHORT).show();
                } else {
                    alarmHelper.setPrevious();
                }

            }
        });
        repeat_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    alarmHelper.setRepeat(true);
                } else
                    alarmHelper.setRepeat(false);

            }
        });

        Typeface heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        submit_bt = (Button) findViewById(R.id.set_button);
        submit_bt.setTypeface(heading);

    }


    private void setupSnoozeSpinner() {
        snooze = 1;
        snooze_sp = (Spinner) findViewById(R.id.snooze);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddAlarm.this, R.array.snooze_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snooze_sp.setAdapter(adapter);
        snooze_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        snooze = 2;
                        break;
                    case 1:
                        snooze = 5;
                        break;
                    case 2:
                        snooze = 10;
                        break;
                    case 3:
                        snooze = 15;
                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snooze = 1;
            }

        });
    }

    //initialize recent alarm
    private void recentAlarm() {
        int pos = 0;

        long recent_time = alarm.getNextAlarm();
        boolean recent_repeat = alarm.isRepeat();
        int recent_snooze = alarm.getSnoozeTime();

        TextView recent_tx = (TextView) findViewById(R.id.recentAlarm);

        switch (recent_snooze) {
            case 2:
                pos = 0;
                break;
            case 5:
                pos = 1;
                break;
            case 10:
                pos = 2;
                break;
            case 15:
                pos = 3;
                break;


        }
        if (recent_time != 0) {
            alarmText = alarm.getAlarmText();
            recent_tx.setText(alarmText);
            status_sw.setChecked(alarm.isActive());
            repeat_sw.setChecked(recent_repeat);
            snooze_sp.setSelection(pos);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        recentAlarm();
        registerReceiver(receiver, intentFilter);
        Log.d("AddAlarm", "resumed");
    }

    public void setAlarm(View paramView) {

        adView.setVisibility(View.VISIBLE);

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour().intValue());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute().intValue());
        ALARM_TIME = this.calendar.getTimeInMillis();
        if (ALARM_TIME < Calendar.getInstance().getTimeInMillis())
            ALARM_TIME += 24 * 60 * 60000;
        repeat = this.repeat_sw.isChecked();
        alarmHelper.setAlarm(ALARM_TIME, repeat, snooze, true);
        recentAlarm();
    }

    public void info(View view) {
        startActivity(new Intent(AddAlarm.this, Info.class));
    }

    //navigation related functions
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_alarm) {
            // Handle the camera action
            startActivity(new Intent(this, AddAlarm.class));
        } else if (id == R.id.nav_quote) {
            Intent intent=new Intent(this, QuoteScreen.class);
            intent.putExtra("parent",AlarmActivity.AddAlarm);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, Info.class));

        } else if (id == R.id.nav_rate) {
            AppRater.showRateDialog(this);

        }
        else if ((id==R.id.test))
            startActivity(new Intent(this, AlarmScreen.class));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}



