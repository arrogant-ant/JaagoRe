package iris.example.sabita_sant.alarm.utils;

import android.animation.ObjectAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Sud on 10/2/18.
 */

public class Animatation {
    public static ObjectAnimator spin(View view) {
        if (view == null)
            return new ObjectAnimator();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.reverse();
        animator.start();
        return animator;
    }

    public static ObjectAnimator translateY(View view, float floatY) {
        if (view == null)
            return new ObjectAnimator();
        float distance = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, floatY,
                view.getContext().getResources().getDisplayMetrics()
        );
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, distance);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        return animator;
    }
}
