package iris.example.sabita_sant.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Created by Sud on 10/22/17.
 */

public class AppRater {
    private final static String APP_NAME="JAAGO Re";
    private final static String APP_PACKAGE="iris.example.sabita_sant.alarm";


    //days interval after which it will prompt in milis
    private final static long DAYS_UNTIL_PROMPT=2*86400000;

    //minimum times before it prompts
    private final static int LAUNCH_UNTIL_PROMPT=3;

    public static void appLaunched(Context context)
    {
        SharedPreferences saved=context.getSharedPreferences("Alarm",Context.MODE_PRIVATE);
        if(saved.getBoolean("dontShow",false))
        {
            return;
        }
        SharedPreferences.Editor editor=saved.edit();
        long launchCount=saved.getLong("launchCount",0)+1;
        editor.putLong("launchCount",launchCount);

        long launchDate=saved.getLong("launchDate",0);
        if(launchDate==0)
        {
            launchDate= System.currentTimeMillis();
            editor.putLong("launchDate",launchDate);

        }
        if(launchCount>LAUNCH_UNTIL_PROMPT)
        {
            if(System.currentTimeMillis()>launchDate+DAYS_UNTIL_PROMPT)
            {
                showRateDialog(context);
            }
        }
        editor.apply();

    }

     static void showRateDialog(final Context context) {
        SharedPreferences saved=context.getSharedPreferences("Alarm",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=saved.edit();

        final Dialog dialog= new Dialog(context);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        String msg="If you are enjoying using "+APP_NAME+", please take a moment to rate us. \nThanks for the support!";
        builder.setTitle("Rate "+APP_NAME)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putBoolean("dontShow",true);
                        editor.apply();
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+APP_PACKAGE)));

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putBoolean("dontShow",true);
                        editor.apply();
                        dialog.dismiss();

                    }
                })
                .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog.dismiss();

                    }
                });
        builder.create();
        builder.show();
    }


}
