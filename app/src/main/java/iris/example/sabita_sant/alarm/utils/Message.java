package iris.example.sabita_sant.alarm.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import iris.example.sabita_sant.alarm.R;

/**
 * Created by Sud on 9/10/18.
 */

public class Message {
    public static Snackbar showSnackbar(Activity activity, View parent, String msg) {
        Snackbar snackbar = Snackbar.make(parent, msg, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.colorSecondary));
        snackbar.show();
        return snackbar;
    }
}
