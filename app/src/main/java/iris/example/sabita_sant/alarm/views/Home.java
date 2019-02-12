package iris.example.sabita_sant.alarm.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.util.Calendar;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.controller.AlarmHelper;
import iris.example.sabita_sant.alarm.controller.AlarmListAdapter;
import iris.example.sabita_sant.alarm.controller.AppRater;
import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.utils.AlarmType;
import iris.example.sabita_sant.alarm.utils.Animatation;
import iris.example.sabita_sant.alarm.utils.Utils;

public class Home extends AppCompatActivity implements AlarmListAdapter.UpdateAlarm {

    private static final String TAG = "Home";
    Toolbar toolbar;
    private String homeTitle, addTitle, quoteTitle;
    private String title;
    private CollapsingToolbarLayout collapsingToolbar;
    private FragmentManager fragmentManager;
    private Fragment homeFrag, newAlarmFrag, quoteFrag;
    private FloatingActionButton fab;
    private AppBarLayout appBarLayout;
    private View scrollView;
    private AdView adView;
    private LinearLayout newAlarmFab, quickAlarmFab;
    private boolean isFabOpen;
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ViewCompat.setNestedScrollingEnabled(scrollView, true);
                    fab.setVisibility(View.VISIBLE);
                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorSurface));
                    title = homeTitle;
                    setFragment(homeFrag);
                    return true;
                case R.id.navigation_new_alarm:
                    ViewCompat.setNestedScrollingEnabled(scrollView, true);
                    fab.setVisibility(View.GONE);
                    if (isFabOpen)
                        closeFabMenu();
                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorSurface));
                    title = addTitle;
                    setFragment(newAlarmFrag);
                    return true;
                case R.id.quotes:
                    fab.setVisibility(View.GONE);
                    if (isFabOpen)
                        closeFabMenu();
                    appBarLayout.setExpanded(false);
                    ViewCompat.setNestedScrollingEnabled(scrollView, false);
                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryVariant));
                    title = quoteTitle;
                    setFragment(quoteFrag);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        UIinit();
    }

    private void init() {
        homeFrag = new HomeFragment();
        quoteFrag = new QuoteFragment();
        newAlarmFrag = new NewAlarmFragment();
        fragmentManager = getSupportFragmentManager();
        homeTitle = getResources().getString(R.string.app_name);
        addTitle = getResources().getString(R.string.set_alarm);
        quoteTitle = getResources().getString(R.string.quote_title);
        title = homeTitle;
        AppRater.appLaunched(this);

    }

    private void UIinit() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        appBarLayout = findViewById(R.id.appBar);
        toolbar = appBarLayout.findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        newAlarmFab = findViewById(R.id.new_alarm_fab);
        quickAlarmFab = findViewById(R.id.quick_alarm_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen)
                    closeFabMenu();
                else
                    showFabMenu();
            }
        });
        scrollView = findViewById(R.id.scroll_view);
        //showing homeTitle only when toolbar is collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setCustomClock();
        setFragment(homeFrag);
    }

    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit();
        loadBannerAd();
    }

    private void setCustomClock() {
        CustomAnalogClock customAnalogClock = findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(0.4f);
        customAnalogClock.init(this, R.drawable.clock_face, R.drawable.clock_hour_hand, R.drawable.clock_mins_hand, 0, false, false);
    }


    public void newAlarm(View view) {
        fab.setVisibility(View.GONE);
        closeFabMenu();
        title = addTitle;
        setFragment(newAlarmFrag);
    }

    private void loadBannerAd() {
        RelativeLayout adContainer = findViewById(R.id.banner_ad);
        adView = new AdView(this, "1405894542877981_1405894869544615", AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        adView.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }

    @Override
    public void update(int alarmID) {
        setFragment(newAlarmFrag);
        fragmentManager.executePendingTransactions();
        NewAlarmFragment updateAlarm = (NewAlarmFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_view);
        updateAlarm.loadPreviousAlarm(alarmID);
    }

    private void showFabMenu() {
        isFabOpen = true;
        newAlarmFab.setVisibility(View.VISIBLE);
        quickAlarmFab.setVisibility(View.VISIBLE);
        AnimatorSet showSet = new AnimatorSet();
        ObjectAnimator fabAnim = Animatation.spin(fab);
        ObjectAnimator newAlarmAnim = Animatation.translateY(newAlarmFab, -200f);
        ObjectAnimator quickAlarmAnim = Animatation.translateY(quickAlarmFab, -400f);
        showSet.playTogether(fabAnim, newAlarmAnim, quickAlarmAnim);


    }

    private void closeFabMenu() {
        if (!isFabOpen) {
            return;
        }
        isFabOpen = false;
        AnimatorSet closeSet = new AnimatorSet();
        ObjectAnimator fabAnim = Animatation.spin(fab);
        final ObjectAnimator newAlarmAnim = Animatation.translateY(newAlarmFab, 200f);
        ObjectAnimator quickAlarmAnim = Animatation.translateY(quickAlarmFab, 400f);
        closeSet.playTogether(fabAnim, newAlarmAnim, quickAlarmAnim);
        closeSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (newAlarmFab != null) {
                    newAlarmFab.setVisibility(View.GONE);

                }
                if (quickAlarmFab != null) {
                    quickAlarmFab.setVisibility(View.GONE);

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    public void setQuickAlarm(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View dialog = inflater.inflate(R.layout.dialog_quick_alarm, null);
        final EditText hourEt = dialog.findViewById(R.id.hours_et);
        final EditText minEt = dialog.findViewById(R.id.mins_et);
        Button mins1 = dialog.findViewById(R.id.mins1);
        Button mins5 = dialog.findViewById(R.id.mins5);
        Button mins15 = dialog.findViewById(R.id.mins15);
        mins1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hourEt.setText("00");
                minEt.setText("01");
            }
        });
        mins5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hourEt.setText("00");
                minEt.setText("05");
            }
        });
        mins15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hourEt.setText("00");
                minEt.setText("15");
            }
        });
        builder.setView(dialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mins = minEt.getText().toString().length() > 0 ? minEt.getText().toString() : "0";
                        String hours = hourEt.getText().toString().length() > 0 ? hourEt.getText().toString() : "0";
                        int duration = (Integer.parseInt(hours) * 60 + Integer.parseInt(mins)) * 60000; // in milis
                        long alarmTime = Calendar.getInstance().getTimeInMillis() + duration;
                        Alarm alarm = new Alarm(alarmTime, 2, 0, new boolean[7], true, null, AlarmType.SIMPLE, "Quick Alarm");
                        // store in alarm db
                        AlarmDatabase.getInstance(Home.this).alarmDao().addAlarm(alarm);
                        AlarmHelper helper = new AlarmHelper(Home.this, alarm.getId());
                        helper.setAlarm();
                        //notifying user
                        Utils.showSnackbar(Home.this, Home.this.findViewById(android.R.id.content), "Alarm set at " + Utils.getAlarmText(alarmTime));
                    }
                })
                .setCancelable(true)
                .create()
                .show();

    }
}