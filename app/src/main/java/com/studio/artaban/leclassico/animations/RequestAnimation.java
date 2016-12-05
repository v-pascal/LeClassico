package com.studio.artaban.leclassico.animations;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/12/16.
 * Request old animation (for recycler view)
 */
public class RequestAnimation {

    private AnimatorSet mRequestAnim1;
    private AnimatorSet mRequestAnim2;
    private AnimatorSet mRequestAnim3;
    // Animations (from resources)

    public RequestAnimation(Context context) {
        Logs.add(Logs.Type.V, "context: " + context);

        mRequestAnim1 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.request_old);
        mRequestAnim2 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.request_old);
        mRequestAnim3 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.request_old);
    }

    //////
    public void display(Resources resources, RecyclerAdapter adapter, View requestView) {
        Logs.add(Logs.Type.V, "resources: " + resources + ";adapter: " + adapter + ";request: " + requestView);

        if (mRequestAnim1 != null) {
            mRequestAnim1.removeAllListeners();
            mRequestAnim1.end();
            mRequestAnim1.cancel();
        }
        if (mRequestAnim2 != null) {
            mRequestAnim2.removeAllListeners();
            mRequestAnim2.end();
            mRequestAnim2.cancel();
        }
        if (mRequestAnim3 != null) {
            mRequestAnim3.removeAllListeners();
            mRequestAnim3.end();
            mRequestAnim3.cancel();
        }
        View request = requestView.findViewById(R.id.layout_more);
        if (!adapter.isRequesting()) {

            request.setBackground(resources.getDrawable(R.drawable.select_more_background));
            request.setOnClickListener(adapter);

            View image1 = requestView.findViewById(R.id.image_1);
            image1.clearAnimation();
            image1.setAlpha(1);
            image1.setScaleX(1);
            image1.setScaleY(1);

            View image2 = requestView.findViewById(R.id.image_2);
            image2.clearAnimation();
            image2.setAlpha(1);
            image2.setScaleX(1);
            image2.setScaleY(1);

            View image3 = requestView.findViewById(R.id.image_3);
            image3.clearAnimation();
            image3.setAlpha(1);
            image3.setScaleX(1);
            image3.setScaleY(1);

        } else {
            request.setBackground(null);
            request.setOnClickListener(null);

            // Start requesting old entries
            mRequestAnim1.setTarget(requestView.findViewById(R.id.image_1));
            mRequestAnim1.start();

            long delay = resources.getInteger(R.integer.duration_request_anim) / 3;
            mRequestAnim2.setStartDelay(delay);
            mRequestAnim2.setTarget(requestView.findViewById(R.id.image_2));
            mRequestAnim2.start();

            mRequestAnim3.setStartDelay(delay << 1);
            mRequestAnim3.setTarget(requestView.findViewById(R.id.image_3));
            mRequestAnim3.start();
        }
    }
}
