package iris.example.sabita_sant.alarm.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Sud on 10/2/18.
 */

public class Animatation {
    public static void spin(View view) {
        if (view == null)
            return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.reverse();
        animator.start();
    }
}
