package iris.jaagore.sabita_sant.alarm;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private TextView app,firm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app= (TextView) findViewById(R.id.sp_title);
        firm= (TextView) findViewById(R.id.firm);

        Typeface heading=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        app.setTypeface(heading);
        firm.setTypeface(heading);
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 1.5 seconds
                    sleep(1500);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),AddAlarm.class);
                    startActivity(i);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

    }
}
