package iris.jaagore.sabita_sant.alarm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    final String title = "Jaago re";
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        toolbar=appBarLayout.findViewById(R.id.toolbar);
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setCustomClock();
        setNavigationDrawer();


    }


    private void setCustomClock() {
        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(0.4f);
        customAnalogClock.init(this, R.drawable.clock_face, R.drawable.clock_hour_hand, R.drawable.clock_mins_hand, 0, false, false);
    }

    //navigation related functions
    private void setNavigationDrawer() {
        //adding nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_alarm) {
            startActivity(new Intent(this, AddAlarm.class));
        } else if (id == R.id.nav_quote) {
            Intent intent = new Intent(this, QuoteScreen.class);
            intent.putExtra("parent", AlarmActivity.AddAlarm);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, Info.class));

        } else if (id == R.id.nav_rate) {
            AppRater.showRateDialog(this);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void newAlarm(View view) {
        // Handle the camera action
        startActivity(new Intent(this, AddAlarm.class));
    }
}
