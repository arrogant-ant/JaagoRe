package iris.jaagore.sabita_sant.alarm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Jaago re");
        setSupportActionBar(toolbar);
        setCustomClock();
        setNavigationDrawer();

    }



    private void setCustomClock() {
        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(0.5f);
        customAnalogClock.init(this,R.drawable.clock_face,R.drawable.hour_hand,R.drawable.mins_hand,0,false,true);
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



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }
}
