package iris.jaagore.sabita_sant.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class Home extends AppCompatActivity {

    Toolbar toolbar;
    private String homeTitle, addTitle, quoteTitle;
    private String title;
    private CollapsingToolbarLayout collapsingToolbar;
    private static final String TAG = "Home";
    private FragmentManager fragmentManager;
    private Fragment homeFrag, newAlarmFrag, quoteFrag;
    private FloatingActionButton fab;
    private AppBarLayout appBarLayout;
    private View scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        UIinit();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Quotes");
        Log.i(TAG, "onCreate: firebase test ");
        Log.i(TAG, "Firebase database: " + myRef.child("0"));

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

    }


    private void UIinit() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        appBarLayout = findViewById(R.id.appBar);
        toolbar = appBarLayout.findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
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

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

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
                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorSurface));
                    title = addTitle;
                    setFragment(newAlarmFrag);
                    return true;
                case R.id.quotes:
                    fab.setVisibility(View.GONE);
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


    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit();

    }

    private void setCustomClock() {
        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(0.4f);
        customAnalogClock.init(this, R.drawable.clock_face, R.drawable.clock_hour_hand, R.drawable.clock_mins_hand, 0, false, false);
    }


    public void newAlarm(View view) {
        fab.setVisibility(View.GONE);
        title = addTitle;
        setFragment(newAlarmFrag);
    }
}
