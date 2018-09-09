package iris.jaagore.sabita_sant.alarm.logic;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import iris.jaagore.sabita_sant.alarm.R;

/**
 * Created by Sud on 9/10/18.
 */

public class Message {
    public static Snackbar showSnackbar(Activity activity, View parent, String msg) {
        Snackbar snackbar = Snackbar.make(parent, "Alarm removed from list", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.colorSecondary));
        snackbar.show();
        return snackbar;
    }
}
